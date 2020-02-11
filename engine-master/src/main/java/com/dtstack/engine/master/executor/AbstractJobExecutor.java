package com.dtstack.engine.master.executor;

import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.Restarted;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.sql.Twins;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.SentinelType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.dao.BatchJobJobDao;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchJobJob;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.impl.BatchFlowWorkJobService;
import com.dtstack.engine.master.impl.BatchJobService;
import com.dtstack.engine.master.impl.BatchTaskShadeService;
import com.dtstack.engine.master.queue.BatchJobElement;
import com.dtstack.engine.master.queue.JopPriorityQueue;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobErrorInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.zk.ZkService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 生产者-消费者模式，考虑三种job的处理情况：
 * 1. 正常的调度任务和补数据任务的处理
 * 2. 其他节点宕机后故障恢复的数据处理
 * 3. JobGraphBuilder 执行时是否能够被正常处理（master 重启时会触发一次，如果能执行则会构建 jobgraph）
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
public abstract class AbstractJobExecutor implements InitializingBean, Runnable {

    private final Logger logger = LoggerFactory.getLogger(AbstractJobExecutor.class);

    @Autowired
    private EnvironmentContext env;

    @Autowired
    protected ZkService zkService;

    @Autowired
    protected BatchJobDao batchJobDao;

    @Autowired
    protected BatchJobJobDao batchJobJobDao;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected BatchJobService batchJobService;

    @Autowired
    protected JobRichOperator jobRichOperator;

    @Autowired
    protected BatchTaskShadeService batchTaskShadeService;

    @Autowired
    protected BatchFlowWorkJobService batchFlowWorkJobService;

    private JopPriorityQueue jopPriorityQueue;

    private Set<String> notStartCache = Sets.newHashSet();
    private Map<String, JobErrorInfo> errorJobCache = Maps.newHashMap();
    private Map<Long, BatchTaskShade> taskCache = Maps.newHashMap();

    private long lastCheckLoadedDay = 0;

