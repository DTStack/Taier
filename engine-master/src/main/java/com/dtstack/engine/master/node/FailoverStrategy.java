package com.dtstack.engine.master.node;

import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.domain.po.SimpleBatchJobPO;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.WorkNodeService;
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
import java.util.ArrayList;
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

    /**
     * 未完成的job
     */
    private static final List<Integer> UNFINISHED_STATUSES = new ArrayList<>();

    static {
        UNFINISHED_STATUSES.addAll(TaskStatusConstrant.UNSUBMIT_STATUS);
        UNFINISHED_STATUSES.addAll(TaskStatusConstrant.RUNNING_STATUS);
        UNFINISHED_STATUSES.addAll(TaskStatusConstrant.WAIT_STATUS);
        UNFINISHED_STATUSES.addAll(TaskStatusConstrant.SUBMITTING_STATUS);
        UNFINISHED_STATUSES.add(TaskStatus.RESTARTING.getStatus());
    }

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ZkService zkService;

    @Autowired
    private BatchJobDao batchJobDao;

    @Autowired
    private WorkNodeService workNodeService;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Autowired
    private JobGraphBuilderTrigger jobGraphBuilderTrigger;

    @Autowired
    private JobPartitioner jobPartitioner;

    private FaultTolerantDealer faultTolerantDealer = new FaultTolerantDealer();

    private ExecutorService masterNodeDealer;

    private boolean currIsMaster = false;

    private FailoverStrategy() {
        masterNodeDealer = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory("masterNodeDealer"));
    }

    public void setIsMaster(boolean isMaster) {
        if (isMaster && !currIsMaster) {
            currIsMaster = true;

            jobGraphBuilderTrigger.dealMaster(true);
            LOG.warn("---start jobMaster change listener------");

            if (masterNodeDealer.isShutdown()) {
                masterNodeDealer = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(), new CustomThreadFactory("masterNodeDealer"));
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
                LOG.error("----jobGraphChecker error:{}", e);
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
                    faultTolerantRecover(node);
                }
            } catch (Exception e) {
                LOG.error("----faultTolerantRecover error:{}", e);
            }
        }

        public void stop() {
            isRun = false;
        }
    }

    public void faultTolerantRecover(String broker) {
        try {
            //再次判断broker是否alive
            BrokerHeartNode brokerHeart = zkService.getBrokerHeartNode(broker);
            if (brokerHeart.getAlive()) {
                return;
            }

            //节点容灾恢复任务
            LOG.warn("----- nodeAddress:{} 节点容灾任务开始恢复----", broker);
            long startId = 0L;
            while (true) {
                List<SimpleBatchJobPO> jobs = batchJobDao.listSimpleJobByStatusAddress(startId, UNFINISHED_STATUSES, broker);
                if (CollectionUtils.isEmpty(jobs)) {
                    break;
                }
                List<Long> cronJobIds = Lists.newArrayList();
                List<Long> fillJobIds = Lists.newArrayList();
                for (SimpleBatchJobPO batchJob : jobs) {
                    if (EScheduleType.NORMAL_SCHEDULE.getType() == batchJob.getType()) {
                        cronJobIds.add(batchJob.getId());
                    } else {
                        fillJobIds.add(batchJob.getId());
                    }
                    startId = batchJob.getId();
                }
                distributeJobs(cronJobIds, EScheduleType.NORMAL_SCHEDULE.getType());
                distributeJobs(fillJobIds, EScheduleType.NORMAL_SCHEDULE.getType());
            }

            //在迁移任务的时候，可能出现要迁移的节点也宕机了，任务没有正常接收需要再次恢复（由HearBeatCheckListener监控）。
            List<SimpleBatchJobPO> jobs = batchJobDao.listSimpleJobByStatusAddress(0L, UNFINISHED_STATUSES, broker);
            if (CollectionUtils.isNotEmpty(jobs)) {
                zkService.updateSynchronizedLocalBrokerHeartNode(broker, BrokerHeartNode.initNullBrokerHeartNode(), true);
            }

            LOG.warn("----- broker:{} 节点容灾任务结束恢复-----", broker);
        } catch (Exception e) {
            LOG.error("----nodeAddress:{} faultTolerantRecover error:{}", broker, e);
        }
    }

    /**
     * Ps：jobIds  为 batchJob 表的 id 字段（非job_id字段）
     */
    private void distributeJobs(List<Long> jobIds, Integer scheduleType) {
        if (jobIds.isEmpty()) {
            return;
        }

        Iterator<Long> jobIdsIterator = jobIds.iterator();

        //任务多节点分发，每个节点要分发的任务量
        Map<String, List<Long>> nodeJobs = Maps.newHashMap();

        Map<String, Integer> nodeJobSize = computeJobSizeForNode(jobIds.size(), scheduleType);
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            while (nodeSize > 0 && jobIdsIterator.hasNext()) {
                nodeSize--;
                List<Long> nodeJobIds = nodeJobs.computeIfAbsent(nodeAddress, k -> Lists.newArrayList());
                nodeJobIds.add(jobIdsIterator.next());
            }
        }

        sendJobs(nodeJobs);
    }

    private Map<String, Integer> computeJobSizeForNode(int jobSize, int scheduleType) {
        Map<String, Integer> jobSizeInfo = jobPartitioner.computeQueueJobSize(scheduleType, jobSize);
        if (jobSizeInfo == null) {
            //if empty
            List<String> aliveNodes = zkService.getAliveBrokersChildren();
            jobSizeInfo = new HashMap<String, Integer>(aliveNodes.size());
            int size = jobSize / aliveNodes.size() + 1;
            for (String aliveNode : aliveNodes) {
                jobSizeInfo.put(aliveNode, size);
            }
        }
        return jobSizeInfo;
    }

    private void sendJobs(Map<String, List<Long>> nodeJobs) {
        for (Map.Entry<String, List<Long>> nodeEntry : nodeJobs.entrySet()) {
            if (nodeEntry.getValue().isEmpty()) {
                continue;
            }
            batchJobDao.updateNodeAddress(nodeEntry.getKey(), nodeEntry.getValue());

            if (nodeEntry.getKey().equals(environmentContext.getLocalAddress())) {
                workNodeService.masterSendJobs();
                continue;
            }
            LOG.warn("---masterSendJobs node:{} begin------", nodeEntry.getKey());
            HttpSendClient.masterSendJobs(nodeEntry.getKey(), null);
            LOG.warn("---masterSendJobs node:{} end------", nodeEntry.getKey());
        }
    }
}

