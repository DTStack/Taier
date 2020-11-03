package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.impl.BatchFlowWorkJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.Restarted;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
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

    private ExecutorService executorService;
    protected final AtomicBoolean RUNNING = new AtomicBoolean(true);

    private LinkedBlockingQueue<ScheduleBatchJob> scheduleJobQueue = null;

    public abstract EScheduleType getScheduleType();

    public abstract void stop();

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getScheduleType(), env.getAcquireQueueJobInterval(), env.getQueueSize());

        scheduleJobQueue = new LinkedBlockingQueue<>(env.getQueueSize());

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(getScheduleType() + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(
                this::emitJob2Queue,
                0,
                env.getAcquireQueueJobInterval(),
                TimeUnit.MILLISECONDS);

        String threadName = this.getClass().getSimpleName() + "_" + getScheduleType() + "_startJobProcessor";
        executorService = new ThreadPoolExecutor(env.getJobExecutorPoolCorePoolSize(), env.getJobExecutorPoolMaximumPoolSize(), env.getJobExecutorPoolKeepAliveTime(), TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(env.getJobExecutorPoolQueueSize()),
                new CustomThreadFactory(threadName),
                new CustomThreadRunsPolicy(threadName, getScheduleType().name()));
    }

    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, String cycStartTime, String cycEndTime,Boolean isEq) {
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, getScheduleType().getType(), cycStartTime, cycEndTime, JobPhaseStatus.CREATE.getCode(),isEq);
        List<ScheduleBatchJob> listExecJobs = getScheduleBatchJobList(scheduleJobs);

        //添加需要重跑的数据
        List<ScheduleBatchJob> restartJobList = getRestartDataJob(cycStartTime);
        listExecJobs.addAll(restartJobList);
        return listExecJobs;
    }

    public void recoverOtherNode() {
        //处理其他节点故障恢复时转移而来的数据
    }

    @Override
    public void run() {
        while (RUNNING.get()) {

            ScheduleJob scheduleJob = null;
            try {
                ScheduleBatchJob scheduleBatchJob = scheduleJobQueue.take();
                scheduleJob = scheduleBatchJob.getScheduleJob();

                logger.info("jobId:{} scheduleType:{} take job from queue.", scheduleJob.getJobId(), getScheduleType());
                this.start(scheduleBatchJob);
            } catch (Exception e) {
                logger.error("happens error:", e);
                try {
                    if (scheduleJob != null) {
                        batchJobService.updateStatusAndLogInfoById(scheduleJob.getId(), RdosTaskStatus.SUBMITFAILD.getStatus(), e.getMessage());
                        logger.error("jobId:{} scheduleType:{} submit failed.", scheduleJob.getJobId(), getScheduleType());
                    }
                } catch (Exception ex) {
                    logger.error("jobId:{} scheduleType:{} update status happens error:", scheduleJob.getJobId(), getScheduleType(), ex);
                }
            }
        }
    }


    private void emitJob2Queue() {
        String nodeAddress = zkService.getLocalAddress();
        if (StringUtils.isBlank(nodeAddress)) {
            return;
        }
        try {
            //限制数据范围
            Pair<String, String> cycTime = getCycTime();
            Long startId = batchJobService.getListMinId(nodeAddress, getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight(), null);
            logger.info("scheduleType:{} nodeAddress:{} leftTime:{} rightTime:{} start scanning since when startId:{} .", getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight(), nodeAddress, startId);
            if (startId == null) {
                //周期实例查询为空之后 还需要校验是否存在重跑的数据 否则startId为空  com.dtstack.engine.master.executor.AbstractJobExecutor.listExecJob 不会查询重跑数据 导致重跑任务无法提交
                startId = batchJobService.getListMinId(nodeAddress, getScheduleType().getType(), null, null, Restarted.RESTARTED.getStatus());
                logger.info("scheduleType:{} nodeAddress:{} get isRestart start scanning since when startId:{} .", getScheduleType().getType(), nodeAddress, startId);
            }
            if (startId!=null) {
                List<ScheduleBatchJob> listExecJobs = this.listExecJob(startId, nodeAddress, cycTime.getLeft(), cycTime.getRight(),Boolean.TRUE);
                while (CollectionUtils.isNotEmpty(listExecJobs)) {
                    for (ScheduleBatchJob scheduleBatchJob : listExecJobs) {
                        // 节点检查是否能进入队列
                        try {
                            Long taskIdUnique = jobRichOperator.getTaskIdUnique(scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());
                            ScheduleTaskShade batchTask =  batchTaskShadeService.getBatchTaskById(scheduleBatchJob.getTaskId(), scheduleBatchJob.getScheduleJob().getAppType());
                            Map<Long, ScheduleTaskShade> taskCache = Maps.newHashMap();
                            taskCache.put(taskIdUnique,batchTask);
                            if (batchTask == null) {
                                String errMsg = JobCheckStatus.NO_TASK.getMsg();
                                batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.SUBMITFAILD.getStatus(), errMsg);
                                logger.warn("jobId:{} scheduleType:{} submit failed for taskId:{} already deleted.", scheduleBatchJob.getJobId(), getScheduleType(), scheduleBatchJob.getTaskId());
                                continue;
                            }

                            Integer type = batchTask.getTaskType();
                            Integer status = batchJobService.getStatusById(scheduleBatchJob.getId());

                            JobCheckRunInfo checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), new HashSet<>(), new HashMap<>(), taskCache);
                            if (type.intValue() == EScheduleJobType.WORK_FLOW.getType() || type.intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
                                logger.info("jobId:{} scheduleType:{} is WORK_FLOW or ALGORITHM_LAB so immediate put queue.", scheduleBatchJob.getJobId(), getScheduleType());
                                if (isPutQueue(checkRunInfo, scheduleBatchJob) && RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
                                    putScheduleJob(scheduleBatchJob);
                                } else if(!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)){
                                    logger.info("jobId:{} scheduleType:{} is WORK_FLOW or ALGORITHM_LAB start judgment son is execution complete.", scheduleBatchJob.getJobId(), getScheduleType());
                                    batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob.getId(), scheduleBatchJob.getJobId(), scheduleBatchJob.getAppType());
                                }
                            } else {
                                if (isPutQueue(checkRunInfo, scheduleBatchJob)) {
                                    // 更新job状态
                                    boolean updateStatus = batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.CREATE, JobPhaseStatus.JOIN_THE_TEAM);
                                    if (updateStatus) {
                                        logger.info("jobId:{} scheduleType:{} nodeAddress:{} JobPhaseStatus:{} update success", scheduleBatchJob.getJobId(), getScheduleType(), nodeAddress, JobPhaseStatus.JOIN_THE_TEAM);
                                        putScheduleJob(scheduleBatchJob);
                                    }
                                }
                            }

                            // listExecJobs 如果全是为重跑的任务 会进入死循环 去除是否重跑的判断条件
                            startId = scheduleBatchJob.getId();
                        } catch (Exception e) {
                            logger.error("jobId:{} scheduleType:{} nodeAddress:{} emitJob2Queue error:", scheduleBatchJob.getJobId(), getScheduleType(), nodeAddress, e);
                            Integer status = RdosTaskStatus.FAILED.getStatus();
                            batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), status,e.getMessage());
                        }
                    }
                    listExecJobs = this.listExecJob(startId, nodeAddress, cycTime.getLeft(), cycTime.getRight(),Boolean.FALSE);
                    logger.info("scheduleType:{} nodeAddress:{} leftTime:{} rightTime:{} start scanning since when startId:{} .", getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight(), nodeAddress, startId);
                }
            }
        } catch (Exception e) {
            logger.error("scheduleType:{} nodeAddress:{} emitJob2Queue error:", getScheduleType(), nodeAddress, e);
        }
    }

    private boolean isPutQueue(JobCheckRunInfo checkRunInfo, ScheduleBatchJob scheduleBatchJob) {
        Integer status;
        String errMsg = checkRunInfo.getErrMsg();
        if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
            logger.info("jobId:{} checkRunInfo.status:{} put success queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.TRUE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH
                || checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_SUCCESS
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_NOT_FINISHED
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_FINISHED) {
            logger.info("jobId:{} checkRunInfo.status:{} unable put to queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.NO_TASK
                || checkRunInfo.getStatus() == JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION
                || checkRunInfo.getStatus() == JobCheckStatus.TASK_DELETE
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_NO_CREATED) {
            status = RdosTaskStatus.FAILED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_EXCEPTION) {
            //上游任务失败
            status = RdosTaskStatus.PARENTFAILED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_CANCELED) {
            status = RdosTaskStatus.KILLED.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TASK_PAUSE
                || checkRunInfo.getStatus() == JobCheckStatus.DEPENDENCY_JOB_FROZEN) {
            status = RdosTaskStatus.FROZEN.getStatus();
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_OVER_EXPIRE
                || JobCheckStatus.DEPENDENCY_JOB_EXPIRE.equals(checkRunInfo.getStatus())) {
            //更新为自动取消
            status = RdosTaskStatus.EXPIRE.getStatus();
        } else {
            logger.error("appear unknown jobId:{} checkRunInfo.status:{} ", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        }
        logger.info("jobId:{} checkRunInfo.status:{} errMsg:{} status:{} update status.", scheduleBatchJob.getJobId(), checkRunInfo.getStatus(), errMsg, status);
        batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), status, errMsg);
        return Boolean.FALSE;
    }

    private void putScheduleJob(ScheduleBatchJob scheduleBatchJob) {
        try {
            if (scheduleJobQueue.contains(scheduleBatchJob)) {
                //元素已存在，返回true
                return;
            }
            scheduleJobQueue.put(scheduleBatchJob);
            logger.info("jobId:{} scheduleType:{} enter queue", scheduleBatchJob.getJobId(), getScheduleType());
        } catch (InterruptedException e) {
            logger.error("jobId:{} scheduleType:{} job phase rollback, error", scheduleBatchJob.getJobId(), getScheduleType(), e);
            batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.CREATE);
        }
    }

    protected List<ScheduleBatchJob> getRestartDataJob(String cycStartTime) {
        int status = RdosTaskStatus.UNSUBMIT.getStatus();
        Timestamp lasTime = null;
        if (!StringUtils.isBlank(cycStartTime)) {
            DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                Date parse = sdf.parse(cycStartTime);
                if (null != parse) {
                    lasTime = new Timestamp(parse.getTime());
                }
            } catch (ParseException e) {
                logger.error("getRestartDataJob {} error ",cycStartTime,e);
            }
        }
        if (null == lasTime) {
            lasTime = new Timestamp(DateTime.now().withTime(0,0,0,0).getMillis());
        }
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listRestartBatchJobList(getScheduleType().getType(), status, lasTime);
        return getScheduleBatchJobList(scheduleJobs);
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
        if (getScheduleType().getType() == EScheduleType.NORMAL_SCHEDULE.getType()) {
            cycTime = jobRichOperator.getCycTimeLimitEndNow();
        } else {
            //补数据没有时间限制
            cycTime = new ImmutablePair<>(null, null);
        }
        return cycTime;
    }

    public void start(ScheduleBatchJob scheduleBatchJob) {
        try {
            executorService.submit(() -> {
                try {
                    //提交代码里面会将jobStatus设置为submitting
                    batchJobService.startJob(scheduleBatchJob.getScheduleJob());
                    logger.info("--- jobId:{} scheduleType:{} send to engine.", scheduleBatchJob.getJobId(), getScheduleType());
                } catch (Exception e) {
                    logger.info("--- jobId:{} scheduleType:{} send to engine error:", scheduleBatchJob.getJobId(), getScheduleType(), e);
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.FAILED.getStatus(), ExceptionUtil.getErrorMessage(e));
                } finally {
                    batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.EXECUTE_OVER);
                }
            });
        } catch (Exception e) {
            logger.info("--- jobId:{} scheduleType:{} executorService submit to engine error:", scheduleBatchJob.getJobId(), getScheduleType(), e);
        }
    }
}
