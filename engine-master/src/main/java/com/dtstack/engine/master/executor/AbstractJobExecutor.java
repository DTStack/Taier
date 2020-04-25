package com.dtstack.engine.master.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.schedule.common.enums.Restarted;
import com.dtstack.engine.common.enums.SentinelType;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.BatchFlowWorkJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.queue.BatchJobElement;
import com.dtstack.engine.master.queue.JopPriorityQueue;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobErrorInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;
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
    protected ScheduleJobDao scheduleJobDao;

    @Autowired
    protected ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected ScheduleJobService batchJobService;

    @Autowired
    protected JobRichOperator jobRichOperator;

    @Autowired
    protected ScheduleTaskShadeService batchTaskShadeService;

    @Autowired
    protected BatchFlowWorkJobService batchFlowWorkJobService;

    private JopPriorityQueue jopPriorityQueue;

    private Set<String> notStartCache = Sets.newHashSet();
    private Map<String, JobErrorInfo> errorJobCache = Maps.newHashMap();
    private Map<Long, ScheduleTaskShade> taskCache = Maps.newHashMap();

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
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, getScheduleType(), cycStartTime, cycEndTime);
        List<ScheduleBatchJob> listExecJobs = getScheduleBatchJobList(scheduleJobs);

        //添加需要重跑的数据
        List<ScheduleBatchJob> restartJobList = getRestartDataJob();
        listExecJobs.addAll(restartJobList);
        return listExecJobs;
    }

    public abstract Integer getScheduleType();

    public abstract void stop();


    public void recoverOtherNode() {
        //处理其他节点故障恢复时转移而来的数据
        jopPriorityQueue.clearAndAllIngestion();
    }

    @Override
    public void run() {

        long lastRebackId = 0L;

        while (RUNNING.get()) {

            ScheduleJob scheduleJob = null;
            JobCheckRunInfo checkRunInfo = null;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("========= scheduleType:{} take job from queue，before queueSize:{}, blocked:{}  tail:{} =========",
                            getScheduleType(), jopPriorityQueue.getQueueSize(), jopPriorityQueue.isBlocked(), jopPriorityQueue.resetTail());
                }
                //元素全部放到survivor中 重新全量加载
                if (jopPriorityQueue.getQueueSize() == 0) {
                    logger.info("========= scheduleType:{} queue is empty , blocked:{}  tail:{}  survivor size {}=========", getScheduleType(), jopPriorityQueue.getQueueSize(),
                            jopPriorityQueue.isBlocked(),jopPriorityQueue.getSurvivorSize());
                    notStartCache.clear();
                    errorJobCache.clear();
                    taskCache.clear();
                    jopPriorityQueue.clearAndAllIngestion();
                    Thread.sleep(5000);
                }
                BatchJobElement batchJobElement = jopPriorityQueue.takeJob();
                if (logger.isDebugEnabled()) {
                    logger.debug("========= scheduleType:{} take job from queue，after queueSize:{}, blocked:{}  tail:{} =========",
                            getScheduleType(), jopPriorityQueue.getQueueSize(), jopPriorityQueue.isBlocked(), jopPriorityQueue.resetTail());
                }

                ScheduleBatchJob scheduleBatchJob = batchJobElement.getScheduleBatchJob();
                if(Objects.isNull(scheduleBatchJob)){
                    continue;
                }
                scheduleJob = scheduleBatchJob.getScheduleJob();
                Long taskIdUnique = jobRichOperator.getTaskIdUnique(scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());
                ScheduleTaskShade batchTask = this.taskCache.computeIfAbsent(taskIdUnique,
                        k -> batchTaskShadeService.getBatchTaskById(scheduleBatchJob.getTaskId(), scheduleBatchJob.getScheduleJob().getAppType()));
                if (batchTask == null) {
                    String errMsg = JobCheckStatus.NO_TASK.getMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.SUBMITFAILD.getStatus(), errMsg);
                    logger.warn("scheduleType:{} jobId [{}] submit failed for task [{}] already deleted.", getScheduleType(), scheduleBatchJob.getJobId(), scheduleBatchJob.getTaskId());
                    continue;
                }
                //工作流类型特殊处理
                Integer type = batchTask.getTaskType();

                //获取batchJob 最新状态
                Integer status = batchJobService.getStatusById(scheduleBatchJob.getId());
                //未执行完工作流加入
                if (RdosTaskStatus.SUBMITTING.getStatus().equals(status) && (type.intValue() != EScheduleJobType.WORK_FLOW.getVal() && type.intValue() != EScheduleJobType.ALGORITHM_LAB.getVal())) {
                    continue;
                }
                //已经提交过的工作流节点跳过检查
                if ((type.intValue() == EScheduleJobType.WORK_FLOW.getType() || type.intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) && !RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
                    checkRunInfo = JobCheckRunInfo.createCheckInfo(JobCheckStatus.CAN_EXE);
                } else {
                    checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), this.notStartCache, this.errorJobCache, this.taskCache);
                }

                if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
                    if (type.intValue() == EScheduleJobType.WORK_FLOW.getVal() || type.intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
                        if (status.intValue() == RdosTaskStatus.UNSUBMIT.getStatus()) {
                            //提交代码里面会将jobstatus设置为submitting
                            batchJobService.startJob(scheduleBatchJob.getScheduleJob());
                            logger.info("---scheduleType:{} send job:{} to engine.", getScheduleType(), scheduleBatchJob.getJobId());
                        }
                        if (!batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob.getJobId(),scheduleBatchJob.getAppType())) {
                            jopPriorityQueue.putSurvivor(batchJobElement);
                        }
                    } else {
                        batchJobService.startJob(scheduleBatchJob.getScheduleJob());
                    }
                } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH) {
                    notStartCache.clear();
                    errorJobCache.clear();
                    taskCache.clear();
                    jopPriorityQueue.clearAndAllIngestion();
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
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.FAILED.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION) {
                    //上游任务失败
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.PARENTFAILED.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.TASK_PAUSE
                        || checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_FROZEN) {
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.FROZEN.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_CANCELED
                        //过期任务置为取消
                        || checkRunInfo.getStatus() == JobCheckStatus.TIME_OVER_EXPIRE) {
                    String errMsg = checkRunInfo.getErrMsg();
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.KILLED.getStatus(), errMsg);
                } else if (checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT) {
                    //当前任务状态为未提交状态--直接移除
                } else {
                    jopPriorityQueue.putSurvivor(batchJobElement);
                    //其他情况跳过,等待下次执行
                }
            } catch (Exception e) {
                logger.error("happens error:", e);
                try {
                    if (scheduleJob != null) {
                        batchJobService.updateStatusAndLogInfoById(scheduleJob.getId(), RdosTaskStatus.SUBMITFAILD.getStatus(), e.getMessage());
                        logger.error("scheduleType:{} job:{} submit failed", getScheduleType(), scheduleJob.getId());
                    }
                } catch (Exception ex) {
                    logger.error("update job {}  status happens error:", scheduleJob.getJobId(), ex);
                }
            } finally {
                logger.warn("========= scheduleType:{} take job {} from queue，before queueSize:{}, blocked:{}  tail:{} checkRunInfo:{} =========",
                        getScheduleType(), Objects.isNull(scheduleJob) ? "" : scheduleJob.getJobId(),
                        jopPriorityQueue.getQueueSize(), jopPriorityQueue.isBlocked(), jopPriorityQueue.resetTail(), JSONObject.toJSONString(checkRunInfo));

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
            Pair<String, String> cycTime = getCycTime();
            outLoop:
            while (true) {
                if (jopPriorityQueue.isBlocked()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("scheduleType:{} nodeAddress:{} Queue Blocked!!!", getScheduleType(), nodeAddress);
                    }
                    break;
                }
                List<ScheduleBatchJob> listExecJobs = this.listExecJob(startId, nodeAddress, cycTime.getLeft(), cycTime.getRight());
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
            if (logger.isDebugEnabled()) {
                logger.debug("scheduleType:{} nodeAddress:{} emitJob2Queue return startId:{}", getScheduleType(), nodeAddress, startId);
            }
        } catch (Exception e) {
            logger.error("scheduleType:{} odeAddress:{} emitJob2Queue error:{}", getScheduleType(), nodeAddress, e);
        }
        return startId;
    }

    protected List<ScheduleBatchJob> getRestartDataJob() {
        int status = RdosTaskStatus.UNSUBMIT.getStatus();
        long loadTime = System.currentTimeMillis();
        Timestamp lasTime = lastRestartJobLoadTime == 0L ? null : new Timestamp(lastRestartJobLoadTime);
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listRestartBatchJobList(getScheduleType(), status, lasTime);
        List<ScheduleBatchJob> scheduleBatchJobs = getScheduleBatchJobList(scheduleJobs);
        lastRestartJobLoadTime = loadTime;
        return scheduleBatchJobs;
    }

    protected List<ScheduleBatchJob> getScheduleBatchJobList(List<ScheduleJob> scheduleJobs) {
        List<ScheduleBatchJob> resultList = Lists.newArrayList();
        for (ScheduleJob scheduleJob : scheduleJobs) {
            ScheduleBatchJob scheduleBatchJob = new ScheduleBatchJob(scheduleJob);
            List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByJobKey(scheduleJob.getJobKey());
            scheduleBatchJob.setJobJobList(scheduleJobJobs);
            resultList.add(scheduleBatchJob);
        }

        return resultList;
    }

    /**
     * CycTimeDayGap 如果为0，则只取当天的调度数据，如果恰好在临界点0点存在上一天的未完成的调度任务，则在下一天会被忽略执行。
     */
    private Pair<String, String> getCycTime() {
        Pair<String, String> cycTime = null;
        if (getScheduleType() == EScheduleType.NORMAL_SCHEDULE.getType()) {
            cycTime = jobRichOperator.getCycTimeLimit();
        } else {
            //补数据没有时间限制
            cycTime = new ImmutablePair<>(null, null);
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
