package com.dtstack.taier.scheduler.executor;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EJobCacheStage;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.scheduler.jobdealer.JobDealer;
import com.dtstack.taier.scheduler.jobdealer.JobRestartDealer;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rdb executor
 *
 * @author ：wangchuan
 * date：Created in 14:04 2022/10/8
 * company: www.dtstack.com
 */
public class RdbJobExecutor implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(RdbJobExecutor.class);

    private final Integer queueSize;

    private final LinkedBlockingQueue<JobClient> rdbJobWaitQueue;

    private final ExecutorService executorService;

    private final DatasourceOperator datasourceOperator;

    private final JobDealer jobDealer;

    private final JobRestartDealer jobRestartDealer;

    private final ScheduleJobService scheduleJobService;

    private final ScheduleJobExpandService scheduleJobExpandService;

    private final ScheduleJobCacheService scheduleJobCacheService;

    private final ScheduleJobService batchJobService;

    private final AtomicBoolean RUNNING = new AtomicBoolean(true);

    private static final Integer DEFAULT_QUEUE_SIZE = 200;

    private static final Integer DEFAULT_CONSUMER_MIN_NUM = 20;

    private static final Integer DEFAULT_CONSUMER_MAX_NUM = 20;

    public RdbJobExecutor(Integer queueSize, Integer consumerMinNum,
                          Integer consumerMaxNum,
                          ApplicationContext applicationContext) {

        if (applicationContext == null) {
            throw new RdosDefineException(" obtain applicationContext fail");
        }

        this.queueSize = Objects.isNull(queueSize) ? DEFAULT_QUEUE_SIZE : queueSize;

        this.rdbJobWaitQueue = new LinkedBlockingQueue<>(this.queueSize);
        this.datasourceOperator = applicationContext.getBean(DatasourceOperator.class);
        this.jobDealer = applicationContext.getBean(JobDealer.class);
        this.jobRestartDealer = applicationContext.getBean(JobRestartDealer.class);
        this.batchJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobExpandService = applicationContext.getBean(ScheduleJobExpandService.class);
        this.scheduleJobCacheService = applicationContext.getBean(ScheduleJobCacheService.class);
        String threadName = this.getClass().getSimpleName() + "_" + "_start";
        executorService = new ThreadPoolExecutor(
                Objects.isNull(consumerMinNum) ? DEFAULT_CONSUMER_MIN_NUM : consumerMinNum,
                Objects.isNull(consumerMaxNum) ? DEFAULT_CONSUMER_MAX_NUM : consumerMaxNum,
                5L,
                TimeUnit.MINUTES,
                new SynchronousQueue<>(),
                new CustomThreadFactory(threadName));
    }

    public JudgeResult submitJob(JobClient jobClient) {
        try {
            if (rdbJobWaitQueue.size() >= queueSize) {
                // 说明队列已经满了，不在向队列中放入数据
                return JudgeResult.notOk("queue full, maxQueueSize:" + queueSize);
            }
            JSONObject jsonObject = getJsonObject(jobClient);
            scheduleJobService.updateJobSubmitSuccess(jobClient.getJobId(), jobClient.getJobId(), jobClient.getJobId());
            scheduleJobExpandService.updateExtraInfoAndLog(jobClient.getJobId(), null, jsonObject.toJSONString(), null);
            jobDealer.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
            rdbJobWaitQueue.put(jobClient);
            return JudgeResult.ok();
        } catch (Throwable e) {
            LOGGER.error("jobId:{} error,e:", jobClient.getJobId(), e);
            return JudgeResult.exception(ExceptionUtil.getErrorMessage(e));
        }
    }

    @NotNull
    private JSONObject getJsonObject(JobClient jobClient) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jobid", jobClient.getJobId());
        jsonObject.put("msg_info", "submit job is success");
        return jsonObject;
    }

    @Override
    public void run() {
        while (RUNNING.get()) {
            try {
                JobClient jobClient = rdbJobWaitQueue.take();
                try {
                    ScheduleJob scheduleJob = batchJobService.getByJobId(jobClient.getJobId());
                    if (scheduleJob == null) {
                        throw new RdosDefineException("scheduleJob not fount");
                    }
                    executorService.execute(() -> {
                        try {
                            jobClient.doStatusCallBack(TaskStatus.RUNNING.getStatus());
                            LOGGER.info("jobId:{} taskType:{} submit to job start run",
                                    jobClient.getJobId(), jobClient.getTaskType());
                            // executeBatchQuery 执行不成功 会执行抛异常，不会返回false
                            datasourceOperator.executeBatchQuery(jobClient);
                            RetryUtil.executeWithRetry(() -> {
                                updateJobStatus(jobClient, TaskStatus.FINISHED.getStatus());
                                scheduleJobCacheService.deleteByJobId(jobClient.getJobId());
                                return null;
                            }, 3, 200, false);
                        } catch (SourceException sourceException) {
                            // sql执行异常
                            LOGGER.error("jobId: {} dtLoaderException:", jobClient.getJobId(),
                                    sourceException);
                            // 重试任务
                            retryRestart(jobClient, ExceptionUtil.getErrorMessage(sourceException));
                        } catch (Throwable e) {
                            // sql执行异常
                            LOGGER.error("jobId: {}, execute error:", jobClient.getJobId(), e);
                            // 重试任务
                            retryRestart(jobClient, ExceptionUtil.getErrorMessage(e));
                        }
                    });
                } catch (RejectedExecutionException e) {
                    // 等待超时，说明没有消费线程去消费，所以需要等2秒超时重试
                    rdbJobWaitQueue.add(jobClient);
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (Throwable e) {
                LOGGER.error("execute error:", e);
            }
        }
    }

    private void updateJobStatus(JobClient jobClient, Integer status) {
        LOGGER.info("jobId:{} status :{}", jobClient.getJobId(), status);
        scheduleJobService.updateJobStatusAndExecTime(jobClient.getJobId(), status);
    }

    private void retryRestart(JobClient jobClient, String engineLog) {
        boolean retry = jobRestartDealer.checkAndRestartForSubmitResult(jobClient);
        if (!retry) {
            // 没有重试 ，直接更新实例状态和日志
            updateJobStatus(jobClient, TaskStatus.FAILED.getStatus());
            scheduleJobExpandService.updateEngineLog(jobClient.getJobId(), engineLog);
            scheduleJobCacheService.deleteByJobId(jobClient.getJobId());
        }
    }
}
