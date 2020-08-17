package com.dtstack.engine.master.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.enums.ScheduleEngineType;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.JobCheckStatus;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobRunStatus;
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
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.Restarted;
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

import java.lang.ref.SoftReference;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

    private JopPriorityQueue jopPriorityQueue;

    private Set<String> notStartCache = Sets.newHashSet();
    private Map<String, JobErrorInfo> errorJobCache = Maps.newHashMap();
    private SoftReference<Map<Long, ScheduleTaskShade>> softReference;
    protected final AtomicBoolean RUNNING = new AtomicBoolean(true);
    private volatile long lastRestartJobLoadTime = 0L;


    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getScheduleType(), env.getAcquireQueueJobInterval(), env.getQueueSize());

        this.jopPriorityQueue = new JopPriorityQueue(
                getScheduleType(),
                env.getAcquireQueueJobInterval(),
                env.getQueueSize(),
                this::emitJob2Queue);
    }

    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, String cycStartTime, String cycEndTime) {
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, getScheduleType(), cycStartTime, cycEndTime, JobRunStatus.CREATE.getCode());
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
        jopPriorityQueue.clearAndAllIngestion((this::disasterPreparedness));
    }

    public void disasterPreparedness() {
        // 容器关闭，容器内的作业恢复待扫描的状态
        while (jopPriorityQueue.getQueueSize() > 0) {
            try {
                BatchJobElement batchJobElement = jopPriorityQueue.takeJob();
                ScheduleBatchJob scheduleBatchJob = batchJobElement.getScheduleBatchJob();
                // 更新待扫描状态
                batchJobService.updateRunStatusById(scheduleBatchJob.getId(), JobRunStatus.JOIN_THE_TEAM ,JobRunStatus.CREATE);
            } catch (InterruptedException e) {
                logger.error("happens error:", e);
            }
        }
    }


    @Override
    public void run() {
        while (RUNNING.get()) {

            ScheduleJob scheduleJob = null;
            JobCheckRunInfo checkRunInfo = null;
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("========= scheduleType:{} take job from queue，before queueSize:{}, full:{}",
                            getScheduleType(), jopPriorityQueue.getQueueSize(), jopPriorityQueue.getFullSize());
                }
                //元素全部放到survivor中 重新全量加载
                BatchJobElement batchJobElement = jopPriorityQueue.takeJob();
                if (logger.isDebugEnabled()) {
                    logger.debug("========= scheduleType:{} take job from queue，after queueSize:{}, full:{} ",
                            getScheduleType(), jopPriorityQueue.getQueueSize(), jopPriorityQueue.getFullSize());
                }

                ScheduleBatchJob scheduleBatchJob = batchJobElement.getScheduleBatchJob();
                if (Objects.isNull(scheduleBatchJob)) {
                    continue;
                }
                scheduleJob = scheduleBatchJob.getScheduleJob();
                boolean runUpdate = batchJobService.updateRunStatusById(scheduleBatchJob.getId(), JobRunStatus.JOIN_THE_TEAM, JobRunStatus.LEAVE_THE_TEAM);

                if (!runUpdate) {
                    continue;
                }

                Long taskIdUnique = jobRichOperator.getTaskIdUnique(scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());
                ScheduleTaskShade batchTask = this.taskCache().computeIfAbsent(taskIdUnique,
                        k -> batchTaskShadeService.getBatchTaskById(scheduleBatchJob.getTaskId(), scheduleBatchJob.getScheduleJob().getAppType()));

                Integer type = batchTask.getTaskType();
                Integer status = batchJobService.getStatusById(scheduleBatchJob.getId());

                if (type.intValue() == EScheduleJobType.WORK_FLOW.getVal() || type.intValue() == EScheduleJobType.ALGORITHM_LAB.getVal()) {
                    if (status.intValue() == RdosTaskStatus.UNSUBMIT.getStatus()) {
                        this.start(batchTask, scheduleBatchJob);
                        this.errorJobCache.remove(scheduleBatchJob.getJobKey());
                        this.notStartCache.remove(scheduleBatchJob.getJobKey());
                    }

                    batchFlowWorkJobService.checkRemoveAndUpdateFlowJobStatus(scheduleBatchJob.getJobId(), scheduleBatchJob.getAppType());
                } else {
                    this.start(batchTask, scheduleBatchJob);
                    this.errorJobCache.remove(scheduleBatchJob.getJobKey());
                    this.notStartCache.remove(scheduleBatchJob.getJobKey());
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
                logger.warn("========= scheduleType:{} take job {} from queue，before queueSize:{}, full:{} checkRunInfo:{} =========",
                        getScheduleType(), Objects.isNull(scheduleJob) ? "" : scheduleJob.getJobId(),
                        jopPriorityQueue.getQueueSize(), jopPriorityQueue.getFullSize(), JSONObject.toJSONString(checkRunInfo));

            }
        }
    }

    /**
     * 缓存task
     *
     * @return
     */
    private  Map<Long, ScheduleTaskShade> taskCache(){
        if (this.softReference == null) {
            this.softReference = new SoftReference<>(Maps.newHashMap());
        }

        if (this.softReference.get()==null) {
            this.softReference = new SoftReference<>(Maps.newHashMap());
        }
        return this.softReference.get();
    }


    private void emitJob2Queue(JopPriorityQueue jopPriorityQueue) {
        String nodeAddress = zkService.getLocalAddress();
        if (StringUtils.isBlank(nodeAddress)) {
            return;
        }
        try {
            //限制数据范围
            Pair<String, String> cycTime = getCycTime();
            Long startId = batchJobService.getListMinId(nodeAddress, getScheduleType(), cycTime.getLeft(), cycTime.getRight());
            List<ScheduleBatchJob> listExecJobs = this.listExecJob(startId, nodeAddress, cycTime.getLeft(), cycTime.getRight());
            while (CollectionUtils.isNotEmpty(listExecJobs)) {
                if (logger.isInfoEnabled()) {
                    logger.info("scheduleType:{} nodeAddress:{} start put queue", getScheduleType(), nodeAddress);
                }

                for (ScheduleBatchJob scheduleBatchJob : listExecJobs) {
                    // 节点检查是否能进入队列
                    JobCheckRunInfo checkRunInfo;

                    Long taskIdUnique = jobRichOperator.getTaskIdUnique(scheduleBatchJob.getAppType(), scheduleBatchJob.getTaskId());
                    ScheduleTaskShade batchTask = this.taskCache().computeIfAbsent(taskIdUnique, k -> batchTaskShadeService.getBatchTaskById(scheduleBatchJob.getTaskId(), scheduleBatchJob.getScheduleJob().getAppType()));

                    if (batchTask == null) {
                        String errMsg = JobCheckStatus.NO_TASK.getMsg();
                        batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.SUBMITFAILD.getStatus(), errMsg);
                        logger.warn("scheduleType:{} jobId [{}] submit failed for task [{}] already deleted.", getScheduleType(), scheduleBatchJob.getJobId(), scheduleBatchJob.getTaskId());
                        continue;
                    }

                    Integer type = batchTask.getTaskType();
                    Integer status = batchJobService.getStatusById(scheduleBatchJob.getId());

                    if ((type.intValue() == EScheduleJobType.WORK_FLOW.getType()
                            || type.intValue() == EScheduleJobType.ALGORITHM_LAB.getVal())
                            && !RdosTaskStatus.UNSUBMIT.getStatus().equals(status)) {
                        checkRunInfo = JobCheckRunInfo.createCheckInfo(JobCheckStatus.CAN_EXE);
                    } else {
                        checkRunInfo = jobRichOperator.checkJobCanRun(scheduleBatchJob, status, scheduleBatchJob.getScheduleType(), this.notStartCache, this.errorJobCache, this.taskCache());
                    }

                    if (isPutQueue(checkRunInfo,scheduleBatchJob)) {
                        // 更新job状态
                        boolean updateStatus = batchJobService.updateRunStatusById(scheduleBatchJob.getId(), JobRunStatus.CREATE, JobRunStatus.JOIN_THE_TEAM);
                        if (updateStatus) {
                            boolean isPutQueue = jopPriorityQueue.putJob(scheduleBatchJob);
                            if (!isPutQueue) {
                                if (logger.isInfoEnabled()) {
                                    logger.info("scheduleType:{} nodeAddress:{} schedule:{} job rollback", getScheduleType(), nodeAddress,scheduleBatchJob.getJobId());
                                }
                                batchJobService.updateRunStatusById(scheduleBatchJob.getId(), JobRunStatus.JOIN_THE_TEAM, JobRunStatus.CREATE);
                            } else {
                                if (logger.isInfoEnabled()) {
                                    logger.info("scheduleType:{} nodeAddress:{} schedule:{} enter queue", getScheduleType(), nodeAddress,scheduleBatchJob.getJobId());
                                }
                            }
                        }
                    }
                    //重跑任务不记录id
                    if (Restarted.RESTARTED.getStatus() != scheduleBatchJob.getIsRestart()) {
                        startId = scheduleBatchJob.getId();
                    }
                }
                listExecJobs = this.listExecJob(startId, nodeAddress, cycTime.getLeft(), cycTime.getRight());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("scheduleType:{} nodeAddress:{} emitJob2Queue return startId:{}", getScheduleType(), nodeAddress, startId);
            }
        } catch (Exception e) {
            logger.error("scheduleType:{} odeAddress:{} emitJob2Queue error:{}", getScheduleType(), nodeAddress, e);
        }
    }

    private boolean isPutQueue(JobCheckRunInfo checkRunInfo, ScheduleBatchJob scheduleBatchJob) {
        Integer status;
        String errMsg = checkRunInfo.getErrMsg();
        if (checkRunInfo.getStatus() == JobCheckStatus.CAN_EXE) {
            return Boolean.TRUE;
        } else if (checkRunInfo.getStatus() == JobCheckStatus.TIME_NOT_REACH
                || checkRunInfo.getStatus() == JobCheckStatus.NOT_UNSUBMIT
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_SUCCESS
                || checkRunInfo.getStatus() == JobCheckStatus.FATHER_JOB_NOT_FINISHED
                || checkRunInfo.getStatus() == JobCheckStatus.CHILD_PRE_NOT_FINISHED) {
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
            logger.error("appear unknown status {} ,jobId:{}", checkRunInfo.getStatus(), scheduleBatchJob.getJobId());
            return Boolean.FALSE;
        }

        batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), status, errMsg);
        return Boolean.FALSE;
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

    private ConcurrentHashMap<String, ExecutorService> executorServiceMap = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<String> startCache = new CopyOnWriteArrayList<>();

    public void start(ScheduleTaskShade batchTask, ScheduleBatchJob scheduleBatchJob) {
        if (startCache.contains(scheduleBatchJob.getJobId())) {
            logger.info("--- scheduleType:{} start cache contains jobId {} ", getScheduleType(), scheduleBatchJob.getJobId());
            return;
        }

        ScheduleEngineType scheduleEngineType = ScheduleEngineType.getEngineType(batchTask.getEngineType());
        String engineType = "default";
        if (Objects.nonNull(scheduleEngineType)) {
            engineType = scheduleEngineType.getEngineName();
        }
        ExecutorService executorService = executorServiceMap.get(engineType);
        if (Objects.isNull(executorService)) {
            String threadName = this.getClass().getSimpleName() + "_" + engineType + "_startJobProcessor";
            executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new CustomThreadFactory(threadName),
                    new CustomThreadRunsPolicy(threadName, engineType)
            );
            executorServiceMap.put(engineType, executorService);
        }
        startCache.add(scheduleBatchJob.getJobId());
        try {
            executorService.submit(() -> {
                try {
                    //提交代码里面会将jobStatus设置为submitting
                    batchJobService.startJob(scheduleBatchJob.getScheduleJob());
                    logger.info("---scheduleType:{} send job:{} to engine.", getScheduleType(), scheduleBatchJob.getJobId());
                } catch (Exception e) {
                    logger.info("--- send job:{} to engine error", scheduleBatchJob.getJobId(), e);
                    batchJobService.updateStatusAndLogInfoById(scheduleBatchJob.getId(), RdosTaskStatus.FAILED.getStatus(), ExceptionUtil.getErrorMessage(e));
                } finally {
                    startCache.remove(scheduleBatchJob.getJobId());
                }
            });
        } catch (Exception e) {
            logger.info("--- submit job:{} to engine error", scheduleBatchJob.getJobId(), e);
            startCache.remove(scheduleBatchJob.getJobId());
        }
    }
}