    protected final AtomicBoolean RUNNING = new AtomicBoolean(true);
    private volatile long lastRestartJobLoadTime = 0L;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getScheduleType(), env.getAcquireQueueJobInterval(), env.getQueueSize());

        this.jopPriorityQueue = new JopPriorityQueue(
                getScheduleType(),
                env.getAcquireQueueJobInterval(),
                env.getQueueSize(),
                (groupPriorityQueue, startId) -> {
                    return this.emitJob2Queue(groupPriorityQueue, startId);
                });
    }

    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, String cycStartTime, String cycEndTime) {
        List<BatchJob> batchJobs = batchJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, getScheduleType(), cycStartTime, cycEndTime);
        List<ScheduleBatchJob> listExecJobs = getScheduleBatchJobList(batchJobs);

        //添加需要重跑的数据
        List<ScheduleBatchJob> restartJobList = getRestartDataJob();
        listExecJobs.addAll(restartJobList);
        return listExecJobs;
    }

    public abstract Integer getScheduleType();

    public abstract void stop();

    public void operateAfterSentinel(SentinelType sentinelType) throws Exception {
        if (sentinelType.isEndDb()) {
            notStartCache.clear();
            errorJobCache.clear();
            taskCache.clear();
        }
        jopPriorityQueue.resetTail();
        jopPriorityQueue.reback(sentinelType);
    }

    public void recoverOtherNode() {
        //处理其他节点故障恢复时转移而来的数据
        jopPriorityQueue.clearAndAllIngestion();
    }

    @Override
    public void run() {

        long lastRebackId = 0L;

        while (RUNNING.get()) {

            BatchJob batchJob = null;
            try {
                BatchJobElement batchJobElement = jopPriorityQueue.takeJob();
                if (batchJobElement.isSentinel()) {
                    //判断哨兵，执行的操作
                    operateAfterSentinel(batchJobElement.getSentinel());
                    if (logger.isInfoEnabled()) {
                        logger.info("========= scheduleType:{} operateAfterSentinel end=========", getScheduleType());
                    }
                    continue;
                }

                ScheduleBatchJob scheduleBatchJob = batchJobElement.getScheduleBatchJob();
                batchJob = scheduleBatchJob.getBatchJob();

                Long taskIdUnique = jobRichOperator.getTaskIdUnique(scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());
                BatchTaskShade batchTask = this.taskCache.computeIfAbsent(taskIdUnique,
                        k -> batchTaskShadeService.getBatchTaskById(scheduleBatchJob.getTaskId(), scheduleBatchJob.getBatchJob().getAppType()));
                if (batchTask == null) {
                    String errMsg = JobCheckStatus.NO_TASK.getMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), TaskStatus.SUBMITFAILD.getStatus(), errMsg);
                    logger.warn("scheduleType:{} jobId [{}] submit failed for task [{}] already deleted.", getScheduleType(), scheduleBatchJob.getJobId(), scheduleBatchJob.getTaskId());
                    continue;
                }
                //工作流类型特殊处理
                Integer type = batchTask.getTaskType();

                //获取batchJob 最新状态
                Integer status = batchJobService.getStatusById(scheduleBatchJob.getId());
                //未执行完工作流加入
                if (TaskStatus.SUBMITTING.getStatus().equals(status) && (type.intValue() != EJobType.WORK_FLOW.getVal() && type.intValue() != EJobType.ALGORITHM_LAB.getVal())) {
                    continue;
                }
                JobCheckRunInfo checkRunInfo;
                //已经提交过的工作流节点跳过检查
                if ((type.intValue() == EJobType.WORK_FLOW.getVal() || type.intValue() == EJobType.ALGORITHM_LAB.getVal()) && !TaskStatus.UNSUBMIT.getStatus().equals(status)) {
                    checkRunInfo = JobCheckRunInfo.createCheckInfo(JobCheckStatus.CAN_EXE);
                } else {
                    checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), this.notStartCache, this.errorJobCache, this.taskCache);
                }

                if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
                    if (type.intValue() == EJobType.WORK_FLOW.getVal() ||
                            type.intValue() == EJobType.ALGORITHM_LAB.getVal()) {
                        if (status.intValue() == TaskStatus.UNSUBMIT.getStatus()) {
                            //提交代码里面会将jobstatus设置为submitting
                            batchJobService.startJob(scheduleBatchJob.getBatchJob());
                            logger.info("---scheduleType:{} send job:{} to engine.", getScheduleType(), scheduleBatchJob.getJobId());
                        }
                        if (!batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob.getJobId())) {
                            jopPriorityQueue.putSurvivor(batchJobElement);
                        }
                    } else {
                        batchJobService.startJob(scheduleBatchJob.getBatchJob());
                    }
                } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH) {
                    jopPriorityQueue.putSurvivor(batchJobElement);
                    //队列是按时间排序的.当前时间未到之后的也可以忽略
                    jopPriorityQueue.reback(SentinelType.NONE);
                    long rebackId = batchJobElement.getScheduleBatchJob().getId();
                    if (logger.isInfoEnabled()) {
                        logger.info("reback job.id:{} lastRebackId:{}", rebackId, lastRebackId);
                    }
                    if (lastRebackId == rebackId) {
                        if (logger.isInfoEnabled()) {
                            logger.info("reback == lastRebackId, sleep 5 second.");
                        }
                        Thread.sleep(5000);
                    }
                    lastRebackId = rebackId;
                } else if (checkRunInfo.getStatus() == JobCheckStatus.NO_TASK
                        || checkRunInfo.getStatus() == JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION
                        || checkRunInfo.getStatus() == JobCheckStatus.TASK_DELETE
                        || checkRunInfo.getStatus() == JobCheckStatus.FATHER_NO_CREATED) {
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), TaskStatus.FAILED.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION) {
                    //上游任务失败
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), TaskStatus.PARENTFAILED.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.TASK_PAUSE
                        || checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_FROZEN) {
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), TaskStatus.FROZEN.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_CANCELED
                        //过期任务置为取消
                        || checkRunInfo.getStatus() == JobCheckStatus.TIME_OVER_EXPIRE) {
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), TaskStatus.KILLED.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT) {
                    //当前任务状态为未提交状态--直接移除
                } else {
                    jopPriorityQueue.putSurvivor(batchJobElement);
                    //其他情况跳过,等待下次执行
                }
            } catch (Exception e) {
                logger.error("happens error:", e);
                if (batchJob != null) {
                    batchJobService.updateStatusAndLogInfoById(batchJob.getId(), TaskStatus.SUBMITFAILD.getStatus(), e.getMessage());
                    logger.error("scheduleType:{} job:{} submit failed", getScheduleType(), batchJob.getId());
                }
            }
        }
    }

    private Long emitJob2Queue(JopPriorityQueue jopPriorityQueue, long startId) {
        String nodeAddress = zkService.getLocalAddress();
        if (StringUtils.isBlank(nodeAddress)) {
            return startId;
        }
        try {
            //限制数据范围
            Twins<String, String> cycTime = getCycTime();
            outLoop:
            while (true) {
                if (jopPriorityQueue.isBlocked()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("scheduleType:{} nodeAddress:{} Queue Blocked!!!", getScheduleType(), nodeAddress);
                    }
                    break;
                }
                List<ScheduleBatchJob> listExecJobs = this.listExecJob(startId, nodeAddress, cycTime.getKey(), cycTime.getType());
                if (CollectionUtils.isEmpty(listExecJobs)) {
                    //遍历数据库结束的哨兵
                    jopPriorityQueue.putSentinel(SentinelType.END_DB);
                    if (logger.isInfoEnabled()) {
                        logger.info("scheduleType:{} nodeAddress:{} add END_DB Sentinel!!!", getScheduleType(), nodeAddress);
                    }
                    break;
                }
                for (ScheduleBatchJob scheduleBatchJob : listExecJobs) {
                    boolean put = jopPriorityQueue.putJob(scheduleBatchJob);
                    if (!put) {
                        //阻塞时的哨兵
                        jopPriorityQueue.putSentinel(SentinelType.END_QUEUE);
                        if (logger.isInfoEnabled()) {
                            logger.info("scheduleType:{} nodeAddress:{} add END_QUEUE Sentinel!!!", getScheduleType(), nodeAddress);
                        }
                        break outLoop;
                    }
                    //重跑任务不记录id
                    if (Restarted.RESTARTED.getStatus() != scheduleBatchJob.getIsRestart()) {
                        startId = scheduleBatchJob.getId();
                    }
                }
            }
            if (logger.isInfoEnabled()) {
                logger.info("scheduleType:{} nodeAddress:{} emitJob2Queue return startId:{}", getScheduleType(), nodeAddress, startId);
            }
        } catch (Exception e) {
            logger.error("scheduleType:{} odeAddress:{} emitJob2Queue error:{}", getScheduleType(), nodeAddress, e);
        }
        return startId;
    }

    protected List<ScheduleBatchJob> getRestartDataJob() {
        int status = TaskStatus.UNSUBMIT.getStatus();
        long loadTime = System.currentTimeMillis();
        Timestamp lasTime = lastRestartJobLoadTime == 0L ? null : new Timestamp(lastRestartJobLoadTime);
        List<BatchJob> batchJobs = batchJobDao.listRestartBatchJobList(getScheduleType(), status, lasTime);
        List<ScheduleBatchJob> scheduleBatchJobs = getScheduleBatchJobList(batchJobs);
        lastRestartJobLoadTime = loadTime;
        return scheduleBatchJobs;
    }

    protected List<ScheduleBatchJob> getScheduleBatchJobList(List<BatchJob> batchJobs) {
        List<ScheduleBatchJob> resultList = Lists.newArrayList();
        for (BatchJob batchJob : batchJobs) {
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(batchJob);
            List<BatchJobJob> batchJobJobs = batchJobJobDao.listByJobKey(batchJob.getJobKey());
            scheduleBatchJob.setJobJobList(batchJobJobs);
            resultList.add(scheduleBatchJob);
        }

        return resultList;
    }

    /**
     * CycTimeDayGap 如果为0，则只取当天的调度数据，如果恰好在临界点0点存在上一天的未完成的调度任务，则在下一天会被忽略执行。
     */
    private Twins<String, String> getCycTime() {
        Twins<String, String> cycTime = null;
        if (getScheduleType() == EScheduleType.NORMAL_SCHEDULE.getType()) {
            cycTime = jobRichOperator.getCycTimeLimit();
        } else {
            //补数据没有时间限制
            cycTime = new Twins<>(null, null);
        }
        return cycTime;
    }

    protected boolean checkLoadedDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long today = calendar.getTime().getTime();
        boolean loaded = lastCheckLoadedDay < today;
        lastCheckLoadedDay = today;
        return loaded;
    }
}
