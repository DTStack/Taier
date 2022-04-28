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


import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.dtstack.taier.common.CustomThreadRunsPolicy;
import com.dtstack.taier.common.enums.*;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.queue.DelayBlockingQueue;
import com.dtstack.taier.dao.domain.ScheduleEngineJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.ParamAction;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.jobdealer.bo.StoppedJob;
import com.dtstack.taier.scheduler.jobdealer.cache.ShardCache;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobOperatorRecordService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/5/26
 */
@Component
public class JobStopDealer implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStopDealer.class);

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobCacheService ScheduleJobCacheService;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    private static final int JOB_STOP_LIMIT = 1000;
    private static final int WAIT_INTERVAL = 3000;
    private static final int OPERATOR_EXPIRED_INTERVAL = 60000;
    private final int asyncDealStopJobQueueSize = 100;
    private final int asyncDealStopJobPoolSize = 10;
    private int jobStoppedRetry;
    private long jobStoppedDelay;

    private final DelayBlockingQueue<StoppedJob<JobElement>> stopJobQueue = new DelayBlockingQueue<StoppedJob<JobElement>>(1000);
    private final ExecutorService delayStopProcessorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), new CustomThreadFactory("delayStopProcessor"));
    private final ExecutorService asyncDealStopJobService = new ThreadPoolExecutor(asyncDealStopJobPoolSize, asyncDealStopJobPoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(asyncDealStopJobQueueSize), new CustomThreadFactory("asyncDealStopJob"), new CustomThreadRunsPolicy("asyncDealStopJob", "stop", 180));
    private final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
    private final DelayStopProcessor delayStopProcessor = new DelayStopProcessor();
    private final AcquireStopJob acquireStopJob = new AcquireStopJob();

    private static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal());

    /**
     * 添加取消实例
     *
     * @param scheduleJobList 需要被取消的任务
     * @return 操作数（取消了任务数）
     */
    public int addStopJobs(List<ScheduleJob> scheduleJobList) {
        return addStopJobs(scheduleJobList, ForceCancelFlag.NO.getFlag());
    }

    /**
     * 添加取消实例
     *
     * @param scheduleJobList 需要被取消的任务
     * @param isForce 是否强制杀死
     * @return 操作数（取消了任务数）
     */
    public int addStopJobs(List<ScheduleJob> scheduleJobList, Integer isForce) {
        if (CollectionUtils.isEmpty(scheduleJobList)) {
            return 0;
        }

        if (scheduleJobList.size() > JOB_STOP_LIMIT) {
            throw new RdosDefineException("please don't stop too many tasks at once, limit:" + JOB_STOP_LIMIT);
        }

        // 分离实例是否提交到yarn上，如果提交到yarn上，需要发送请求stop，如果未提交，直接更新db
        List<ScheduleJob> needSendStopJobs = new ArrayList<>(scheduleJobList.size());
        List<String> unSubmitJobList= new ArrayList<>(scheduleJobList.size());
        for (ScheduleJob job : scheduleJobList) {
            if (isSubmit(job)) {
                unSubmitJobList.add(job.getJobId());
            } else {
                needSendStopJobs.add(job);
            }
        }

        // 查询一遍Operator表，过滤数据
        List<ScheduleJobOperatorRecord> scheduleJobOperatorRecordList = scheduleJobOperatorRecordService.lambdaQuery()
                .in(ScheduleJobOperatorRecord::getJobId, scheduleJobList.stream().map(ScheduleJob::getJobId).collect(Collectors.toList()))
                .eq(ScheduleJobOperatorRecord::getIsDeleted, Deleted.NORMAL.getStatus())
                .eq(ScheduleJobOperatorRecord::getOperatorType,OperatorType.STOP.getType())
                .list();
        List<String> alreadyExistJobIds = scheduleJobOperatorRecordList.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toList());


        // 处理已经提交到yarn的实例状态
        if (CollectionUtils.isNotEmpty(needSendStopJobs)) {
            isForce = Optional.ofNullable(isForce).orElse(ForceCancelFlag.NO.getFlag());
            Integer finalIsForce = isForce;
            List<ScheduleJobOperatorRecord> jobOperatorRecordList = needSendStopJobs.stream()
                    .filter(scheduleJob -> !alreadyExistJobIds.contains(scheduleJob.getJobId()))
                    .map(scheduleJob -> buildScheduleJobOperatorRecord(finalIsForce, scheduleJob)).collect(Collectors.toList());

            scheduleJobOperatorRecordService.saveBatch(jobOperatorRecordList);
        }

        // 更新未提交到yarn实例状态
        if (CollectionUtils.isNotEmpty(unSubmitJobList)) {
            cancellingJob(scheduleJobService.lambdaUpdate().in(ScheduleJob::getJobId, unSubmitJobList));
        }
        return scheduleJobList.size();
    }

    /**
     * 构建OperatorRecord
     *
     * @param finalIsForce 是否强制
     * @param scheduleJob 周期实例
     * @return ScheduleJobOperatorRecord
     */
    private ScheduleJobOperatorRecord buildScheduleJobOperatorRecord(Integer finalIsForce, ScheduleJob scheduleJob) {
        ScheduleJobOperatorRecord jobStopRecord = new ScheduleJobOperatorRecord();
        jobStopRecord.setJobId(scheduleJob.getJobId());
        jobStopRecord.setOperatorType(OperatorType.STOP.getType());
        jobStopRecord.setForceCancelFlag(finalIsForce);
        jobStopRecord.setNodeAddress(environmentContext.getLocalAddress());
        return jobStopRecord;
    }

    /**
     * 把实例状态更新成取消
     *
     * @param scheduleJobService 周期实例
     */
    private void cancellingJob(LambdaUpdateChainWrapper<ScheduleJob> scheduleJobService) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setStatus(TaskStatus.CANCELED.getStatus());
        scheduleJobService
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .update(scheduleJob);
    }

    /**
     * 判断实例是否是已经提交到yarn上
     *
     * @param scheduleJob 周期实例
     * @return 是否提交yarn
     */
    private boolean isSubmit(ScheduleJob scheduleJob) {
        return TaskStatus.UNSUBMIT.getStatus().equals(scheduleJob.getStatus()) || SPECIAL_TASK_TYPES.contains(scheduleJob.getTaskType());
    }


    @Override
    public void afterPropertiesSet() {
        LOGGER.info("Initializing " + this.getClass().getName());

        jobStoppedRetry = environmentContext.getJobStoppedRetry();
        jobStoppedDelay = environmentContext.getJobStoppedDelay();

        delayStopProcessorService.submit(delayStopProcessor);
        scheduledService.scheduleWithFixedDelay(
                acquireStopJob,
                WAIT_INTERVAL,
                WAIT_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() throws Exception {
        delayStopProcessor.close();
        delayStopProcessorService.shutdownNow();
        scheduledService.shutdownNow();
        asyncDealStopJobService.shutdownNow();
        LOGGER.info("job stop process thread is shutdown...");
    }

    private class AcquireStopJob implements Runnable {
        @Override
        public void run() {
            long tmpStartId = 0L;
            Timestamp operatorExpired = new Timestamp(System.currentTimeMillis() + OPERATOR_EXPIRED_INTERVAL);
            while (true) {
                try {
                    //根据条件判断是否有数据存在
                    List<ScheduleJobOperatorRecord> jobStopRecords = scheduleJobOperatorRecordService.listOperatorRecord(tmpStartId, environmentContext.getLocalAddress(), OperatorType.STOP.getType(), true);
                    if (jobStopRecords.isEmpty()) {
                        break;
                    }

                    //使用乐观锁防止多节点重复停止任务
                    Iterator<ScheduleJobOperatorRecord> it = jobStopRecords.iterator();
                    while (it.hasNext()) {
                        ScheduleJobOperatorRecord jobStopRecord = it.next();
                        tmpStartId = jobStopRecord.getId();
                        //已经被修改过version的任务代表其他节点正在处理，可以忽略
                        Integer update = scheduleJobOperatorRecordService.updateOperatorExpiredVersion(jobStopRecord.getId(), operatorExpired, jobStopRecord.getVersion());
                        if (update != 1) {
                            it.remove();
                        }
                    }
                    //经乐观锁判断，经过remove后所剩下的数据
                    if (jobStopRecords.isEmpty()) {
                        break;
                    }
                    List<String> jobIds = jobStopRecords.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toList());
                    List<ScheduleEngineJobCache> jobCaches = ScheduleJobCacheService.getByJobIds(jobIds);

                    //为了下面兼容异常状态的任务停止
                    Map<String, ScheduleEngineJobCache> jobCacheMap = new HashMap<>(jobCaches.size());
                    for (ScheduleEngineJobCache jobCache : jobCaches) {
                        jobCacheMap.put(jobCache.getJobId(), jobCache);
                    }

                    for (ScheduleJobOperatorRecord jobStopRecord : jobStopRecords) {
                        ScheduleEngineJobCache jobCache = jobCacheMap.get(jobStopRecord.getJobId());
                        if (jobCache != null) {
                            //停止任务的时效性，发起停止操作要比任务存入jobCache表的时间要迟
                            if (jobCache.getGmtCreate().after(jobStopRecord.getGmtCreate())) {
                                scheduleJobOperatorRecordService.removeById(jobStopRecord.getId());
                                continue;
                            }

                            boolean forceCancelFlag = ForceCancelFlag.YES.getFlag().equals(jobStopRecord.getForceCancelFlag());
                            JobElement jobElement = new JobElement(jobCache.getJobId(), jobStopRecord.getId(), forceCancelFlag );
                            asyncDealStopJobService.submit(() -> asyncDealStopJob(new StoppedJob<>(jobElement, jobStoppedRetry, jobStoppedDelay)));
                        } else {
                            //jobCache表没有记录，可能任务已经停止。在update表时增加where条件不等于stopped
                            ScheduleJob scheduleJob = new ScheduleJob();
                            scheduleJob.setStatus(TaskStatus.CANCELED.getStatus());
                            scheduleJobService.lambdaUpdate()
                                    .eq(ScheduleJob::getJobId,jobStopRecord.getJobId())
                                    .eq(ScheduleJob::getIsDeleted,Deleted.NORMAL.getStatus())
                                    .in(ScheduleJob::getStatus, TaskStatus.getUnfinishedStatuses())
                                    .update(scheduleJob);
                            LOGGER.info("[Unnormal Job] jobId:{} update job status:{}, job is finished.", jobStopRecord.getJobId(), TaskStatus.CANCELED.getStatus());
                            shardCache.updateLocalMemTaskStatus(jobStopRecord.getJobId(), TaskStatus.CANCELED.getStatus());
                            scheduleJobOperatorRecordService.removeById(jobStopRecord.getId());
                        }
                    }

                    Thread.sleep(500);
                } catch (Throwable e) {
                    LOGGER.error("when acquire stop jobs happens error:", e);
                }
            }
        }
    }

    private class DelayStopProcessor implements Runnable {
        private volatile Boolean open = Boolean.TRUE;

        @Override
        public void run() {
            LOGGER.info("DelayStopProcessor thread is start...");
            while (open) {
                try {
                    StoppedJob<JobElement> stoppedJob = stopJobQueue.take();
                    asyncDealStopJobService.submit(() -> asyncDealStopJob(stoppedJob));
                } catch (InterruptedException ie){
                    LOGGER.warn("interruption of stopJobQueue.take...");
                    break;
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            }
        }

        public void close(){
            open = Boolean.FALSE;
        }

    }

    private void asyncDealStopJob(StoppedJob<JobElement> stoppedJob) {
        try {
            if (!checkExpired(stoppedJob.getJob())) {
                ScheduleEngineJobCache jobCache = ScheduleJobCacheService.getByJobId(stoppedJob.getJob().jobId);
                StoppedStatus stoppedStatus = this.stopJob(stoppedJob.getJob());
                switch (stoppedStatus) {
                    case STOPPED:
                    case MISSED:
                        scheduleJobOperatorRecordService.removeById(stoppedJob.getJob().stopJobId);
                        break;
                    case STOPPING:
                    case RETRY:
                        if (stoppedJob.isRetry()) {
                            if (StoppedStatus.STOPPING == stoppedStatus) {
                                stoppedJob.resetDelay(jobStoppedDelay * stoppedJob.getIncrCount() * 5);
                            } else {
                                stoppedJob.resetDelay(jobStoppedDelay);
                            }
                            stoppedJob.incrCount();
                            stopJobQueue.put(stoppedJob);
                        } else {
                            if (ComputeType.STREAM.getType() == jobCache.getComputeType()) {
                                // stream 任务 超过停止最大限制不更改状态
                                scheduleJobOperatorRecordService.deleteById(stoppedJob.getJob().stopJobId);
                                LOGGER.warn("stream jobId:{} retry limited ,job status can not change!", stoppedJob.getJob().jobId);
                            } else {
                                removeMemStatusAndJobCache(stoppedJob.getJob().jobId);
                                LOGGER.warn("jobId:{} retry limited!", stoppedJob.getJob().jobId);
                            }
                        }
                    default:
                }
            } else {
                scheduleJobOperatorRecordService.removeById(stoppedJob.getJob().stopJobId);
                LOGGER.warn("delete stop record jobId {} stopJobId {} ", stoppedJob.getJob().jobId, stoppedJob.getJob().stopJobId);
            }

        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private StoppedStatus stopJob(JobElement jobElement) throws Exception {
        ScheduleEngineJobCache jobCache = ScheduleJobCacheService.getByJobId(jobElement.jobId);
        ScheduleJob scheduleJob = scheduleJobService.lambdaQuery()
                .eq(ScheduleJob::getJobId, jobElement.jobId)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .one();
        if (jobCache == null) {
            if (scheduleJob != null && TaskStatus.isStopped(scheduleJob.getStatus())) {
                LOGGER.info("jobId:{} stopped success, set job is STOPPED.", jobElement.jobId);
                return StoppedStatus.STOPPED;
            } else {
                this.removeMemStatusAndJobCache(jobElement.jobId);
                LOGGER.info("jobId:{} jobCache is null, set job is MISSED.", jobElement.jobId);
                return StoppedStatus.MISSED;
            }
        } else if (null != scheduleJob && EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
            if (!TaskStatus.getWaitStatus().contains(scheduleJob.getStatus()) || EJobCacheStage.PRIORITY.getStage() != jobCache.getStage()) {
                this.removeMemStatusAndJobCache(jobCache.getJobId());
                LOGGER.info("jobId:{} is unsubmitted, set job is STOPPED.", jobElement.jobId);
                return StoppedStatus.STOPPED;
            } else {
                //任务如果处于提交的状态过程中 但是stage由PRIORITY变更为SUBMITTED  直接删除会导致还是会提交到yarn上 占用资源
                LOGGER.info("jobId:{} is stopping.", jobCache.getJobId());
                return StoppedStatus.STOPPING;
            }
        } else {
            if (scheduleJob == null) {
                this.removeMemStatusAndJobCache(jobElement.jobId);
                LOGGER.info("jobId:{} scheduleJob is null, set job is MISSED.", jobElement.jobId);
                return StoppedStatus.MISSED;
            } else if (TaskStatus.getStoppedAndNotFound().contains(scheduleJob.getStatus())) {
                this.removeMemStatusAndJobCache(jobElement.jobId);
                LOGGER.info("jobId:{} and status:{} is StoppedAndNotFound, set job is STOPPED.", jobElement.jobId, scheduleJob.getStatus());
                return StoppedStatus.STOPPED;
            }


            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
            paramAction.setEngineTaskId(scheduleJob.getEngineJobId());
            paramAction.setApplicationId(scheduleJob.getApplicationId());
            JobClient jobClient = new JobClient(paramAction);
            jobClient.setForceCancel(jobElement.isForceCancel);

            if (StringUtils.isNotBlank(scheduleJob.getEngineJobId()) && !jobClient.getEngineTaskId().equals(scheduleJob.getEngineJobId())) {
                this.removeMemStatusAndJobCache(jobElement.jobId);
                LOGGER.info("jobId:{} stopped success, because of [difference engineJobId].", paramAction.getJobId());
                return StoppedStatus.STOPPED;
            }
            JobResult jobResult = workerOperator.stopJob(jobClient);
            if (jobResult.getCheckRetry()) {
                LOGGER.info("jobId:{} is retry.", paramAction.getJobId());
                return StoppedStatus.RETRY;
            } else {
                LOGGER.info("jobId:{} is stopping.", paramAction.getJobId());
                return StoppedStatus.STOPPING;
            }
        }
    }

    private void removeMemStatusAndJobCache(String jobId) {
        shardCache.removeIfPresent(jobId);
        ScheduleJobCacheService.deleteByJobId(jobId);
        //修改任务状态
        scheduleJobService.updateStatusAndLogInfoById(jobId, TaskStatus.CANCELED.getStatus(),"");
        LOGGER.info("jobId:{} delete jobCache and update job status:{}, job set finished.", jobId, TaskStatus.CANCELED.getStatus());
    }

    private boolean checkExpired(JobElement jobElement) {
        ScheduleEngineJobCache jobCache = ScheduleJobCacheService.getByJobId(jobElement.jobId);
        ScheduleJobOperatorRecord scheduleJobOperatorRecord = scheduleJobOperatorRecordService.getById(jobElement.stopJobId);

        if (jobCache != null && scheduleJobOperatorRecord != null && scheduleJobOperatorRecord.getGmtCreate() != null) {
            return jobCache.getGmtCreate().after(scheduleJobOperatorRecord.getGmtCreate());
        } else {
            return true;
        }
    }

    private class JobElement {

        public String jobId;
        public long stopJobId;
        public boolean isForceCancel;


        public JobElement(String jobId, long stopJobId, boolean isForceCancel) {
            this.jobId = jobId;
            this.stopJobId = stopJobId;
            this.isForceCancel = isForceCancel;
        }
    }
}
