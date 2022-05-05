/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.BlockCallerPolicy;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.util.LogCountUtil;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobHistory;
import com.dtstack.taier.dao.mapper.ScheduleJobHistoryMapper;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.jobdealer.bo.JobCompletedInfo;
import com.dtstack.taier.scheduler.jobdealer.bo.JobStatusFrequency;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardCache;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardManager;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
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
public class JobStatusDealer implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(JobStatusDealer.class);

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
    private ScheduleJobService scheduleJobService;
    private ScheduleJobCacheService scheduleJobCacheService;
    private JobRestartDealer jobRestartDealer;
    private WorkerOperator workerOperator;
    private EnvironmentContext environmentContext;
    private ScheduleJobHistoryMapper scheduleJobHistoryMapper;
    private long jobLogDelay;
    private JobCompletedLogDelayDealer jobCompletedLogDelayDealer;

    private int taskStatusDealerPoolSize;

    /**
     * 记录job 连续某个状态的频次
     */
    private final Map<String, JobStatusFrequency> jobStatusFrequency = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool;

    @Override
    public void run() {
        try {
            if (LOGGER.isDebugEnabled() && LogCountUtil.count(logOutput++, MULTIPLES)) {
                LOGGER.debug("jobResource:{} start again gap:[{} ms]...", jobResource, INTERVAL * MULTIPLES);
            }

            List<Map.Entry<String, Integer>> jobs = new ArrayList<>(shardManager.getShard().entrySet());
            if (jobs.isEmpty()){
                return;
            }

            jobs = jobs.stream().filter(job -> !TaskStatus.needClean(job.getValue())).collect(Collectors.toList());

            Semaphore buildSemaphore = new Semaphore(taskStatusDealerPoolSize);
            for (Map.Entry<String, Integer> job : jobs) {
                try {
                    buildSemaphore.acquire();
                    taskStatusPool.submit(() -> {
                        try {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("jobId:{} before dealJob status:{}", job.getKey(), job.getValue());
                            }
                            dealJob(job.getKey());
                        } catch (Throwable e) {
                            LOGGER.error("jobId:{}", job.getKey(), e);
                        } finally {
                            buildSemaphore.release();
                        }
                    });
                } catch (Throwable e) {
                    LOGGER.error("jobId:{} [acquire pool error]:",job.getKey(), e);
                    buildSemaphore.release();
                }
            }

        } catch (Throwable e) {
            LOGGER.error("jobResource:{} run error:", jobResource, e);
        }
    }


    private void dealJob(String jobId) throws Exception {
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        ScheduleEngineJobCache engineJobCache = scheduleJobCacheService.getJobCacheByJobId(jobId);
        if (scheduleJob == null || engineJobCache == null ||
                (StringUtils.isBlank(scheduleJob.getApplicationId()) && StringUtils.isBlank(scheduleJob.getEngineJobId()))) {
            shardCache.updateLocalMemTaskStatus(jobId, TaskStatus.CANCELED.getStatus());

            Integer status = TaskStatus.CANCELED.getStatus();
            String engineJobId = null;
            if (scheduleJob != null) {
                engineJobId = scheduleJob.getEngineJobId();

                if (TaskStatus.getStoppedStatus().contains(scheduleJob.getStatus())) {
                    status = scheduleJob.getStatus();
                } else {
                    scheduleJobService.updateJobStatusAndExecTime(jobId, status);
                }
            } else {
                scheduleJobService.updateJobStatusAndExecTime(jobId, status);
            }

            scheduleJobCacheService.deleteByJobId(jobId);
            LOGGER.info("jobId:{} set job finished, status:{}, scheduleJob is {} null, engineJobCache is {} null, engineJobId is {} blank.",
                    jobId, status, scheduleJob == null ? "" : "not", engineJobCache == null ? "" : "not", engineJobId == null ? "" : "not");
        } else {
            String engineTaskId = scheduleJob.getEngineJobId();
            String appId = scheduleJob.getApplicationId();
            ParamAction paramAction = PublicUtil.jsonStrToObject(engineJobCache.getJobInfo(), ParamAction.class);
            Integer taskType = paramAction.getTaskType();
            Map<String, Object> pluginInfo = paramAction.getPluginInfo();
            JobIdentifier jobIdentifier = new JobIdentifier(engineTaskId, appId, jobId,scheduleJob.getTenantId(),taskType,
                    TaskParamsUtils.parseDeployTypeByTaskParams(paramAction.getTaskParams(),scheduleJob.getComputeType()).getType(),
                    null,  MapUtils.isEmpty(pluginInfo) ? null : JSONObject.toJSONString(pluginInfo),paramAction.getComponentVersion());

            TaskStatus taskStatus = workerOperator.getJobStatus(jobIdentifier);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("------ jobId:{} dealJob status:{}", jobId, taskStatus);
            }

            if (taskStatus != null) {

                taskStatus = checkNotFoundStatus(taskStatus, jobId);
                Integer status = taskStatus.getStatus();
                // 重试状态 先不更新状态
                boolean isRestart = jobRestartDealer.checkAndRestart(status, scheduleJob, engineJobCache, (job, client) -> ForkJoinPool.commonPool().execute(() -> {
                    String engineLog = workerOperator.getEngineLog(jobIdentifier);
                    jobRestartDealer.jobRetryRecord(job, client, engineLog);
                }));
                if (isRestart) {
                    LOGGER.info("----- jobId:{} after dealJob status:{}", jobId, taskStatus);
                    return;
                }

                shardCache.updateLocalMemTaskStatus(jobId, status);
                updateJobStatusWithPredicate(scheduleJob, jobId, status);

                //数据的更新顺序，先更新job_cache，再更新engine_batch_job
                if (TaskStatus.getStoppedStatus().contains(status)) {
                    jobLogDelayDealer(jobId, jobIdentifier, engineJobCache.getComputeType(),scheduleJob.getType());
                    jobStatusFrequency.remove(jobId);
                    scheduleJobCacheService.deleteByJobId(jobId);
                    updateHistoryEndTime(jobId,appId);
                    LOGGER.info("------ jobId:{} is stop status {} delete jobCache", jobId, status);
                }


                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("------ jobId:{} after dealJob status:{}", jobId, taskStatus);
                }
            }
        }
    }

    private void updateHistoryEndTime(String jobId, String appId) {
        ScheduleJobHistory scheduleJobHistory = new ScheduleJobHistory();
        scheduleJobHistory.setJobId(jobId);
        scheduleJobHistory.setApplicationId(appId);
        scheduleJobHistory.setExecEndTime(DateTime.now().toDate());
        scheduleJobHistoryMapper.update(scheduleJobHistory, Wrappers.lambdaQuery(ScheduleJobHistory.class)
                .eq(ScheduleJobHistory::getJobId, jobId)
                .eq(ScheduleJobHistory::getApplicationId, appId));
    }

    private void updateJobStatusWithPredicate(ScheduleJob scheduleJob, String jobId, Integer status) {
        //流计算只有在状态变更(且任务没有被手动停止 进入CANCELLING)的时候才去更新schedule_job表
        Predicate<ScheduleJob> isStreamUpdateConditions = job ->
                ComputeType.STREAM.getType().equals(job.getComputeType())
                        && !job.getStatus().equals(status)
                        && !TaskStatus.CANCELLING.getStatus().equals(job.getStatus());

        //流计算 任务被手动停止 进入CANCELLING 除非YARN上状态已结束 才回写, 引擎返回的最终状态需要回写到数据库
        Predicate<ScheduleJob> isStreamCancellingConditions = job ->
                ComputeType.STREAM.getType().equals(job.getComputeType())
                        && TaskStatus.CANCELLING.getStatus().equals(job.getStatus())
                        && TaskStatus.STOPPED_STATUS.contains(status);

        if (ComputeType.BATCH.getType().equals(scheduleJob.getComputeType()) || isStreamUpdateConditions.test(scheduleJob) || isStreamCancellingConditions.test(scheduleJob)) {
            if (TaskStatus.getStoppedStatus().contains(status)) {
                // 如果是停止状态 更新停止时间
                scheduleJobService.updateJobStatusAndExecTime(jobId, status);
            } else {
                scheduleJobService.updateStatus(jobId, status);
            }
        }
    }
    
    private TaskStatus checkNotFoundStatus(TaskStatus taskStatus, String jobId) {
        JobStatusFrequency statusPair = updateJobStatusFrequency(jobId, taskStatus.getStatus());
        //如果状态为NotFound，则对频次进行判断
        if (statusPair.getStatus() == TaskStatus.NOTFOUND.getStatus().intValue()) {
            if (statusPair.getNum() >= NOT_FOUND_LIMIT_TIMES || System.currentTimeMillis() - statusPair.getCreateTime() >= NOT_FOUND_LIMIT_INTERVAL) {
                LOGGER.info(" job id {}  check not found status had try max , change status to {} ", jobId, TaskStatus.FAILED.getStatus());
                return TaskStatus.FAILED;
            }
        }
        return taskStatus;
    }


    private void jobLogDelayDealer(String jobId, JobIdentifier jobIdentifier, int computeType,Integer type) {
        //临时运行的任务立马去获取日志
        jobCompletedLogDelayDealer.addCompletedTaskInfo(new JobCompletedInfo(jobId, jobIdentifier, computeType, EScheduleType.TEMP_JOB.getType().equals(type) ? 0 : jobLogDelay));
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
        this.jobRestartDealer = applicationContext.getBean(JobRestartDealer.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
        this.scheduleJobService = applicationContext.getBean(ScheduleJobService.class);
        this.scheduleJobCacheService = applicationContext.getBean(ScheduleJobCacheService.class);
        this.scheduleJobHistoryMapper = applicationContext.getBean(ScheduleJobHistoryMapper.class);
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
        LOGGER.info("{} thread start ...", jobResource + this.getClass().getSimpleName());

    }
}