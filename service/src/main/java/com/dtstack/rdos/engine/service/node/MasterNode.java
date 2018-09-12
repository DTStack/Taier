package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
import com.dtstack.rdos.engine.service.zk.data.BrokerHeartNode;
import com.google.common.collect.Lists;
import com.netflix.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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

    private RdosEngineBatchJobDAO rdosEngineBatchJobDao = new RdosEngineBatchJobDAO();

    private RdosEngineStreamJobDAO rdosEngineStreamJobDao = new RdosEngineStreamJobDAO();

    private FaultTolerantDealer faultTolerantDealer = new FaultTolerantDealer();

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
            if (faultTolerantDealer!=null){
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

        public void stop(){
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
            List<RdosEngineJobCache> jobCaches = engineJobCacheDao.getJobForPriorityQueue(broker, EJobCacheStage.IN_PRIORITY_QUEUE.getStage());
            if (CollectionUtils.isEmpty(jobCaches)) {
                return;
            }
            LOG.info("----- broker:{} 节点容灾任务开始恢复----", broker);
            List<JobClient> jobClients = new ArrayList<>(jobCaches.size());
            jobCaches.forEach(jobCache -> {
                try {
                    ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
                    JobClient jobClient = new JobClient(paramAction);
                    jobClients.add(jobClient);
                } catch (Exception e) {
                    //数据转换异常--打日志
                    LOG.error("", e);
                    dealSubmitFailJob(jobCache.getJobId(), jobCache.getComputeType(), "该任务存储信息异常,无法转换." + e.toString());
                }
            });
            for (JobClient jobClient : jobClients) {
                //进行多节点任务分发（ fixme 如果存在吞吐量问题，改成批量，只分发taskId，再由各个节点从数据库恢复）
                WorkNode.getInstance().addStartJob(jobClient);
            }
            List<String> shards = zkDistributed.getBrokerDataChildren(broker);
            for (String shard : shards) {
                zkDistributed.synchronizedBrokerDataShard(broker, shard, BrokerDataShard.initBrokerDataShard(), true);
            }
            LOG.info("----- broker:{} 节点容灾任务结束恢复-----", broker);
        } catch (Exception e) {
            LOG.error("----broker:{} faultTolerantRecover error:{}", broker, e);
        } finally {
            zkDistributed.releaseLock(locks);
        }
    }

    /**
     * master 节点分发任务失败
     */
    private void dealSubmitFailJob(String taskId, Integer computeType, String errorMsg) {
        engineJobCacheDao.deleteJob(taskId);
        if (ComputeType.BATCH.typeEqual(computeType)) {
            rdosEngineBatchJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));
        } else if (ComputeType.STREAM.typeEqual(computeType)) {
            rdosEngineStreamJobDao.submitFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), generateErrorMsg(errorMsg));
        } else {
            LOG.error("not support compute type:" + computeType);
        }
    }

    private String generateErrorMsg(String msgInfo) {
        return String.format("{\"msg_info\":\"%s\"}", msgInfo);
    }
}
