package com.dtstack.engine.service.node;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.service.queue.GroupInfo;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.send.HttpSendClient;
import com.dtstack.engine.service.task.QueueListener;
import com.dtstack.engine.service.zk.ZkDistributed;
import com.dtstack.engine.service.zk.data.BrokerHeartNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/1
 */
public class MasterNode {

    private static final Logger LOG = LoggerFactory.getLogger(MasterNode.class);

    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private FaultTolerantDealer faultTolerantDealer = new FaultTolerantDealer();

    private static WorkNode workNode = WorkNode.getInstance();

    private ExecutorService faultTolerantExecutor;

    private static MasterNode masterNode = new MasterNode();

    private boolean currIsMaster = false;

    public static MasterNode getInstance() {
        return masterNode;
    }

    private MasterNode() {
        faultTolerantExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("faultTolerantDealer"));
    }

    public void setIsMaster(boolean isMaster) {
        if (isMaster && !currIsMaster) {
            currIsMaster = true;
            if (faultTolerantExecutor.isShutdown()) {
                faultTolerantExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(), new CustomThreadFactory("faultTolerantDealer"));
            }
            faultTolerantExecutor.submit(faultTolerantDealer);
            LOG.warn("---start master node deal thread------");
        } else if (!isMaster && currIsMaster) {
            currIsMaster = false;
            if (faultTolerantDealer != null) {
                faultTolerantDealer.stop();
            }
            faultTolerantExecutor.shutdownNow();
            LOG.warn("---stop master node deal thread------");
        }
    }

    public void dataMigration(String node) {
        if (StringUtils.isBlank(node)) {
            return;
        }
        try {
            queue.put(node);
        } catch (InterruptedException e) {
            LOG.error("{}", e);
        }
    }

    /**
     * 容错处理
     */
    class FaultTolerantDealer implements Runnable {

        private volatile boolean isRun = true;

        @Override
        public void run() {
            try {
                while (isRun) {
                    String node = queue.take();
                    faultTolerantRecover(node);
                }
            } catch (Exception e) {
                LOG.error("----load data from DB error:{}", e);
            }
        }

        public void stop() {
            isRun = false;
        }
    }

    public void faultTolerantRecover(String broker) {
        List<InterProcessMutex> locks = null;
        try {
            //获取锁
            locks = zkDistributed.acquireBrokerLock(Lists.newArrayList(broker), true);
            //再获取锁后再次判断broker是否alive
            BrokerHeartNode brokerHeart = zkDistributed.getBrokerHeartNode(broker);
            if (brokerHeart.getAlive()) {
                //broker可能在获取锁的窗口期间，先获得了锁，进行了数据恢复
                return;
            }
            //节点容灾恢复任务
            LOG.warn("----- broker:{} 节点容灾任务开始恢复----", broker);
            long startId = 0L;
            while (true) {
                List<RdosEngineJobCache> jobCaches = engineJobCacheDao.listByNodeAddressStage(startId, broker, null, null, null);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                Map<String, List<String>> jobResources = Maps.newHashMap();
                List<String> submitedJobs = Lists.newArrayList();
                for (RdosEngineJobCache jobCache : jobCaches) {
                    try {
                        if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                            String jobResource = WorkNode.getInstance().getJobResource(jobCache.getEngineType(), jobCache.getGroupName());
                            List<String> jobIds = jobResources.computeIfAbsent(jobResource, k -> Lists.newArrayList());
                            jobIds.add(jobCache.getJobId());
                        } else {
                            submitedJobs.add(jobCache.getJobId());
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        workNode.dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
                distributeQueueJobs(jobResources);
                distributeZkJobs(submitedJobs);
            }
            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收
            List<RdosEngineJobCache> jobCaches = engineJobCacheDao.listByNodeAddressStage(0L, broker, null, null, null);
            if (CollectionUtils.isNotEmpty(jobCaches)) {
                zkDistributed.updateSynchronizedLocalBrokerHeartNode(broker, BrokerHeartNode.initNullBrokerHeartNode(), true);
            }
            LOG.warn("----- broker:{} 节点容灾任务结束恢复-----", broker);
        } catch (Exception e) {
            LOG.error("----broker:{} faultTolerantRecover error:{}", broker, e);
        } finally {
            zkDistributed.releaseLock(locks);
        }
    }

    private void distributeQueueJobs(Map<String, List<String>> jobResources) {
        if (jobResources.isEmpty()) {
            return;
        }
        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<String>> nodeJobs = Maps.newHashMap();
        for (Map.Entry<String, List<String>> jobResourceEntry : jobResources.entrySet()) {
            String jobResource = jobResourceEntry.getKey();
            List<String> jobIds = jobResourceEntry.getValue();
            if (jobIds.isEmpty()) {
                continue;
            }
            Map<String, Integer> jobCacheSizeInfo = computeJobCacheSizeForNode(jobIds.size(), jobResource);
            Iterator<String> jobIdsIterator = jobIds.iterator();
            for (Map.Entry<String, Integer> jobCacheSizeEntry : jobCacheSizeInfo.entrySet()) {
                String nodeAddress = jobCacheSizeEntry.getKey();
                int nodeSize = jobCacheSizeEntry.getValue();
                while (nodeSize > 0 && jobIdsIterator.hasNext()) {
                    nodeSize--;
                    List<String> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                    nodeJobIds.add(jobIdsIterator.next());
                }
            }
        }

        sendJobs(nodeJobs);
    }

    private Map<String, Integer> computeJobCacheSizeForNode(int jobSize, String jobResource) {
        Map<String, Integer> jobSizeInfo = computeJobCacheSize(jobResource, jobSize);
        if (jobSizeInfo == null) {
            //if empty
            List<String> aliveNodes = zkDistributed.getAliveBrokersChildren();
            jobSizeInfo = new HashMap<String, Integer>(aliveNodes.size());
            int size = jobSize / aliveNodes.size() + 1;
            for (String aliveNode : aliveNodes) {
                jobSizeInfo.put(aliveNode, size);
            }
        }
        return jobSizeInfo;
    }


    public Map<String, Integer> computeJobCacheSize(String jobResource, int jobSize) {
        Map<String, Map<String, GroupInfo>> allNodesGroupQueueJobResources = QueueListener.getAllNodesGroupQueueInfo();
        if (allNodesGroupQueueJobResources.isEmpty()) {
            return null;
        }
        Map<String, GroupInfo> nodesGroupQueue = allNodesGroupQueueJobResources.get(jobResource);
        if (nodesGroupQueue == null) {
            return null;
        }
        Map<String, Integer> nodeSort = Maps.newHashMap();
        int total = jobSize;
        for (Map.Entry<String, GroupInfo> groupInfoEntry : nodesGroupQueue.entrySet()) {
            GroupInfo groupInfo = groupInfoEntry.getValue();
            total += groupInfo.getSize();
            nodeSort.put(groupInfoEntry.getKey(), groupInfo.getSize());
        }
        int avg = total / nodeSort.size() + 1;
        for (Map.Entry<String, Integer> entry : nodeSort.entrySet()) {
            entry.setValue(avg - entry.getValue());
        }
        return nodeSort;
    }

    private void distributeZkJobs(List<String> jobs) {
        if (jobs.isEmpty()) {
            return;
        }
        List<String> aliveNodes = zkDistributed.getAliveBrokersChildren();
        int avg = jobs.size() / aliveNodes.size() + 1;
        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<String>> nodeJobs = Maps.newHashMap();
        Iterator<String> jobsIt = jobs.iterator();
        int size = avg;
        for (String nodeAddress : aliveNodes) {
            if (size > 0 && jobsIt.hasNext()) {
                size--;
                String jobId = jobsIt.next();
                List<String> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                nodeJobIds.add(jobId);
            }
        }
        sendJobs(nodeJobs);
    }

    private void sendJobs(Map<String, List<String>> nodeJobs) {
        for (Map.Entry<String, List<String>> nodeEntry : nodeJobs.entrySet()) {
            if (nodeEntry.getValue().isEmpty()) {
                continue;
            }
            if (nodeEntry.getKey().equals(zkDistributed.getLocalAddress())){
                workNode.masterSendSubmitJob(nodeEntry.getValue());
                continue;
            }
            Map<String, Object> params = new HashMap<>(1);
            params.put("jobIds", nodeEntry.getValue());
            HttpSendClient.masterSendJobs(nodeEntry.getKey(), params);
        }
    }

}
