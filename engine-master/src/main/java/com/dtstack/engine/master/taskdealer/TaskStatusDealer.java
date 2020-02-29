package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.hash.ShardData;
import com.dtstack.engine.common.util.LogCountUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.PluginInfoDao;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.FailedTaskInfo;
import com.dtstack.engine.master.cache.ShardCache;
import com.dtstack.engine.master.cache.ShardManager;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
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
    private EngineJobDao engineJobDao;
    private EngineJobCacheDao engineJobCacheDao;
    private PluginInfoDao pluginInfoDao;
    private TaskCheckpointDealer taskCheckpointDealer;
    private TaskRestartDealer taskRestartDealer;
    private WorkerOperator workerOperator;

    /**
     * 失败任务的额外处理：当前只是对(失败任务 or 取消任务)继续更新日志或者更新checkpoint
     */
    private Map<String, FailedTaskInfo> failedJobCache = Maps.newConcurrentMap();

    /**
     * 记录job 连续某个状态的频次
     */
    private Map<String, TaskStatusFrequencyDealer> jobStatusFrequency = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(true), new CustomThreadFactory("taskStatusListener"));

    @Override
    public void run() {
        try {
            if (logger.isInfoEnabled() && LogCountUtil.count(logOutput++, MULTIPLES)) {
                logger.info("jobResource:{} TaskStatusListener start again gap:[{} ms]...", jobResource, INTERVAL * MULTIPLES);
            }
            updateTaskStatus();
            dealFailedJob();
        } catch (Throwable e) {
            logger.error("jobResource:{} TaskStatusTaskListener run error:{}", jobResource, e);
        }
    }

    public void dealFailedJob() {
        try {
            for (Map.Entry<String, FailedTaskInfo> failedTaskEntry : failedJobCache.entrySet()) {
                FailedTaskInfo failedTaskInfo = failedTaskEntry.getValue();
                String key = failedTaskEntry.getKey();
                updateJobEngineLog(failedTaskInfo.getJobId(), failedTaskInfo.getJobIdentifier(),
                        failedTaskInfo.getEngineType(), failedTaskInfo.getComputeType(), failedTaskInfo.getPluginInfo());

                boolean streamAndopenCheckpoint = isFlinkStreamTask(failedTaskInfo) && taskCheckpointDealer.checkOpenCheckPoint(failedTaskInfo.getJobId());
                if (streamAndopenCheckpoint) {
                    //更新checkpoint
                    taskCheckpointDealer.updateStreamJobCheckpoints(failedTaskInfo.getJobIdentifier(), failedTaskInfo.getEngineType(), failedTaskInfo.getPluginInfo());
                } else if (isSyncTask(failedTaskInfo)) {
                    taskCheckpointDealer.updateBatchTaskCheckpoint(failedTaskInfo.getPluginInfo(), failedTaskInfo.getJobIdentifier());
                }

                failedTaskInfo.waitClean();
                if (!failedTaskInfo.allowClean()) {
                    // filter batch task
                    if (streamAndopenCheckpoint) {
                        taskCheckpointDealer.dealStreamCheckpoint(failedTaskInfo);
                    }
                    failedJobCache.remove(key);
                }
            }
        } catch (Exception e) {
            logger.error("dealFailed job run error:{}", e);
        }
    }

    private boolean isSyncTask(FailedTaskInfo failedTaskInfo) {
        return failedTaskInfo.getComputeType() == ComputeType.BATCH.getType()
                && EngineType.isFlink(failedTaskInfo.getEngineType());
    }

    public boolean isFlinkStreamTask(FailedTaskInfo failedTaskInfo) {
        return failedTaskInfo.getComputeType() == ComputeType.STREAM.getType()
                && EngineType.isFlink(failedTaskInfo.getEngineType());
    }

    public void addFailedJob(FailedTaskInfo failedTaskInfo) {
        if (!failedJobCache.containsKey(failedTaskInfo.getJobId())) {
            failedJobCache.put(failedTaskInfo.getJobId(), failedTaskInfo);
        }
    }

    private void updateTaskStatus() {
        try {
            Map<String, ShardData> shards = shardManager.getShards();
            CountDownLatch ctl = new CountDownLatch(shards.size());
            for (Map.Entry<String, ShardData> shardEntry : shards.entrySet()) {
                taskStatusPool.submit(() -> {
                    try {
                        for (Map.Entry<String, Integer> entry : shardEntry.getValue().getView().entrySet()) {
                            try {
                                if (!RdosTaskStatus.needClean(entry.getValue())) {

                                    logger.info("jobId:{} status:{}", entry.getKey(), entry.getValue());
                                    dealJob(entry.getKey());
                                }
                            } catch (Throwable e) {
                                logger.error("", e);
                            }
                        }
                    } catch (Throwable e) {
                        logger.error("{}", e);
                    } finally {
                        ctl.countDown();
                    }
                });
            }
            ctl.await();
        } catch (Throwable e) {
            logger.error("{}", e);
        }
    }

    private void dealJob(String jobId) throws Exception {
        EngineJob engineJob = engineJobDao.getRdosJobByJobId(jobId);
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJob != null && engineJobCache != null) {
            String engineTaskId = engineJob.getEngineJobId();
            String appId = engineJob.getApplicationId();
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineTaskId, appId, jobId);

            if (StringUtils.isNotBlank(engineTaskId)) {
                String pluginInfoStr = "";
                if (engineJob.getPluginInfoId() > 0) {
                    pluginInfoStr = pluginInfoDao.getPluginInfo(engineJob.getPluginInfoId());
                }

                RdosTaskStatus rdosTaskStatus = workerOperator.getJobStatus(engineJobCache.getEngineType(), pluginInfoStr, jobIdentifier);

                if (rdosTaskStatus != null) {

                    updateJobEngineLog(jobId, jobIdentifier, engineJobCache.getEngineType(), engineJobCache.getComputeType(), pluginInfoStr);

                    rdosTaskStatus = checkNotFoundStatus(rdosTaskStatus, jobId);

                    Integer status = rdosTaskStatus.getStatus();
                    // 重试状态 先不更新状态
                    boolean isRestart = taskRestartDealer.checkAndRestart(status, jobId, engineTaskId, appId, engineJobCache.getEngineType(), pluginInfoStr);
                    if (isRestart) {
                        return;
                    }

                    if (taskCheckpointDealer.isSyncTaskAndOpenCheckpoint(jobId, engineJobCache.getEngineType(), engineJobCache.getComputeType())) {
                        taskCheckpointDealer.dealSyncTaskCheckpoint(status, jobIdentifier, pluginInfoStr);
                    }

                    shardCache.updateLocalMemTaskStatus(jobId, status);
                    //数据的更新顺序，先更新job_cache，再更新engine_batch_job

                    if (ComputeType.STREAM.getType().equals(engineJob.getComputeType())) {
                        dealStreamAfterGetStatus(status, jobId, engineJobCache.getEngineType(), jobIdentifier, pluginInfoStr);
                    } else {
                        dealBatchJobAfterGetStatus(status, jobId);
                    }

                    engineJobDao.updateJobStatusAndExecTime(jobId, status);
                    logger.info("jobId:{} update job status:{}.", jobId, status);
                }

                if (RdosTaskStatus.FAILED.equals(rdosTaskStatus)) {
                    FailedTaskInfo failedTaskInfo = new FailedTaskInfo(engineJob.getJobId(), jobIdentifier,
                            engineJobCache.getEngineType(), engineJobCache.getComputeType(), pluginInfoStr);
                    addFailedJob(failedTaskInfo);
                }
            }
        } else {
            shardCache.updateLocalMemTaskStatus(jobId, RdosTaskStatus.FAILED.getStatus());
            engineJobCacheDao.delete(jobId);
        }
    }

    private void updateJobEngineLog(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, String pluginInfo) {
        try {
            //从engine获取log
            String jobLog = workerOperator.getEngineLog(engineType, pluginInfo, jobIdentifier);
            if (jobLog != null) {
                updateJobEngineLog(jobId, jobLog);
            }
        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            logger.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            updateJobEngineLog(jobId, errorLog);
        }
    }

    private void updateJobEngineLog(String jobId, String jobLog) {

        //写入db
        engineJobDao.updateEngineLog(jobId, jobLog);
    }

    private RdosTaskStatus checkNotFoundStatus(RdosTaskStatus taskStatus, String jobId) {
        TaskStatusFrequencyDealer statusPair = updateJobStatusFrequency(jobId, taskStatus.getStatus());
        if (statusPair.getStatus() == RdosTaskStatus.NOTFOUND.getStatus().intValue()) {
            if (statusPair.getNum() >= NOT_FOUND_LIMIT_TIMES ||
                    System.currentTimeMillis() - statusPair.getCreateTime() >= NOT_FOUND_LIMIT_INTERVAL) {
                return RdosTaskStatus.FAILED;
            }
        }
        return taskStatus;
    }

    /**
     * stream 获取任务状态--的处理
     *
     * @param status
     * @param jobId
     */
    private void dealStreamAfterGetStatus(Integer status, String jobId, String engineTypeName,
                                          JobIdentifier jobIdentifier, String pluginInfo) throws ExecutionException {

        String engineTaskId = jobIdentifier.getEngineJobId();

        boolean openCheckPoint = taskCheckpointDealer.checkOpenCheckPoint(jobId);

        if (RdosTaskStatus.getStoppedStatus().contains(status)) {
            jobStatusFrequency.remove(jobId);
            engineJobCacheDao.delete(jobId);

            if (Strings.isNullOrEmpty(engineTaskId)) {
                return;
            }

            if (openCheckPoint) {
                taskCheckpointDealer.updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }
        }

        if (!openCheckPoint) {
            return;
        }

        if (RdosTaskStatus.RUNNING.getStatus().equals(status)) {
            //运行中的stream任务需要更新checkpoint 并且 控制频率
            Integer checkpointCallNum = taskCheckpointDealer.getCheckpointCallNum(engineTaskId);
            if (checkpointCallNum % TaskCheckpointDealer.CHECKPOINT_GET_RATE == 0) {
                taskCheckpointDealer.updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }
        }

    }

    private void dealBatchJobAfterGetStatus(Integer status, String jobId) throws ExecutionException {
        if (RdosTaskStatus.getStoppedStatus().contains(status)) {
            jobStatusFrequency.remove(jobId);
            engineJobCacheDao.delete(jobId);
        }
    }

    /**
     * 更新任务状态频次
     *
     * @param jobId
     * @param status
     * @return
     */
    private TaskStatusFrequencyDealer updateJobStatusFrequency(String jobId, Integer status) {

        TaskStatusFrequencyDealer statusFrequency = jobStatusFrequency.get(jobId);
        statusFrequency = statusFrequency == null ? new TaskStatusFrequencyDealer(status) : statusFrequency;
        if (statusFrequency.getStatus() == status.intValue()) {
            statusFrequency.setNum(statusFrequency.getNum() + 1);
        } else {
            statusFrequency = new TaskStatusFrequencyDealer(status);
        }

        jobStatusFrequency.put(jobId, statusFrequency);
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
    }

    private void setBean() {
        this.engineJobDao = applicationContext.getBean(EngineJobDao.class);
        this.engineJobCacheDao = applicationContext.getBean(EngineJobCacheDao.class);
        this.pluginInfoDao = applicationContext.getBean(PluginInfoDao.class);
        this.taskCheckpointDealer = applicationContext.getBean(TaskCheckpointDealer.class);
        this.taskRestartDealer = applicationContext.getBean(TaskRestartDealer.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
    }
}