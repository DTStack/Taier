package com.dtstack.engine.master.failover;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.po.SimpleScheduleJobPO;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.NodeRecoverService;
import com.dtstack.engine.master.queue.JobPartitioner;
import com.dtstack.engine.master.scheduler.JobGraphBuilder;
import com.dtstack.engine.master.scheduler.JobGraphBuilderTrigger;
import com.dtstack.engine.master.send.HttpSendClient;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.engine.master.zookeeper.data.BrokerHeartNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
 * create: 2019/10/22
 */
@Component
public class FailoverStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(FailoverStrategy.class);

    private BlockingQueue<String> queue = new LinkedBlockingDeque<>();

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ZkService zkService;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private NodeRecoverService nodeRecoverService;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private JobGraphBuilderTrigger jobGraphBuilderTrigger;

    @Autowired
    private JobPartitioner jobPartitioner;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobDao rdosEngineBatchJobDao;

    private FaultTolerantDealer faultTolerantDealer = new FaultTolerantDealer();

    private ExecutorService masterNodeDealer;

    private boolean currIsMaster = false;

    private FailoverStrategy() {
        masterNodeDealer = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
    }

    public void setIsMaster(boolean isMaster) {
        if (isMaster && !currIsMaster) {
            currIsMaster = true;

            jobGraphBuilderTrigger.dealMaster(true);
            LOG.warn("---start jobMaster change listener------");

            if (masterNodeDealer.isShutdown()) {
                masterNodeDealer = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName()));
            }
            masterNodeDealer.submit(faultTolerantDealer);
            masterNodeDealer.submit(new JobGraphChecker());
            LOG.warn("---start master node dealer thread------");
        } else if (!isMaster && currIsMaster) {
            currIsMaster = false;

            jobGraphBuilderTrigger.dealMaster(false);
            LOG.warn("---stop jobMaster change listener------");

            if (faultTolerantDealer != null) {
                faultTolerantDealer.stop();
            }
            masterNodeDealer.shutdownNow();
            LOG.warn("---stop master node dealer thread------");
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

    class JobGraphChecker implements Runnable {

        @Override
        public void run() {
            try {
                //判断当天jobGraph是否已经生成
                SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
                String currDayStr = sdfDay.format(Calendar.getInstance().getTime());
                jobGraphBuilder.buildTaskJobGraph(currDayStr);
            } catch (Exception e) {
                LOG.error("----jobGraphChecker error:", e);
            }
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
                    LOG.warn("----- nodeAddress:{} 节点容灾任务开始恢复----", node);

                    faultTolerantRecoverBatchJob(node);
                    faultTolerantRecoverJobCache(node);

                    List<String> aliveNodes = zkService.getAliveBrokersChildren();
                    for (String nodeAddress : aliveNodes) {
                        LOG.warn("----- nodeAddress:{} masterTriggerNode -----", nodeAddress);
                        if (nodeAddress.equals(environmentContext.getLocalAddress())) {
                            nodeRecoverService.masterTriggerNode();
                            continue;
                        }
                        HttpSendClient.masterTriggerNode(nodeAddress);
                    }
                    LOG.warn("----- nodeAddress:{} 节点容灾任务结束恢复-----", node);
                }
            } catch (Exception e) {
                LOG.error("----faultTolerantRecover error:", e);
            }
        }

        public void stop() {
            isRun = false;
        }
    }

    public void faultTolerantRecoverBatchJob(String nodeAddress) {
        try {
            //再次判断broker是否alive
            BrokerHeartNode brokerHeart = zkService.getBrokerHeartNode(nodeAddress);
            if (brokerHeart.getAlive()) {
                return;
            }

            //节点容灾恢复任务
            LOG.warn("----- nodeAddress:{} BatchJob 任务开始恢复----", nodeAddress);
            long startId = 0L;
            while (true) {
                List<SimpleScheduleJobPO> jobs = scheduleJobDao.listSimpleJobByStatusAddress(startId, RdosTaskStatus.getUnfinishedStatuses(), nodeAddress);
                if (CollectionUtils.isEmpty(jobs)) {
                    break;
                }
                List<Long> cronJobIds = Lists.newArrayList();
                List<Long> fillJobIds = Lists.newArrayList();
                List<Long> phaseStatus = Lists.newArrayList();
                for (SimpleScheduleJobPO batchJob : jobs) {
                    if (EScheduleType.NORMAL_SCHEDULE.getType() == batchJob.getType()) {
                        cronJobIds.add(batchJob.getId());
                    } else {
                        fillJobIds.add(batchJob.getId());
                    }

                    if (JobPhaseStatus.JOIN_THE_TEAM.getCode().equals(batchJob.getPhaseStatus())) {
                        phaseStatus.add(batchJob.getId());
                    }
                    startId = batchJob.getId();
                }
                distributeBatchJobs(cronJobIds, EScheduleType.NORMAL_SCHEDULE.getType());
                distributeBatchJobs(fillJobIds, EScheduleType.FILL_DATA.getType());
                updatePhaseStatus(phaseStatus);
            }

            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收需要再次恢复（由HearBeatCheckListener监控）。
            List<SimpleScheduleJobPO> jobs = scheduleJobDao.listSimpleJobByStatusAddress(0L, RdosTaskStatus.getUnfinishedStatuses(), nodeAddress);
            if (CollectionUtils.isNotEmpty(jobs)) {
                zkService.updateSynchronizedLocalBrokerHeartNode(nodeAddress, BrokerHeartNode.initNullBrokerHeartNode(), true);
            }

            LOG.warn("----- nodeAddress:{} BatchJob 任务结束恢复-----", nodeAddress);
        } catch (Exception e) {
            LOG.error("----nodeAddress:{} faultTolerantRecoverBatchJob error:", nodeAddress, e);
        }
    }

    private void updatePhaseStatus(List<Long> phaseStatus) {
        if (CollectionUtils.isNotEmpty(phaseStatus)) {
            scheduleJobDao.updateListPhaseStatus(phaseStatus, JobPhaseStatus.CREATE.getCode());
        }
    }

    /**
     * Ps：jobIds  为 batchJob 表的 id 字段（非job_id字段）
     */
    private void distributeBatchJobs(List<Long> jobIds, Integer scheduleType) {
        if (jobIds.isEmpty()) {
            return;
        }

        Iterator<Long> jobIdsIterator = jobIds.iterator();

        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<Long>> nodeJobs = Maps.newHashMap();

        Map<String, Integer> nodeJobSize = jobPartitioner.computeBatchJobSize(scheduleType, jobIds.size());
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            while (nodeSize > 0 && jobIdsIterator.hasNext()) {
                nodeSize--;
                List<Long> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                nodeJobIds.add(jobIdsIterator.next());
            }
        }

        updateBatchJobs(nodeJobs);
    }

    private void updateBatchJobs(Map<String, List<Long>> nodeJobs) {
        for (Map.Entry<String, List<Long>> nodeEntry : nodeJobs.entrySet()) {
            if (nodeEntry.getValue().isEmpty()) {
                continue;
            }
            scheduleJobDao.updateNodeAddress(nodeEntry.getKey(), nodeEntry.getValue());
            LOG.info("jobIds:{} failover to address:{}", nodeEntry.getValue(), nodeEntry.getKey());
        }
    }

    public void faultTolerantRecoverJobCache(String nodeAddress) {
        try {
            //再次判断broker是否alive
            BrokerHeartNode brokerHeart = zkService.getBrokerHeartNode(nodeAddress);
            if (brokerHeart.getAlive()) {
                return;
            }

            //节点容灾恢复任务
            LOG.warn("----- nodeAddress:{} JobCache 任务开始恢复----", nodeAddress);
            long startId = 0L;
            while (true) {
                List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(startId, nodeAddress, null, null);
                if (CollectionUtils.isEmpty(jobCaches)) {
                    break;
                }
                Map<String, List<String>> jobResources = Maps.newHashMap();
                List<String> submittedJobs = Lists.newArrayList();
                for (EngineJobCache jobCache : jobCaches) {
                    try {
                        if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
                            List<String> jobIds = jobResources.computeIfAbsent(jobCache.getJobResource(), k -> Lists.newArrayList());
                            jobIds.add(jobCache.getJobId());
                        } else {
                            submittedJobs.add(jobCache.getJobId());
                        }
                        startId = jobCache.getId();
                    } catch (Exception e) {
                        //数据转换异常--打日志
                        LOG.error("", e);
                        dealSubmitFailJob(jobCache.getJobId(), "This task stores information exception and cannot be converted." + e.toString());
                    }
                }
                distributeQueueJobs(jobResources);
                distributeSubmittedJobs(submittedJobs);
            }
            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收
            List<EngineJobCache> jobCaches = engineJobCacheDao.listByStage(0L, nodeAddress, null, null);
            if (CollectionUtils.isNotEmpty(jobCaches)) {
                //如果尚有任务未迁移完成，重置 nodeAddress 继续恢复
                zkService.updateSynchronizedLocalBrokerHeartNode(nodeAddress, BrokerHeartNode.initNullBrokerHeartNode(), true);
            }
            LOG.warn("----- nodeAddress:{} JobCache 任务结束恢复-----", nodeAddress);
        } catch (Exception e) {
            LOG.error("----nodeAddress:{} faultTolerantRecoverJobCache error:", nodeAddress, e);
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
            Map<String, Integer> jobCacheSizeInfo = jobPartitioner.computeJobCacheSize(jobResource, jobIds.size());
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
        updateJobCaches(nodeJobs, EJobCacheStage.DB.getStage());
    }

    private void distributeSubmittedJobs(List<String> jobs) {
        if (jobs.isEmpty()) {
            return;
        }
        List<String> aliveNodes = zkService.getAliveBrokersChildren();
        int avg = jobs.size() / aliveNodes.size() + 1;
        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<String>> nodeJobs = Maps.newHashMap();
        Iterator<String> jobsIt = jobs.iterator();
        int size = avg;
        for (String nodeAddress : aliveNodes) {
            while (size > 0 && jobsIt.hasNext()) {
                size--;
                String jobId = jobsIt.next();
                List<String> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                nodeJobIds.add(jobId);
            }
        }
        updateJobCaches(nodeJobs, EJobCacheStage.SUBMITTED.getStage());
    }

    private void updateJobCaches(Map<String, List<String>> nodeJobs, Integer stage) {
        for (Map.Entry<String, List<String>> nodeEntry : nodeJobs.entrySet()) {
            if (nodeEntry.getValue().isEmpty()) {
                continue;
            }

            engineJobCacheDao.updateNodeAddressFailover(nodeEntry.getKey(), nodeEntry.getValue(), stage);
            LOG.info("jobIds:{} failover to address:{}, set stage={}", nodeEntry.getValue(), nodeEntry.getKey(), stage);
        }
    }

    /**
     * master 节点分发任务失败
     * @param taskId
     */
    public void dealSubmitFailJob(String taskId, String errorMsg){
        engineJobCacheDao.delete(taskId);
        rdosEngineBatchJobDao.jobFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(errorMsg));
        LOG.info("jobId:{} update job status:{}, job is finished.", taskId, RdosTaskStatus.SUBMITFAILD.getStatus());
    }
}

