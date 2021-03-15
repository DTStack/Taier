package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.enums.TaskRuleEnum;
import com.dtstack.engine.common.BlockCallerPolicy;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobStatusFrequency;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.bo.JobCompletedInfo;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.master.utils.TaskParamsUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 *
 * @author toutian
 *         create: 2020/01/17
 */
public class  JobStatusDealer implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(JobStatusDealer.class);

    /**
     * 最大允许查询不到任务信息的次数--超过这个次数任务会被设置为CANCELED
     */
    private final static int NOT_FOUND_LIMIT_TIMES = 300;

    /**
     * 最大允许查询不到的任务信息最久时间
     */
    private final static int NOT_FOUND_LIMIT_INTERVAL = 3 * 60 * 1000;

    public static final long INTERVAL = 3500;
    private final static int MULTIPLES = 5;
    private int logOutput = 0;

    private ApplicationContext applicationContext;
    private ShardManager shardManager;
    private ShardCache shardCache;
    private String jobResource;
    private ScheduleJobDao scheduleJobDao;
    private ScheduleJobJobDao scheduleJobJobDao;
    private EngineJobCacheDao engineJobCacheDao;
    private JobCheckpointDealer jobCheckpointDealer;
    private JobRestartDealer jobRestartDealer;
    private WorkerOperator workerOperator;
    private EnvironmentContext environmentContext;
    private long jobLogDelay;
    private JobCompletedLogDelayDealer jobCompletedLogDelayDealer;

    private int taskStatusDealerPoolSize;

    /**
     * 记录job 连续某个状态的频次
     */
    private final Map<String, JobStatusFrequency> jobStatusFrequency = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool;

    private ScheduleJobService scheduleJobService;

    @Override
    public void run() {
        try {
            if (logger.isDebugEnabled() && LogCountUtil.count(logOutput++, MULTIPLES)) {
                logger.debug("jobResource:{} start again gap:[{} ms]...", jobResource, INTERVAL * MULTIPLES);
            }

            List<Map.Entry<String, Integer>> jobs = new ArrayList<>(shardManager.getShard().entrySet());
            if (jobs.isEmpty()){
                return;
            }

            jobs = jobs.stream().filter(job -> !RdosTaskStatus.needClean(job.getValue())).collect(Collectors.toList());

            Semaphore buildSemaphore = new Semaphore(taskStatusDealerPoolSize);
            CountDownLatch ctl = new CountDownLatch(jobs.size());
            for (Map.Entry<String, Integer> job : jobs) {
                try {
                    buildSemaphore.acquire();
                    taskStatusPool.submit(() -> {
                        try {
                            logger.info("jobId:{} before dealJob status:{}", job.getKey(), job.getValue());
                            dealJob(job.getKey());
                        } catch (Throwable e) {
                            logger.error("jobId:{}", job.getKey(), e);
                        } finally {
                            buildSemaphore.release();
                            ctl.countDown();
                        }
                    });
                } catch (Throwable e) {
                    logger.error("jobId:{} [acquire pool error]:",job.getKey(), e);
                    buildSemaphore.release();
                    ctl.countDown();
                }
            }
            ctl.await();

        } catch (Throwable e) {
            logger.error("jobResource:{} run error:", jobResource, e);
        }
    }


    private void dealJob(String jobId) throws Exception {
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (scheduleJob == null || engineJobCache == null || StringUtils.isBlank(scheduleJob.getEngineJobId())) {
            shardCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.CANCELED.getStatus());

            Integer status = RdosTaskStatus.CANCELED.getStatus();
            String engineJobId = null;
            if (scheduleJob != null) {
                engineJobId = scheduleJob.getEngineJobId();

                if (RdosTaskStatus.getStoppedStatus().contains(scheduleJob.getStatus())) {
                    status = scheduleJob.getStatus();
                } else {
                    scheduleJobDao.updateJobStatusAndExecTime(jobId, status);
                }
            } else {
                scheduleJobDao.updateJobStatusAndExecTime(jobId, status);
            }

            engineJobCacheDao.delete(jobId);
            logger.info("jobId:{} set job finished, status:{}, scheduleJob is {} null, engineJobCache is {} null, engineJobId is {} blank.",
                    jobId, status, scheduleJob == null ? "" : "not", engineJobCache == null ? "" : "not", engineJobId == null ? "" : "not");
        } else {
            String engineTaskId = scheduleJob.getEngineJobId();
            String appId = scheduleJob.getApplicationId();
            String engineType = engineJobCache.getEngineType();
            JSONObject info = JSONObject.parseObject(engineJobCache.getJobInfo());
            String taskParams = info.getString("taskParams");
            String pluginInfo = info.getString("pluginInfo");
            Long userId = info.getLong("userId");
            JobIdentifier jobIdentifier = new JobIdentifier(engineTaskId, appId, jobId,scheduleJob.getDtuicTenantId(),engineType, TaskParamsUtil.parseDeployTypeByTaskParams(taskParams,scheduleJob.getComputeType(),engineType).getType(),userId, pluginInfo);

            RdosTaskStatus rdosTaskStatus = workerOperator.getJobStatus(jobIdentifier);

            logger.info("------ jobId:{} dealJob status:{}", jobId, rdosTaskStatus);

            if (rdosTaskStatus != null) {

                rdosTaskStatus = checkNotFoundStatus(rdosTaskStatus, jobId);
                Integer status = rdosTaskStatus.getStatus();
                // 重试状态 先不更新状态
                boolean isRestart = jobRestartDealer.checkAndRestart(status, scheduleJob,engineJobCache);
                if (isRestart) {
                    logger.info("----- jobId:{} after dealJob status:{}", jobId, rdosTaskStatus);
                    return;
                }

                shardCache.updateLocalMemTaskStatus(jobId, status);
                updateJobStatusWithPredicate(scheduleJob, jobId, status);

                //数据的更新顺序，先更新job_cache，再更新engine_batch_job
                if (RdosTaskStatus.getStoppedStatus().contains(status)) {
                    if (EngineType.isFlink(engineType)){
                        jobCheckpointDealer.updateCheckpointImmediately(new JobCheckpointInfo(jobIdentifier, engineType), jobId, status);
                    }


                    jobLogDelayDealer(jobId, jobIdentifier, engineType, engineJobCache.getComputeType(),scheduleJob.getType());
                    jobStatusFrequency.remove(jobId);
                    engineJobCacheDao.delete(jobId);
                    logger.info("------ jobId:{} is stop status {} delete jobCache", jobId, status);
                }

                if (RdosTaskStatus.RUNNING.getStatus().equals(status) && EngineType.isFlink(engineType)) {
                    jobCheckpointDealer.addCheckpointTaskForQueue(scheduleJob.getComputeType(), jobId, jobIdentifier, engineType);
                }

                logger.info("------ jobId:{} after dealJob status:{}", jobId, rdosTaskStatus);
            }
        }
    }

    private void updateJobStatusWithPredicate(ScheduleJob scheduleJob, String jobId, Integer status) {
        //流计算只有在状态变更(且任务没有被手动停止 进入CANCELLING)的时候才去更新schedule_job表
        Predicate<ScheduleJob> isStreamUpdateConditions = job ->
                ComputeType.STREAM.getType().equals(job.getComputeType())
                        && !job.getStatus().equals(status)
                        && !RdosTaskStatus.CANCELLING.getStatus().equals(job.getStatus());

        //流计算 任务被手动停止 进入CANCELLING 除非YARN上状态已结束 才回写
        Predicate<ScheduleJob> isStreamCancellingConditions = job ->
                ComputeType.STREAM.getType().equals(job.getComputeType())
                        && RdosTaskStatus.CANCELLING.getStatus().equals(job.getStatus())
                        && RdosTaskStatus.STOP_STATUS.contains(status);

        if (ComputeType.BATCH.getType().equals(scheduleJob.getComputeType()) || isStreamUpdateConditions.test(scheduleJob) || isStreamCancellingConditions.test(scheduleJob)) {
            if (RdosTaskStatus.RUNNING.getStatus().equals(status) && hasTaskRule(scheduleJob)) {
                // 判断子节点是否存在强弱任务
                scheduleJobDao.updateJobStatusAndExecTime(jobId,RdosTaskStatus.RUNNING_TASK_RULE.getStatus());
            } else {
                scheduleJobDao.updateJobStatusAndExecTime(jobId, status);
            }
        }
    }

    private boolean hasTaskRule(ScheduleJob scheduleJob) {
        boolean hasTaskRule = Boolean.FALSE;
        List<ScheduleJobJob> scheduleJobJobs = scheduleJobJobDao.listByParentJobKey(scheduleJob.getJobKey());

        List<String> jobKeys = scheduleJobJobs.stream().map(ScheduleJobJob::getJobKey).collect(Collectors.toList());
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobByJobKeys(jobKeys);

        for (ScheduleJob job : scheduleJobs) {
            if (TaskRuleEnum.STRONG_RULE.getCode().equals(job.getStatus())) {
                // 存在强规则任务
                hasTaskRule = Boolean.TRUE;
                break;
            }
        }
        return hasTaskRule;
    }

    private RdosTaskStatus checkNotFoundStatus(RdosTaskStatus taskStatus, String jobId) {
        JobStatusFrequency statusPair = updateJobStatusFrequency(jobId, taskStatus.getStatus());
        //如果状态为NotFound，则对频次进行判断
        if (statusPair.getStatus() == RdosTaskStatus.NOTFOUND.getStatus().intValue()) {
            if (statusPair.getNum() >= NOT_FOUND_LIMIT_TIMES || System.currentTimeMillis() - statusPair.getCreateTime() >= NOT_FOUND_LIMIT_INTERVAL) {
                logger.info(" job id {}  check not found status had try max , change status to {} ", jobId, RdosTaskStatus.FAILED.getStatus());
                return RdosTaskStatus.FAILED;
            }
        }
        return taskStatus;
    }


    private void jobLogDelayDealer(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType,Integer type) {
        //临时运行的任务立马去获取日志
        jobCompletedLogDelayDealer.addCompletedTaskInfo(new JobCompletedInfo(jobId, jobIdentifier, engineType, computeType, EScheduleType.TEMP_JOB.getType() == type ? 0 : jobLogDelay));
    }


    /**
     * 更新任务状态频次
     *
     * @param jobId
     * @param status
     * @return
     */
    private JobStatusFrequency updateJobStatusFrequency(String jobId, Integer status) {
        JobStatusFrequency statusFrequency = jobStatusFrequency.computeIfAbsent(jobId, k -> new JobStatusFrequency(status));
        if (statusFrequency.getStatus().equals(status)) {
            statusFrequency.setNum(statusFrequency.getNum() + 1);
        } else {
            statusFrequency.resetJobStatus(status);
        }
        return statusFrequency;
    }

    public void setShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    public void setShardCache(ShardCache shardCache) {
        this.shardCache = shardCache;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setBean();
        createLogDelayDealer();

        this.taskStatusDealerPoolSize = environmentContext.getTaskStatusDealerPoolSize();
        this.taskStatusPool = new ThreadPoolExecutor(taskStatusDealerPoolSize, taskStatusDealerPoolSize, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), new CustomThreadFactory(jobResource + this.getClass().getSimpleName() + "DealJob"), new BlockCallerPolicy());
    }

    private void setBean() {
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.jobCheckpointDealer = applicationContext.getBean(JobCheckpointDealer.class);
        this.jobRestartDealer = applicationContext.getBean(JobRestartDealer.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobJobDao = applicationContext.getBean(ScheduleJobJobDao.class);

    }

    private void createLogDelayDealer() {
        this.jobCompletedLogDelayDealer = new JobCompletedLogDelayDealer(applicationContext);
        this.jobLogDelay = environmentContext.getJobLogDelay();
    }

    public void start() {
        long jobStatusCheckInterVal = environmentContext.getJobStatusCheckInterVal();
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(jobResource + this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                jobStatusCheckInterVal,
                TimeUnit.MILLISECONDS);
        logger.info("{} thread start ...", jobResource + this.getClass().getSimpleName());

    }
}