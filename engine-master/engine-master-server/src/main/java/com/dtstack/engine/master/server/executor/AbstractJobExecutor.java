package com.dtstack.engine.master.server.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.BatchFlowWorkJobService;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.server.scheduler.JobCheckRunInfo;
import com.dtstack.engine.master.server.scheduler.JobRichOperator;
import com.dtstack.engine.master.zookeeper.ZkService;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.Restarted;
import com.google.common.collect.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobExecutor.class);

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
    protected final AtomicBoolean RUNNING = new AtomicBoolean(false);

    private LinkedBlockingQueue<ScheduleBatchJob> scheduleJobQueue = null;

    public abstract EScheduleType getScheduleType();

    public abstract void stop();

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getScheduleType(), env.getAcquireQueueJobInterval(), env.getQueueSize());

        scheduleJobQueue = new LinkedBlockingQueue<>(env.getQueueSize());
        RUNNING.compareAndSet(false, true);

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

    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, Boolean isEq) {
        Pair<String, String> cycTime = getCycTime(false);
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight(), JobPhaseStatus.CREATE.getCode(), isEq
                , null, Restarted.NORMAL.getStatus());
        LOGGER.info("scheduleType:{} nodeAddress:{} leftTime:{} rightTime:{} start scanning since when startId:{}  isEq {}  queryJobSize {}.", getScheduleType(), nodeAddress, cycTime.getLeft(), cycTime.getRight(), startId, isEq,
                scheduleJobs.size());
        return getScheduleBatchJobList(scheduleJobs);
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

                LOGGER.info("jobId:{} scheduleType:{} take job from queue.", scheduleJob.getJobId(), getScheduleType());
                this.start(scheduleBatchJob);
            } catch (Exception e) {
                LOGGER.error("happens error:", e);
                try {
                    if (scheduleJob != null) {
                        batchJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), RdosTaskStatus.SUBMITFAILD.getStatus(), e.getMessage());
                        LOGGER.error("jobId:{} scheduleType:{} submit failed.", scheduleJob.getJobId(), getScheduleType());
                    }
                } catch (Exception ex) {
                    LOGGER.error("jobId:{} scheduleType:{} update status happens error:", scheduleJob.getJobId(), getScheduleType(), ex);
                }
            }
        }
    }

    /**
     * 增量从数据库获取id标示
     *
     * @param nodeAddress
     * @param isRestart
     * @return
     */
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        Pair<String, String> cycTime = getCycTime(true);
        Long listMinId = batchJobService.getListMinId(nodeAddress, getScheduleType().getType(), cycTime.getLeft(), cycTime.getRight(), isRestart);
        LOGGER.info("getListMinId scheduleType {} nodeAddress {} isRestart {} lastMinId is {} . cycStartTime {} cycEndTime {}", getScheduleType(), nodeAddress, isRestart, listMinId, cycTime.getLeft(), cycTime.getRight());
        return listMinId;
    }

    private void emitJob2Queue() {
        String nodeAddress = "";
        try {
            nodeAddress = zkService.getLocalAddress();
            if (StringUtils.isBlank(nodeAddress)) {
                return;
            }
            Long startId = getListMinId(nodeAddress, Restarted.NORMAL.getStatus());
            LOGGER.info("start emitJob2Queue  scheduleType {} nodeAddress {} startId is {} ", getScheduleType(), nodeAddress, startId);
            if (startId != null) {
                List<ScheduleBatchJob> listExecJobs = this.listExecJob(startId, nodeAddress, Boolean.TRUE);
                while (CollectionUtils.isNotEmpty(listExecJobs)) {
                    // 按照appType分组
                    Map<Integer, Set<Long>> groupByAppMap = listExecJobs.stream().collect(Collectors.groupingBy(shade->shade.getScheduleJob().getAppType(),
                            Collectors.mapping(ScheduleBatchJob::getTaskId, Collectors.toSet())));
                    Table<Integer,Long,ScheduleTaskShade> cache= HashBasedTable.create();
                    batchTaskShadeService.listTaskShadeByIdAndType(groupByAppMap).forEach((k,v)->v.forEach(shade->cache.put(k,shade.getTaskId(),shade)));
                    for (ScheduleBatchJob scheduleBatchJob : listExecJobs) {
                        // 节点检查是否能进入队列
                        try {
                            Long taskIdUnique = jobRichOperator.getTaskIdUnique(scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());
                            ScheduleTaskShade batchTask=cache.get(scheduleBatchJob.getScheduleJob().getAppType(),scheduleBatchJob.getTaskId());

                            if (batchTask == null) {
                                String errMsg = JobCheckStatus.NO_TASK.getMsg();
                                batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), RdosTaskStatus.SUBMITFAILD.getStatus(), errMsg);
                                LOGGER.warn("jobId:{} scheduleType:{} submit failed for taskId:{} already deleted.", scheduleBatchJob.getJobId(), getScheduleType(), scheduleBatchJob.getTaskId());
                                continue;
                            }

                            Integer type = batchTask.getTaskType();
                            Integer status = batchJobService.getJobStatus(scheduleBatchJob.getJobId());

                            checkJobVersion(scheduleBatchJob.getScheduleJob(),batchTask);

                            Map<Long, ScheduleTaskShade> taskCache = Maps.newHashMapWithExpectedSize(4);
                            taskCache.put(taskIdUnique, batchTask);

//                          // JobCheckRunInfo checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), new HashSet<>(), new HashMap<>(), taskCache);
                            JobCheckRunInfo checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), batchTask);
                            if (EScheduleJobType.WORK_FLOW.getType().equals(type) || EScheduleJobType.ALGORITHM_LAB.getVal().equals(type)) {
                                LOGGER.info("jobId:{} scheduleType:{} is WORK_FLOW or ALGORITHM_LAB so immediate put queue.", scheduleBatchJob.getJobId(), getScheduleType());
                                if (RdosTaskStatus.UNSUBMIT.getStatus().equals(status) && isPutQueue(checkRunInfo, scheduleBatchJob)) {
                                    putScheduleJob(scheduleBatchJob);
                                } else if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
                                    LOGGER.info("jobId:{} scheduleType:{} is WORK_FLOW or ALGORITHM_LAB start judgment son is execution complete.", scheduleBatchJob.getJobId(), getScheduleType());
                                    batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob);
                                }
                            } else if (EScheduleJobType.NOT_DO_TASK.getType().equals(type)) {
                                LOGGER.info("jobId:{} scheduleType:{} is NOT_DO_TASK not put queue.", scheduleBatchJob.getJobId(), getScheduleType());
                                // 空任务且是未提交状态
                                if (RdosTaskStatus.UNSUBMIT.getStatus().equals(status) && isPutQueue(checkRunInfo, scheduleBatchJob)) {
                                    // 直接状态成运行中
                                    LOGGER.info("jobId:{} is NOT_DO_TASK,status:{} , update RUNNING", scheduleBatchJob.getJobId(),status);
                                    batchJobService.updateStatusByJobIdEqualsStatus(scheduleBatchJob.getJobId(), RdosTaskStatus.RUNNING.getStatus(),RdosTaskStatus.UNSUBMIT.getStatus());
                                } else if (!RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
                                    LOGGER.info("jobId:{} is NOT_DO_TASK,status:{} is not submit，determine whether the timeout", scheduleBatchJob.getJobId(),status);
                                    // 已经提交状态 判断是否超时
                                    if (isTimeOut(scheduleBatchJob, batchTask)) {
                                        // 直接失败更新状态
                                        LOGGER.info("jobId:{} is NOT_DO_TASK,status:{} ,job timeout so update FAILED", scheduleBatchJob.getJobId(), status);
                                        batchJobService.updateStatusAndLogInfoAndExecTimeById(scheduleBatchJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), "空任务超时",null,new Date());
                                    }
                                }
                            } else {
                                if (isPutQueue(checkRunInfo, scheduleBatchJob)) {
                                    // 更新job状态
                                    boolean updateStatus = batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.CREATE, JobPhaseStatus.JOIN_THE_TEAM);
                                    if (updateStatus) {
                                        LOGGER.info("jobId:{} scheduleType:{} nodeAddress:{} JobPhaseStatus:{} update success", scheduleBatchJob.getJobId(), getScheduleType(), nodeAddress, JobPhaseStatus.JOIN_THE_TEAM);
                                        putScheduleJob(scheduleBatchJob);
                                    }
                                }
                            }
                            LOGGER.info("startId is {} jobId is {} scheduleType {} isRestart {}", startId, scheduleBatchJob.getJobId(), getScheduleType(),scheduleBatchJob.getIsRestart());
                            startId = scheduleBatchJob.getId();
                        } catch (Exception e) {
                            LOGGER.error("jobId:{} scheduleType:{} nodeAddress:{} emitJob2Queue error:", scheduleBatchJob.getJobId(), getScheduleType(), nodeAddress, e);
                            Integer status = RdosTaskStatus.FAILED.getStatus();
                            batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), status, e.getMessage());
                        }
                    }
                    listExecJobs = this.listExecJob(startId, nodeAddress, Boolean.FALSE);
                }
            }
        } catch (Exception e) {
            LOGGER.error("scheduleType:{} nodeAddress:{} emitJob2Queue error:", getScheduleType(), nodeAddress, e);
        }
    }

    protected boolean isTimeOut(ScheduleBatchJob scheduleBatchJob, ScheduleTaskShade batchTask) {
        String scheduleConf = batchTask.getScheduleConf();
        JSONObject confJson = JSONObject.parseObject(scheduleConf);

        Long timeout = environmentContext.getTaskRuleTimeout();

        if (confJson != null && confJson.get("taskRuleTimeout") != null && StringUtils.isNotBlank(confJson.get("taskRuleTimeout").toString())) {
            try {
                timeout = Long.parseLong(confJson.get("taskRuleTimeout").toString());
            } catch (NumberFormatException e) {
                LOGGER.warn("scheduleConf 获取超时时间失败！");
            }
        }

        ScheduleJob scheduleJob = scheduleBatchJob.getScheduleJob();
        if (scheduleJob == null) {
            throw new RdosDefineException("job is not null");
        }

        Timestamp execStartTime = scheduleJob.getExecStartTime();

        if (execStartTime == null) {
            throw new RdosDefineException("not find execStartTime");
        }

        long time = execStartTime.getTime();
        long currentTimeMillis = System.currentTimeMillis();

        if ((currentTimeMillis - time) > timeout) {
            // 已经超时
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


    private void checkJobVersion(ScheduleJob scheduleJob, ScheduleTaskShade batchTask) {
        if (null == scheduleJob || null == batchTask || null == scheduleJob.getVersionId() || null == batchTask.getVersionId()) {
            return;
        }
        //同步taskShade最新的versionId
        if (!batchTask.getVersionId().equals(scheduleJob.getVersionId())) {
            LOGGER.info("update scheduleJob jobId {} versionId from {} to {} taskId {}", scheduleJob.getJobId(),scheduleJob.getVersionId(), batchTask.getVersionId(),batchTask.getTaskId());
            scheduleJobDao.updateStatusByJobId(scheduleJob.getJobId(), null, null, batchTask.getVersionId(),null,null);
        }
    }

    private boolean isPutQueue(JobCheckRunInfo checkRunInfo, ScheduleBatchJob scheduleBatchJob) {
        Integer status;
        String errMsg = checkRunInfo.getErrMsg();
        if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
            LOGGER.info("jobId:{} checkRunInfo.status:{} put success queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.TRUE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH
                || checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_NOT_FINISHED
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_FINISHED) {
            LOGGER.info("jobId:{} checkRunInfo.status:{} unable put to queue", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.NO_TASK
                || checkRunInfo.getStatus() == JobCheckStatus.SELF_PRE_PERIOD_EXCEPTION
                || checkRunInfo.getStatus() == JobCheckStatus.TASK_DELETE
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_NO_CREATED
                || checkRunInfo.getStatus() == JobCheckStatus.RESOURCE_OVER_LIMIT) {
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
        } else if (checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_SUCCESS) {
            status = RdosTaskStatus.FAILED.getStatus();
        } else {
            LOGGER.error("appear unknown jobId:{} checkRunInfo.status:{} ", scheduleBatchJob.getJobId(), checkRunInfo.getStatus());
            return Boolean.FALSE;
        }
        LOGGER.info("jobId:{} checkRunInfo.status:{} errMsg:{} status:{} update status.", scheduleBatchJob.getJobId(), checkRunInfo.getStatus(), errMsg, status);
        batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), status, errMsg);
        batchFlowWorkJobService.batchUpdateFlowSubJobStatus(scheduleBatchJob.getScheduleJob(),status);
        return Boolean.FALSE;
    }

    private void putScheduleJob(ScheduleBatchJob scheduleBatchJob) {
        try {
            if (scheduleJobQueue.contains(scheduleBatchJob)) {
                //元素已存在，返回true
                LOGGER.info("jobId:{} scheduleType:{} queue has contains ", scheduleBatchJob.getJobId(), getScheduleType());
                return;
            }
            scheduleJobQueue.put(scheduleBatchJob);
            LOGGER.info("jobId:{} scheduleType:{} enter queue", scheduleBatchJob.getJobId(), getScheduleType());
        } catch (InterruptedException e) {
            LOGGER.error("jobId:{} scheduleType:{} job phase rollback, error", scheduleBatchJob.getJobId(), getScheduleType(), e);
            batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.CREATE);
        }
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
    public Pair<String, String> getCycTime(boolean minJobId) {
        if (getScheduleType().getType() == EScheduleType.NORMAL_SCHEDULE.getType()) {
            return jobRichOperator.getCycTimeLimitEndNow(true,false,minJobId);
        }
        // 补数据
        else if(env.getOpenFillDataCycTimeLimit()) {
            return jobRichOperator.getCycTimeLimitEndNow(false,false,minJobId);
        }
        return new ImmutablePair<>(null, null);

    }

    public void start(ScheduleBatchJob scheduleBatchJob) {
        try {
            executorService.submit(() -> {
                try {
                    //提交代码里面会将jobStatus设置为submitting
                    batchJobService.startJob(scheduleBatchJob.getScheduleJob());
                    LOGGER.info("--- jobId:{} scheduleType:{} send to engine.", scheduleBatchJob.getJobId(), getScheduleType());
                } catch (Exception e) {
                    LOGGER.info("--- jobId:{} scheduleType:{} send to engine error:", scheduleBatchJob.getJobId(), getScheduleType(), e);
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getJobId(), RdosTaskStatus.FAILED.getStatus(), ExceptionUtil.getErrorMessage(e));
                } finally {
                    batchJobService.updatePhaseStatusById(scheduleBatchJob.getId(), JobPhaseStatus.JOIN_THE_TEAM, JobPhaseStatus.EXECUTE_OVER);
                }
            });
        } catch (Exception e) {
            LOGGER.info("--- jobId:{} scheduleType:{} executorService submit to engine error:", scheduleBatchJob.getJobId(), getScheduleType(), e);
        }
    }
}
