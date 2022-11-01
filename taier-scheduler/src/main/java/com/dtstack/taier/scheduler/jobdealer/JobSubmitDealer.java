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
import com.dtstack.taier.common.BlockCallerPolicy;
import com.dtstack.taier.common.enums.EJobCacheStage;
import com.dtstack.taier.common.enums.EJobClientType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ClientAccessException;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.exception.WorkerAccessException;
import com.dtstack.taier.common.queue.DelayBlockingQueue;
import com.dtstack.taier.common.util.SleepUtil;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.constrant.JobResultConstant;
import com.dtstack.taier.pluginapi.enums.EQueueSourceType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ClientArgumentException;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.executor.RdbJobExecutor;
import com.dtstack.taier.scheduler.jobdealer.bo.SimpleJobDelay;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardCache;
import com.dtstack.taier.scheduler.server.JobPartitioner;
import com.dtstack.taier.scheduler.server.queue.GroupInfo;
import com.dtstack.taier.scheduler.server.queue.GroupPriorityQueue;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class JobSubmitDealer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobSubmitDealer.class);

    /**
     * 用于taskListener处理, 此处为static修饰，全局共用一个
     */
    private static LinkedBlockingQueue<JobClient> submittedQueue = new LinkedBlockingQueue<>();

    private JobPartitioner jobPartitioner;
    private WorkerOperator workerOperator;
    private ScheduleJobCacheService ScheduleJobCacheService;
    private ShardCache shardCache;

    private long jobRestartDelay;
    private long jobLackingDelay;
    private long jobPriorityStep;
    private long jobLackingInterval;
    private long jobSubmitExpired;
    private long jobLackingCountLimited = 3;
    private boolean checkJobMaxPriorityStrategy = false;
    private int jobSubmitConcurrent = 1;

    private String localAddress;
    private String jobResource = null;
    private GroupPriorityQueue priorityQueue;
    private PriorityBlockingQueue<JobClient> queue = null;
    private DelayBlockingQueue<SimpleJobDelay<JobClient>> delayJobQueue = null;
    private JudgeResult workerNotFindResult = JudgeResult.notOk("worker not find");
    private ExecutorService jobSubmitConcurrentService;
    private ScheduleJobExpandService scheduleJobExpandService;
    private ApplicationContext applicationContext;
    private RdbJobExecutor rdbJobExecutor;

    public JobSubmitDealer(String localAddress, GroupPriorityQueue priorityQueue, ApplicationContext applicationContext) {
        this.jobPartitioner = applicationContext.getBean(JobPartitioner.class);
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
        this.ScheduleJobCacheService = applicationContext.getBean(ScheduleJobCacheService.class);
        this.shardCache = applicationContext.getBean(ShardCache.class);
        this.scheduleJobExpandService = applicationContext.getBean(ScheduleJobExpandService.class);
        this.applicationContext = applicationContext;
        EnvironmentContext environmentContext = applicationContext.getBean(EnvironmentContext.class);
        if (null == priorityQueue) {
            throw new TaierDefineException("priorityQueue must not null.");
        }

        jobRestartDelay = environmentContext.getJobRestartDelay();
        jobLackingDelay = environmentContext.getJobLackingDelay();
        jobPriorityStep = environmentContext.getJobPriorityStep();
        jobLackingInterval = environmentContext.getJobLackingInterval();
        jobSubmitExpired = environmentContext.getJobSubmitExpired();
        jobLackingCountLimited = environmentContext.getJobLackingCountLimited();
        checkJobMaxPriorityStrategy = environmentContext.isCheckJobMaxPriorityStrategy();
        jobSubmitConcurrent = environmentContext.getJobSubmitConcurrent();

        this.localAddress = localAddress;
        this.priorityQueue = priorityQueue;
        this.jobResource = priorityQueue.getJobResource();
        this.queue = priorityQueue.getQueue();
        this.delayJobQueue = new DelayBlockingQueue<>(priorityQueue.getQueueSizeLimited());

        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_DelayJobProcessor"));
        executorService.submit(new RestartJobProcessor());

        this.jobSubmitConcurrentService = new ThreadPoolExecutor(1, jobSubmitConcurrent, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory(this.getClass().getSimpleName() + "_" + jobResource + "_JobSubmitConcurrent"), new BlockCallerPolicy());
        rdbJobExecutor = new RdbJobExecutor(applicationContext, jobResource);
    }

    private class RestartJobProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                SimpleJobDelay<JobClient> simpleJobDelay = null;
                JobClient jobClient = null;
                try {
                    simpleJobDelay = delayJobQueue.take();
                    jobClient = simpleJobDelay.getJob();
                    if (jobClient != null) {
                        jobClient.setQueueSourceType(EQueueSourceType.DELAY.getCode());
                        queue.put(jobClient);
                        LOGGER.info("jobId:{} stage:{} take job from delayJobQueue queue size:{} and add to priorityQueue.", jobClient.getJobId(), simpleJobDelay.getStage(), delayJobQueue.size());
                    }
                } catch (Exception e) {
                    if (simpleJobDelay != null && jobClient != null) {
                        LOGGER.error("jobId:{} stage:{}", jobClient.getJobId(), simpleJobDelay.getStage(), e);
                    } else {
                        LOGGER.error("restartJob take error", e);
                    }
                }
            }
        }
    }

    public boolean tryPutRestartJob(JobClient jobClient) {
        boolean tryPut = delayJobQueue.tryPut(new SimpleJobDelay<>(jobClient, EJobCacheStage.RESTART.getStage(), Objects.isNull(jobClient.getRetryIntervalTime()) ? jobRestartDelay : jobClient.getRetryIntervalTime()));
        LOGGER.info("jobId:{} {} add job to restart delayJobQueue.", jobClient.getJobId(), tryPut ? "success" : "failed");
        if (tryPut) {
            //restart的状态修改会在外面处理，这里只需要set stage
            ScheduleJobCacheService.updateStage(jobClient.getJobId(), EJobCacheStage.RESTART.getStage(), localAddress, jobClient.getPriority(), null);
        }
        return tryPut;
    }

    private void putLackingJob(JobClient jobClient, JudgeResult judgeResult) {
        try {
            delayJobQueue.put(new SimpleJobDelay<>(jobClient, EJobCacheStage.LACKING.getStage(), jobLackingDelay));
            jobClient.lackingCountIncrement();
            ScheduleJobCacheService.updateStage(jobClient.getJobId(), EJobCacheStage.LACKING.getStage(), localAddress, jobClient.getPriority(), judgeResult.getReason());
            jobClient.doStatusCallBack(TaskStatus.LACKING.getStatus());
        } catch (InterruptedException e) {
            queue.put(jobClient);
            LOGGER.error("jobId:{} delayJobQueue.put failed.", jobClient.getJobId(), e);
        }
        LOGGER.info("jobId:{} success add job to lacking delayJobQueue, job's lackingCount:{}.", jobClient.getJobId(), jobClient.getLackingCount());
    }

    public int getDelayJobQueueSize() {
        return delayJobQueue.size();
    }

    @Override
    public void run() {
        while (true) {
            JobClient jobClient = null;
            try {
                jobClient = queue.take();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("jobId:{} jobResource:{} queue size:{} take job from priorityQueue.", jobClient.getJobId(), jobResource, queue.size());
                }
                if (checkIsFinished(jobClient)) {
                    continue;
                }
                if (checkJobSubmitExpired(jobClient)) {
                    shardCache.updateLocalMemTaskStatus(jobClient.getJobId(), TaskStatus.AUTOCANCELED.getStatus());
                    jobClient.doStatusCallBack(TaskStatus.AUTOCANCELED.getStatus());
                    ScheduleJobCacheService.deleteByJobId(jobClient.getJobId());
                    LOGGER.info("jobId:{} checkJobSubmitExpired is true, job ignore to submit.", jobClient.getJobId());
                    continue;
                }
                if (!checkMaxPriority(jobResource)) {
                    LOGGER.info("jobId:{} checkMaxPriority is false, wait other node job which priority higher.", jobClient.getJobId());
                    queue.put(jobClient);
                    SleepUtil.sleep(jobLackingInterval);
                    continue;
                }

                //提交任务
                JobClient finalJobClient = jobClient;
                jobSubmitConcurrentService.submit(() -> submitJob(finalJobClient));
            } catch (Exception e) {
                if (null != jobClient) {
                    LOGGER.error("jobId {} submit error", jobClient.getJobId(), e);
                    handlerFailedWithRetry(jobClient, false, e);
                } else {
                    LOGGER.error("submit error", e);
                }
            }
        }
    }

    private boolean checkIsFinished(JobClient jobClient) {
        ScheduleEngineJobCache engineJobCache = ScheduleJobCacheService.getByJobId(jobClient.getJobId());
        try {
            if (null == jobClient.getQueueSourceType() || EQueueSourceType.NORMAL.getCode() == jobClient.getQueueSourceType()) {
                if (null == engineJobCache) {
                    shardCache.updateLocalMemTaskStatus(jobClient.getJobId(), TaskStatus.CANCELED.getStatus());
                    jobClient.doStatusCallBack(TaskStatus.CANCELED.getStatus());
                    LOGGER.info("jobId:{} checkIsFinished is true, job is Finished.", jobClient.getJobId());
                    return true;
                }
            } else {
                if (null == engineJobCache) {
                    //如果任务出现资源不足 一直deploy加大延时  界面杀死重跑立马完成之后 deployQueue数据未移除
                    //重新放入之后直接取消 导致状态更新waitEngine 状态不一致 所以需要判断下数据是否存在
                    LOGGER.info("jobId:{} stage:{} take job from delayJobQueue  but engine job cache has deleted", jobClient.getJobId(), delayJobQueue.size());
                    return true;
                } else {
                    //如果任务存在 还需要判断cache表数据是否为重跑后插入生成的
                    boolean checkCanSubmit = true;
                    if (null != jobClient.getSubmitCacheTime()) {
                        long insertDbCacheTime = engineJobCache.getGmtCreate().getTime();
                        checkCanSubmit = insertDbCacheTime <= jobClient.getSubmitCacheTime();

                    }
                    if (checkCanSubmit) {
                        ScheduleJobCacheService.updateStage(jobClient.getJobId(), EJobCacheStage.PRIORITY.getStage(), localAddress, jobClient.getPriority(), null);
                        jobClient.doStatusCallBack(TaskStatus.WAITENGINE.getStatus());
                        return false;
                    } else {
                        //插入cache表的时间 比 jobClient 第一次提交时间晚 认为任务重新提交过 当前延时队列的jobClient 抛弃 不做任何处理
                        LOGGER.info("jobId:{} checkIsFinished is true checkCanSubmit is false jobClient cacheSubmitTime {} cacheDB SubmitTime {}, job is Finished.",
                                jobClient.getJobId(), jobClient.getSubmitCacheTime(), engineJobCache.getGmtCreate().getTime());
                        return true;
                    }

                }
            }
        } finally {
            //重置状态
            jobClient.setQueueSourceType(EQueueSourceType.NORMAL.getCode());
            if (null != engineJobCache && null == jobClient.getSubmitCacheTime()) {
                LOGGER.info("jobId:{} set submitCacheTime is {},", jobClient.getJobId(), engineJobCache.getGmtCreate().getTime());
                jobClient.setSubmitCacheTime(engineJobCache.getGmtCreate().getTime());
            }
        }
        return false;
    }

    private boolean checkJobSubmitExpired(JobClient jobClient) {
        long submitExpiredTime;
        if ((submitExpiredTime = jobClient.getSubmitExpiredTime()) > 0) {
            return System.currentTimeMillis() - jobClient.getGenerateTime() > submitExpiredTime;
        } else if (jobSubmitExpired > 0) {
            return System.currentTimeMillis() - jobClient.getGenerateTime() > jobSubmitExpired;
        }
        return false;
    }

    private boolean checkMaxPriority(String jobResource) {
        //根据配置要求是否需要对job判断最高的优先级
        if (!checkJobMaxPriorityStrategy) {
            return true;
        }

        Map<String, GroupInfo> groupInfoMap = jobPartitioner.getGroupInfoByJobResource(jobResource);
        if (null == groupInfoMap) {
            return true;
        }
        String minPriorityAddress = null;
        long minPriority = Long.MAX_VALUE;
        long localPriority = Long.MAX_VALUE;
        for (Map.Entry<String, GroupInfo> groupInfoEntry : groupInfoMap.entrySet()) {
            String address = groupInfoEntry.getKey();
            GroupInfo groupInfo = groupInfoEntry.getValue();

            if (groupInfo.getPriority() > 0 && localAddress.equals(address)) {
                localPriority = groupInfo.getPriority();
            }

            //Priority值越低，优先级越高
            if (groupInfo.getPriority() > 0 && groupInfo.getPriority() < minPriority) {
                minPriorityAddress = address;
                minPriority = groupInfo.getPriority();
            }
        }
        // hashmap不排序，防止多节点下a、b相同priority逻辑死锁
        return localAddress.equalsIgnoreCase(minPriorityAddress) || localPriority == minPriority;
    }

    private void submitJob(JobClient jobClient) {
        // 判断是不是 rdbms 任务
        EJobClientType jobClientType = EJobClientType.getJobClientTypeByTask(jobClient.getTaskType());
        switch (jobClientType) {
            case DATASOURCE_PLUGIN:
                submitRdbJob(jobClient);
                return;
            case WORKER_PLUGIN:
                submitEngineJob(jobClient);
                return;
            default:
                throw new DtCenterDefException(String.format("task type [%s] is not support.", jobClient.getTaskType()));
        }
    }

    private void submitEngineJob(JobClient jobClient) {
        JobResult jobResult;
        try {
            // 判断资源
            JudgeResult judgeResult = workerOperator.judgeSlots(jobClient);
            if (JudgeResult.JudgeType.OK == judgeResult.getResult()) {
                LOGGER.info("jobId:{} taskType:{} submit to engine start.", jobClient.getJobId(), jobClient.getTaskType());

                jobClient.doStatusCallBack(TaskStatus.COMPUTING.getStatus());

                // 提交任务
                jobResult = workerOperator.submitJob(jobClient);

                if (EScheduleJobType.FLINK_SQL.getType().equals(jobClient.getTaskType())) {
                    saveArchiveFsDir(jobClient, jobResult);
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("jobId:{} taskType:{} submit jobResult:{}.", jobClient.getJobId(), jobClient.getTaskType(), jobResult);
                }

                jobClient.setEngineTaskId(jobResult.getData(JobResult.EXT_ID_KEY));
                jobClient.setApplicationId(jobResult.getData(JobResult.JOB_ID_KEY));
                addToTaskListener(jobClient, jobResult);
                LOGGER.info("jobId:{} taskType:{} submit to engine end.", jobClient.getJobId(), jobClient.getTaskType());
            } else if (JudgeResult.JudgeType.LIMIT_ERROR == judgeResult.getResult()) {
                LOGGER.info("jobId:{} taskType:{} submitJob happens system limitError:{}", jobClient.getJobId(), jobClient.getTaskType(), judgeResult.getReason());
                jobClient.setEngineTaskId(null);
                jobResult = JobResult.createErrorResult(false, judgeResult.getReason());
                addToTaskListener(jobClient, jobResult);
            } else if (JudgeResult.JudgeType.EXCEPTION == judgeResult.getResult()) {
                LOGGER.info("jobId:{} taskType:{} judgeSlots result is exception {}", jobClient.getJobId(), jobClient.getTaskType(), judgeResult.getReason());
                handlerFailedWithRetry(jobClient, true, new Exception(judgeResult.getReason()));
            } else {
                LOGGER.info("jobId:{} taskType:{} judgeSlots result is false.", jobClient.getJobId(), jobClient.getTaskType());
                handlerNoResource(jobClient, judgeResult);
            }
        } catch (WorkerAccessException e) {
            LOGGER.info(" jobId:{} taskType:{} worker not find.", jobClient.getJobId(), jobClient.getTaskType());
            handlerNoResource(jobClient, workerNotFindResult);
        } catch (ClientAccessException | ClientArgumentException e) {
            handlerFailedWithRetry(jobClient, false, e);
        } catch (Throwable e) {
            handlerFailedWithRetry(jobClient, true, e);
        }
    }

    private void submitRdbJob(JobClient jobClient) {
        try {
            // 提交无状态任务
            JudgeResult judgeResult = rdbJobExecutor.submitJob(jobClient);
            // 提交成功
            if (JudgeResult.JudgeType.OK == judgeResult.getResult()) {
                LOGGER.info("jobId:{} taskType:{} submit end.", jobClient.getJobId(), jobClient.getTaskType());
            } else if (JudgeResult.JudgeType.LIMIT_ERROR == judgeResult.getResult()) {
                LOGGER.info("jobId:{} taskType:{} submitJob happens system limitError:{}", jobClient.getJobId(), jobClient.getTaskType(), judgeResult.getReason());
                jobClient.setEngineTaskId(null);
                JobResult jobResult = JobResult.createErrorResult(false, judgeResult.getReason());
                addToTaskListener(jobClient, jobResult);
            } else {
                LOGGER.info("jobId:{} taskType:{} judgeSlots result is false.", jobClient.getJobId(), jobClient.getTaskType());
                handlerNoResource(jobClient, judgeResult);
            }
        } catch (Throwable e) {
            handlerFailedWithRetry(jobClient, true, e);
        }
    }

    private void saveArchiveFsDir(JobClient jobClient, JobResult jobResult) {
        JSONObject pluginInfo = jobResult.getExtraInfoJson();
        if (null == pluginInfo || pluginInfo.isEmpty()) {
            return;
        }

        String archiveFsDir = pluginInfo.getString(JobResultConstant.ARCHIVE);

        if (StringUtils.isNotBlank(archiveFsDir)) {
            LOGGER.info("update jobId {} ldap archiveFsDir {}", jobClient.getJobId(), archiveFsDir);
            String jobExtraInfo = scheduleJobExpandService.getJobExtraInfo(jobClient.getJobId());
            JSONObject jsonObject;
            if (StringUtils.isBlank(jobExtraInfo)) {
                jsonObject = new JSONObject();
            } else {
                jsonObject = JSONObject.parseObject(jobExtraInfo);
            }
            jsonObject.put(JobResultConstant.ARCHIVE, archiveFsDir);
            scheduleJobExpandService.updateExtraInfo(jobClient.getJobId(), jsonObject.toJSONString());
        }
    }

    private void handlerFailedWithRetry(JobClient jobClient, boolean checkRetry, Throwable e) {
        LOGGER.error("jobId:{} taskType:{} submitJob happens system error:", jobClient.getJobId(), jobClient.getTaskType(), e);
        jobClient.setEngineTaskId(null);
        addToTaskListener(jobClient, JobResult.createErrorResult(checkRetry, e));
    }

    private void handlerNoResource(JobClient jobClient, JudgeResult judgeResult) {
        //因为资源不足提交任务失败，优先级数值增加 WAIT_INTERVAL
        jobClient.setPriority(jobClient.getPriority() + jobPriorityStep);

        //delayQueue的任务比重过大时，直接放入优先级队列重试
        if (jobClient.lackingCountIncrement() > jobLackingCountLimited && delayJobQueue.size() < priorityQueue.getQueueSizeLimited()) {
            putLackingJob(jobClient, judgeResult);
        } else {
            ScheduleJobCacheService.updateStage(jobClient.getJobId(), EJobCacheStage.PRIORITY.getStage(), localAddress, jobClient.getPriority(), null);
            queue.put(jobClient);
            SleepUtil.sleep(jobLackingInterval);
            LOGGER.info("jobId:{} unlimited_lackingCount:{} add to priorityQueue.", jobClient.getJobId(), jobClient.getLackingCount());

        }
    }

    private void addToTaskListener(JobClient jobClient, JobResult jobResult) {
        jobClient.setJobResult(jobResult);
        //添加触发读取任务状态消息
        submittedQueue.offer(jobClient);
    }

    public static LinkedBlockingQueue<JobClient> getSubmittedQueue() {
        return submittedQueue;
    }
}
