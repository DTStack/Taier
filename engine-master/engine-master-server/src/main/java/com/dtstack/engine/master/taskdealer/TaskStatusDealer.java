package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.hash.ShardData;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.PluginInfoDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.CompletedTaskInfo;
import com.dtstack.engine.master.bo.FailedTaskInfo;
import com.dtstack.engine.master.cache.ShardCache;
import com.dtstack.engine.master.cache.ShardManager;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 *
 * @author toutian
 *         create: 2020/01/17
 */
public class TaskStatusDealer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TaskStatusDealer.class);

    /**
     * 最大允许查询不到任务信息的次数--超过这个次数任务会被设置为CANCELED
     */
    private final static int NOT_FOUND_LIMIT_TIMES = 300;

    /**
     * 最大允许查询不到的任务信息最久时间
     */
    private final static int NOT_FOUND_LIMIT_INTERVAL = 3 * 60 * 1000;

    public static final long INTERVAL = 2000;
    private final static int MULTIPLES = 5;
    private int logOutput = 0;

    private ApplicationContext applicationContext;
    private ShardManager shardManager;
    private ShardCache shardCache;
    private String jobResource;
    private ScheduleJobDao scheduleJobDao;
    private EngineJobCacheDao engineJobCacheDao;
    private PluginInfoDao pluginInfoDao;
    private TaskCheckpointDealer taskCheckpointDealer;
    private TaskRestartDealer taskRestartDealer;
    private WorkerOperator workerOperator;
    private EnvironmentContext environmentContext;
    private long jobLogDelay;
    private JobCompletedLogDelayDealer jobCompletedLogDelayDealer;

    private int taskStatusDealerPoolSize;

    /**
     * 记录job 连续某个状态的频次
     */
    private Map<String, TaskStatusFrequencyDealer> jobStatusFrequency = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool;

    @Override
    public void run() {
        try {
            if (logger.isInfoEnabled() && LogCountUtil.count(logOutput++, MULTIPLES)) {
                logger.info("jobResource:{} start again gap:[{} ms]...", jobResource, INTERVAL * MULTIPLES);
            }

            CountDownLatch ctl = new CountDownLatch(taskStatusDealerPoolSize);
            Map<String, ShardData> shards = shardManager.getShards();
            for (Map.Entry<String, ShardData> shardEntry : shards.entrySet()) {
                for (Map.Entry<String, Integer> entry : shardEntry.getValue().getView().entrySet()) {
                    try {
                        if (!RdosTaskStatus.needClean(entry.getValue())) {
                            taskStatusPool.submit(() -> {
                                try {
                                    logger.info("jobId:{} status:{}", entry.getKey(), entry.getValue());
                                    dealJob(entry.getKey());
                                } catch (Throwable e) {
                                    logger.error("{}", e);
                                } finally {
                                    ctl.countDown();
                                }
                            });
                        }
                    } catch (Throwable e) {
                        logger.error("", e);
                    }
                }
            }
            ctl.await();

        } catch (Throwable e) {
            logger.error("jobResource:{} run error:{}", jobResource, e);
        }
    }


    private void dealJob(String jobId) throws Exception {
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (scheduleJob != null && engineJobCache != null) {
            String engineTaskId = scheduleJob.getEngineJobId();
            String appId = scheduleJob.getApplicationId();
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineTaskId, appId, jobId);

            if (StringUtils.isNotBlank(engineTaskId)) {
                String pluginInfoStr = scheduleJob.getPluginInfoId() > 0 ? pluginInfoDao.getPluginInfo(scheduleJob.getPluginInfoId()) : "";
                RdosTaskStatus rdosTaskStatus = workerOperator.getJobStatus(engineJobCache.getEngineType(), pluginInfoStr, jobIdentifier);
                if (rdosTaskStatus != null) {

                    rdosTaskStatus = checkNotFoundStatus(rdosTaskStatus, jobId);
                    Integer status = rdosTaskStatus.getStatus();
                    // 重试状态 先不更新状态
                    boolean isRestart = taskRestartDealer.checkAndRestart(status, jobId, engineTaskId, appId, engineJobCache.getEngineType(), pluginInfoStr);
                    if (isRestart) {
                        return;
                    }

                    shardCache.updateLocalMemTaskStatus(jobId, status);
                    scheduleJobDao.updateJobStatusAndExecTime(jobId, status);
                    logger.info("jobId:{} update job status:{}.", jobId, status);

                    //数据的更新顺序，先更新job_cache，再更新engine_batch_job
                    if (RdosTaskStatus.getStoppedStatus().contains(status)) {
                        jobLogDelayDealer(jobId, jobIdentifier, engineJobCache.getEngineType(), engineJobCache.getComputeType(), pluginInfoStr);
                        jobStatusFrequency.remove(jobId);
                        engineJobCacheDao.delete(jobId);
                    }

                    if (RdosTaskStatus.RUNNING.getStatus().equals(status)) {
                        taskCheckpointDealer.addCheckpointTaskForQueue(scheduleJob.getComputeType(), jobId, jobIdentifier, engineJobCache.getEngineType(), pluginInfoStr);
                    }
                }
            }
        } else {
            shardCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.CANCELED.getStatus());
            scheduleJobDao.updateJobStatusAndExecTime(jobId, RdosTaskStatus.CANCELED.getStatus());
            logger.info("jobId:{} update job status:{}.", jobId, RdosTaskStatus.CANCELED.getStatus());
            engineJobCacheDao.delete(jobId);
        }
    }

    private RdosTaskStatus checkNotFoundStatus(RdosTaskStatus taskStatus, String jobId) {
        TaskStatusFrequencyDealer statusPair = updateJobStatusFrequency(jobId, taskStatus.getStatus());
        //如果状态为NotFound，则对频次进行判断
        if (statusPair.getStatus() == RdosTaskStatus.NOTFOUND.getStatus().intValue()) {
            if (statusPair.getNum() >= NOT_FOUND_LIMIT_TIMES || System.currentTimeMillis() - statusPair.getCreateTime() >= NOT_FOUND_LIMIT_INTERVAL) {
                return RdosTaskStatus.FAILED;
            }
        }
        return taskStatus;
    }


    private void jobLogDelayDealer(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, String pluginInfo) {
        jobCompletedLogDelayDealer.addCompletedTaskInfo(new CompletedTaskInfo(jobId, jobIdentifier, engineType, computeType, pluginInfo, jobLogDelay));
    }


    /**
     * 更新任务状态频次
     *
     * @param jobId
     * @param status
     * @return
     */
    private TaskStatusFrequencyDealer updateJobStatusFrequency(String jobId, Integer status) {
        TaskStatusFrequencyDealer statusFrequency = jobStatusFrequency.computeIfAbsent(jobId, k -> new TaskStatusFrequencyDealer(status));
        if (statusFrequency.getStatus().equals(status)) {
            statusFrequency.setNum(statusFrequency.getNum() + 1);
        } else {
            statusFrequency.setNum(0);
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
                new SynchronousQueue<>(true), new CustomThreadFactory(jobResource + this.getClass().getSimpleName()));
    }

    private void setBean() {
        this.environmentContext = applicationContext.getBean(EnvironmentContext.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.pluginInfoDao = applicationContext.getBean(PluginInfoDao.class);
        this.taskCheckpointDealer = applicationContext.getBean(TaskCheckpointDealer.class);
        this.taskRestartDealer = applicationContext.getBean(TaskRestartDealer.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
        this.scheduleJobDao = applicationContext.getBean(ScheduleJobDao.class);
    }

    private void createLogDelayDealer() {
        this.jobCompletedLogDelayDealer = new JobCompletedLogDelayDealer(applicationContext);
        this.jobLogDelay = environmentContext.getJobLogDelay();
    }
}