package com.dtstack.engine.master;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class MasterNode {

    private static final Logger LOG = LoggerFactory.getLogger(MasterNode.class);

    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineJobDao rdosEngineBatchJobDao;

    private FaultTolerantDealer faultTolerantDealer = new FaultTolerantDealer();

    private ExecutorService faultTolerantExecutor;

    private boolean currIsMaster = false;

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
                    //TODO, 重写engine job cache的故障恢复，与schedule现有的 FailoverStrategy 兼容
//                    faultTolerantRecover(node);
                }
            } catch (Exception e) {
                LOG.error("----load data from DB error:{}", e);
            }
        }

        public void stop() {
            isRun = false;
        }
    }

//    public void faultTolerantRecover(String broker) {
//        List<InterProcessMutex> locks = null;
//        try {
//            //获取锁
//            locks = zkService.acquireBrokerLock(Lists.newArrayList(broker), true);
//            //再获取锁后再次判断broker是否alive
//            BrokerHeartNode brokerHeart = zkService.getBrokerHeartNode(broker);
//            if (brokerHeart.getAlive()) {
//                //broker可能在获取锁的窗口期间，先获得了锁，进行了数据恢复
//                return;
//            }
//            //节点容灾恢复任务
//            LOG.warn("----- broker:{} 节点容灾任务开始恢复----", broker);
//            long startId = 0L;
//            while (true) {
//                List<EngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(startId, broker, null, null);
//                if (CollectionUtils.isEmpty(jobCaches)) {
//                    break;
//                }
//                Map<String, Map<String, List<String>>> priorityEngineTypes = Maps.newHashMap();
//                List<String> submitedJobs = Lists.newArrayList();
//                for (EngineJobCache jobCache : jobCaches) {
//                    try {
//                        ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
//                        JobClient jobClient = new JobClient(paramAction);
//                        Map<String, List<String>> groups = null;
//                        if (jobCache.getStage() == EJobCacheStage.DB.getStage()) {
//                            groups = priorityEngineTypes.computeIfAbsent(jobCache.getEngineType(), k -> Maps.newHashMap());
//                            List<String> jobIds = groups.computeIfAbsent(jobClient.getGroupName(), k -> Lists.newArrayList());
//                            jobIds.add(jobCache.getJobId());
//                        } else {
//                            submitedJobs.add(jobCache.getJobId());
//                        }
//                        startId = jobCache.getId();
//                    } catch (Exception e) {
//                        //数据转换异常--打日志
//                        LOG.error("", e);
//                        dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "This task stores information exception and cannot be converted." + e.toString());
//                    }
//                }
//                distributeQueueJobs(priorityEngineTypes);
//                distributeZkJobs(submitedJobs);
//            }
//            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收
//            List<EngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(0L, broker, null, null);
//            if (CollectionUtils.isNotEmpty(jobCaches)) {
//                //如果尚有任务未迁移完成，重置 broker 继续恢复
//                zkService.updateSynchronizedLocalBrokerHeartNode(broker, BrokerHeartNode.initNullBrokerHeartNode(), true);
//            }
//            List<String> shards = zkService.getBrokerDataChildren(broker);
//            for (String shard : shards) {
//                zkService.synchronizedBrokerDataShard(broker, shard, BrokerDataShard.initBrokerDataShard(), true);
//            }
//            LOG.warn("----- broker:{} 节点容灾任务结束恢复-----", broker);
//        } catch (Exception e) {
//            LOG.error("----broker:{} faultTolerantRecover error:{}", broker, e);
//        } finally {
//            zkService.releaseLock(locks);
//        }
//    }
//
//    private void distributeQueueJobs(Map<String, Map<String, List<String>>> engineTypes) {
//        if (engineTypes.isEmpty()) {
//            return;
//        }
//        //任务多节点分发，每个节点要分发的任务量
//        Map<String, List<String>> nodeJobs = Maps.newHashMap();
//        for (Map.Entry<String, Map<String, List<String>>> engineTypeEntry : engineTypes.entrySet()) {
//            String engineType = engineTypeEntry.getKey();
//            Map<String, List<String>> groups = engineTypeEntry.getValue();
//            for (Map.Entry<String, List<String>> groupEntry : groups.entrySet()) {
//                List<String> jobIds = groupEntry.getValue();
//                if (jobIds.isEmpty()) {
//                    continue;
//                }
//                String group = groupEntry.getKey();
//                Iterator<String> jobIdsIt = groupEntry.getValue().iterator();
//                Map<String, Integer> jobSizeInfo = computeQueueJobSize(engineType, group, jobIds.size());
//                if (jobSizeInfo == null) {
//                    continue;
//                }
//                for (Map.Entry<String, Integer> jobSizeEntry : jobSizeInfo.entrySet()) {
//                    int size = jobSizeEntry.getValue();
//                    String node = jobSizeEntry.getKey();
//                    while (size > 0 && jobIdsIt.hasNext()) {
//                        size--;
//                        String jobId = jobIdsIt.next();
//                        List<String> nodeJobIds = nodeJobs.computeIfAbsent(node, k -> Lists.newArrayList());
//                        nodeJobIds.add(jobId);
//                    }
//                }
//            }
//        }
//        sendJobs(nodeJobs);
//    }
//
//    private Map<String, Integer> computeQueueJobSize(String engineType, String groupName, int jobSize) {
//        if (clusterQueueInfo.isEmpty()) {
//            return null;
//        }
//        JobPartitioner.EngineTypeQueueInfo engineTypeQueueInfo = clusterQueueInfo.getEngineTypeQueueInfo(engineType);
//        if (engineTypeQueueInfo == null) {
//            return null;
//        }
//        Map<String, Integer> nodeSort = Maps.newHashMap();
//        int total = jobSize;
//        for (Map.Entry<String, JobPartitioner.GroupQueueInfo> engineTypeEntry : engineTypeQueueInfo.getGroupQueueInfoMap().entrySet()) {
//            JobPartitioner.GroupQueueInfo groupEntry = engineTypeEntry.getValue();
//            Map<String, GroupInfo> remoteQueueInfo = groupEntry.getGroupInfo();
//            GroupInfo groupInfo = remoteQueueInfo.getOrDefault(groupName, new GroupInfo());
//            total += groupInfo.getSize();
//            nodeSort.put(engineTypeEntry.getKey(), groupInfo.getSize());
//        }
//        int avg = total / nodeSort.size() + 1;
//        for (Map.Entry<String, Integer> entry : nodeSort.entrySet()) {
//            entry.setValue(avg - entry.getValue());
//        }
//        return nodeSort;
//    }
//
//    private void distributeZkJobs(List<String> jobs) {
//        if (jobs.isEmpty()) {
//            return;
//        }
//        Map<String, Integer> zkDataCache = zkService.getAliveBrokerShardSize();
//        if (zkDataCache == null) {
//            return;
//        }
//        int total = jobs.size();
//        Map<String, Integer> nodeSort = Maps.newHashMap();
//        for (Map.Entry<String, Integer> entry : zkDataCache.entrySet()) {
//            total += entry.getValue();
//            nodeSort.put(entry.getKey(), entry.getValue());
//        }
//        int avg = total / nodeSort.size() + 1;
//        //任务多节点分发，每个节点要分发的任务量
//        Map<String, List<String>> nodeJobs = Maps.newHashMap();
//        Iterator<String> jobsIt = jobs.iterator();
//        for (Map.Entry<String, Integer> entry : nodeSort.entrySet()) {
//            int size = avg - entry.getValue();
//            if (size > 0 && jobsIt.hasNext()) {
//                size--;
//                String jobId = jobsIt.next();
//                List<String> nodeJobIds = nodeJobs.computeIfAbsent(entry.getKey(), k -> Lists.newArrayList());
//                nodeJobIds.add(jobId);
//            }
//        }
//        sendJobs(nodeJobs);
//    }
//
//    private void sendJobs(Map<String, List<String>> nodeJobs) {
//        for (Map.Entry<String, List<String>> nodeEntry : nodeJobs.entrySet()) {
//            if (nodeEntry.getValue().isEmpty()) {
//                continue;
//            }
////            if (nodeEntry.getKey().equals(zkService.getLocalAddress())){
////                workNode.masterSendSubmitJob(nodeEntry.getValue());
////                continue;
////            }
//            Map<String, Object> params = new HashMap<>(1);
//            params.put("jobIds", nodeEntry.getValue());
//            HttpSendClient.masterSendJobs(nodeEntry.getKey(), params);
//        }
//    }

    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg){
        engineJobCacheDao.delete(taskId);
        rdosEngineBatchJobDao.jobFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(errorMsg));
    }

}
