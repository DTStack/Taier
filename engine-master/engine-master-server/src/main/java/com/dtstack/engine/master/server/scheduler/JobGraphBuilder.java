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

package com.dtstack.engine.master.server.scheduler;

import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.master.druid.DtDruidRemoveAbandoned;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.DateUtil;
import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 1. 变为Master节点时会主动触发一次是否构建jobgraph的判断
 * 2. 定时任务调度时触发
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
@Component
public class JobGraphBuilder extends AbstractBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphBuilder.class);

    /**
     * 系统调度的时候插入的默认batch_job名称
     */
    private static final String CRON_JOB_NAME = "cronJob";
    private static final String CRON_TRIGGER_TYPE = "cronTrigger";
    private static final String NORMAL_TASK_FLOW_ID = "0";

    public static final List<Integer> SPECIAL_TASK_TYPES = Lists.newArrayList(EScheduleJobType.WORK_FLOW.getVal());

    private static final int TASK_BATCH_SIZE = 50;
    private static final int JOB_BATCH_SIZE = 50;
    private static final int MAX_TASK_BUILD_THREAD = 20;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    private Lock lock = new ReentrantLock();

    private volatile boolean isBuildError = false;

    /**
     * 1：如果当前节点是master-->每天晚上10点预先生成第二天的任务依赖;
     * 2：如果初始化master节点-->获取当天的jobgraph为null-->生成
     * 可能多线程调用
     *
     * @param triggerDay yyyy-MM-dd
     * @return
     */
    public void buildTaskJobGraph(String triggerDay) {

        if (environmentContext.getJobGraphBuilderSwitch()) {
            return;
        }

        lock.lock();

        try {
            isBuildError = false;
            //检查是否已经生成过
            String triggerTimeStr = triggerDay + " 00:00:00";
            Timestamp triggerTime = Timestamp.valueOf(triggerTimeStr);

            boolean hasBuild = jobGraphTriggerService.checkHasBuildJobGraph(triggerTime);
            if (hasBuild) {
                LOGGER.info("trigger Day {} has build so break", triggerDay);
                return;
            }
            //清理周期实例脏数据
            cleanDirtyJobGraph(triggerDay);

            int totalTask = 0;
            totalTask = getTotalTask();
            LOGGER.info("Counting task which status=SUBMIT scheduleStatus=NORMAL totalTask:{}", totalTask);

            if (totalTask <= 0) {
                return;
            }

            ExecutorService jobGraphBuildPool = new ThreadPoolExecutor(MAX_TASK_BUILD_THREAD, MAX_TASK_BUILD_THREAD, 10L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(MAX_TASK_BUILD_THREAD), new CustomThreadFactory("JobGraphBuilder"));

            Map<String,List<String>> allFlowJobs = Maps.newHashMap();
            final Long[] minId = {0L};
            Map<String, String> flowJobId = new ConcurrentHashMap<>(totalTask);
            //限制 thread 并发
            int totalBatch = totalTask / TASK_BATCH_SIZE;
            if (totalTask % TASK_BATCH_SIZE != 0) {
                totalBatch++;
            }
            Semaphore buildSemaphore = new Semaphore(MAX_TASK_BUILD_THREAD);
            CountDownLatch ctl = new CountDownLatch(totalBatch);
            long startId = 0L;
            int i = 0;
            AtomicInteger count = new AtomicInteger();

            while (true) {
                final int batchIdx = ++i;
                if (batchIdx > totalBatch) {
                    break;
                }
                final List<ScheduleTaskShade> batchTaskShades = getScheduleTaskShades(startId);
                if (batchTaskShades.isEmpty()) {
                    break;
                }

                startId = batchTaskShades.get(batchTaskShades.size() - 1).getId();
                LOGGER.info("batch-number:{} startId:{}", batchIdx, startId);

                try {
                    buildSemaphore.acquire();
                    jobGraphBuildPool.execute(() -> {
                        try {
                            List<ScheduleBatchJob> allJobs = Lists.newArrayList();
                            for (ScheduleTaskShade task : batchTaskShades) {
                                try {
                                    List<ScheduleBatchJob> jobRunBeans = RetryUtil.executeWithRetry(() -> {
                                        String cronJobName = CRON_JOB_NAME + "_" + task.getName();
                                        return buildJobRunBean(task, CRON_TRIGGER_TYPE, EScheduleType.NORMAL_SCHEDULE,
                                                true, true, triggerDay, cronJobName, null, task.getTenantId(),count);
                                    }, environmentContext.getBuildJobErrorRetry(), 200, false);
                                    if (CollectionUtils.isNotEmpty(jobRunBeans)) {
                                        jobRunBeans.forEach(job->{
                                            allJobs.add(job);
                                            if (minId[0] == 0L) {
                                                minId[0] = job.getJobExecuteOrder();
                                            } else if (minId[0] > job.getJobExecuteOrder()) {
                                                minId[0] = job.getJobExecuteOrder();
                                            }
                                        });
                                    }


                                    if (SPECIAL_TASK_TYPES.contains(task.getTaskType())) {
                                        for (ScheduleBatchJob jobRunBean : jobRunBeans) {
                                            flowJobId.put(this.buildFlowReplaceId(task.getTaskId(), jobRunBean.getCycTime(), null), jobRunBean.getJobId());
                                        }
                                    }
                                } catch (Throwable e) {
                                    LOGGER.error("build task failure taskId:{} apptype:{}",task.getTaskId(),null, e);
                                }
                            }
                            LOGGER.info("batch-number:{} done!!! allJobs size:{}", batchIdx, allJobs.size());

                            // 填充工作流任务
                            for (ScheduleBatchJob job : allJobs) {
                                String flowIdKey = job.getScheduleJob().getFlowJobId();
                                if (!NORMAL_TASK_FLOW_ID.equals(flowIdKey)) {
                                    // 说明是工作流子任务
                                    String flowId = flowJobId.get(flowIdKey);
                                    if (StringUtils.isBlank(flowId)) {
                                        // 后面更新
                                        synchronized (allFlowJobs) {
                                            List<String> jodIds = allFlowJobs.get(flowIdKey);
                                            if (CollectionUtils.isEmpty(jodIds)) {
                                                jodIds = Lists.newArrayList();
                                            }
                                            jodIds.add(job.getJobId());
                                            allFlowJobs.put(flowIdKey,jodIds);
                                        }
                                    } else {
                                        job.getScheduleJob().setFlowJobId(flowId);
                                    }
                                }
                            }

                            // 插入周期实例
//                            scheduleJobService.insertJobList(allJobs, EScheduleType.NORMAL_SCHEDULE.getType());
                            LOGGER.info("batch-number:{} done!!! allFlowJobs size:{}", batchIdx, allFlowJobs.size());
                        } catch (Throwable e) {
                            LOGGER.error("!!! buildTaskJobGraph  build job error !!!", e);
                        } finally {
                            buildSemaphore.release();
                            ctl.countDown();
                        }
                    });
                } catch (Throwable e) {
                    LOGGER.error("[acquire pool error]:", e);
                    isBuildError = true;
                    throw new RdosDefineException(e);
                }
            }
            ctl.await();
            if (isBuildError) {
                LOGGER.info("buildTaskJobGraph happend error jobSize {}", allFlowJobs.size());
                return;
            }
            LOGGER.info("buildTaskJobGraph all done!!! allJobs size:{}", allFlowJobs.size());
            jobGraphBuildPool.shutdown();

            // 更新未填充的工作流
            for (Map.Entry<String, List<String>> listEntry : allFlowJobs.entrySet()) {
                String placeholder = listEntry.getKey();
                String flowJob = flowJobId.get(placeholder);


                ScheduleJob scheduleJob = new ScheduleJob();
                if (StringUtils.isNotBlank(flowJob)) {
                    scheduleJob.setFlowJobId(flowJob);
                    scheduleJobService.lambdaUpdate()
                            .eq(ScheduleJob::getFlowJobId,placeholder)
                            .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                            .update(scheduleJob);
                } else {
                    scheduleJob.setFlowJobId(NORMAL_TASK_FLOW_ID);
                    scheduleJobService.lambdaUpdate()
                            .eq(ScheduleJob::getFlowJobId,placeholder)
                            .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                            .update(scheduleJob);
                }
            }

            //存储生成的jobRunBean
            jobGraphBuilder.saveJobGraph(triggerDay, minId[0]);
        } catch (Exception e) {
            LOGGER.error("buildTaskJobGraph ！！！", e);
        } finally {
            LOGGER.info("buildTaskJobGraph exit & unlock ...");
            lock.unlock();
        }
    }

    /**
     * get taskShade need to build job graph
     * @param startId
     * @return
     */
    private List<ScheduleTaskShade> getScheduleTaskShades(Long startId) {
        return scheduleTaskService.listRunnableTask(startId, Lists.newArrayList(EScheduleStatus.NORMAL.getVal(), EScheduleStatus.FREEZE.getVal()), TASK_BATCH_SIZE);
    }

    private int getTotalTask() {
       return scheduleTaskService.lambdaQuery()
                .in(ScheduleTaskShade::getScheduleStatus, Lists.newArrayList(EScheduleStatus.NORMAL.getVal(), EScheduleStatus.FREEZE.getVal()))
                .eq(ScheduleTaskShade::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                .count();
    }

    /**
     * 清理周期实例脏数据
     * @param triggerDay
     */
    private void cleanDirtyJobGraph(String triggerDay) {
        String preCycTime = DateUtil.getTimeStrWithoutSymbol(triggerDay);
        int totalJob = scheduleJobService.countByCycTimeAndJobName(preCycTime, CRON_JOB_NAME, EScheduleType.NORMAL_SCHEDULE.getType());
        if (totalJob <= 0) {
            return;
        }
        LOGGER.info("Start cleaning dirty cron job graph,  totalJob:{}", totalJob);

        int totalBatch;
        if (totalJob % JOB_BATCH_SIZE != 0) {
            totalBatch = totalJob / JOB_BATCH_SIZE + 1;
        } else {
            totalBatch = totalJob / JOB_BATCH_SIZE;
        }
        long startId = 0L;
        int i = 0;

        while (true) {
            final int batchIdx = ++i;
            if (batchIdx > totalBatch) {
                break;
            }
            final List<ScheduleJob> scheduleJobList = scheduleJobService.listByCycTimeAndJobName(startId, preCycTime, CRON_JOB_NAME, EScheduleType.NORMAL_SCHEDULE.getType(), JOB_BATCH_SIZE);
            if (scheduleJobList.isEmpty()) {
                break;
            }
            LOGGER.info("Start clean batchJobList, batch-number:{} startId:{}", batchIdx, startId);
            startId = scheduleJobList.get(scheduleJobList.size() - 1).getId();
            List<String> jobKeyList = new ArrayList<>();
            for ( ScheduleJob scheduleJob : scheduleJobList) {
                jobKeyList.add(scheduleJob.getJobKey());
            }
            scheduleJobService.lambdaUpdate().in(ScheduleJob::getJobKey,jobKeyList).remove();
            LOGGER.info("batch-number:{} done! Cleaning dirty jobs size:{}", batchIdx, scheduleJobList.size());
        }
    }

    /**
     * 保存生成的jobGraph记录
     *
     * @param triggerDay
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @DtDruidRemoveAbandoned
    public boolean saveJobGraph(String triggerDay,Long minJobId) {
        LOGGER.info("start saveJobGraph to db {}", triggerDay);
        //记录当天job已经生成
        String triggerTimeStr = triggerDay + " 00:00:00";
        Timestamp timestamp = Timestamp.valueOf(triggerTimeStr);
        try {
            RetryUtil.executeWithRetry(() -> {
                jobGraphTriggerService.addJobTrigger(timestamp,minJobId);
                return null;
            }, environmentContext.getBuildJobErrorRetry(), 200, false);
        } catch (Exception e) {
            LOGGER.error("addJobTrigger triggerTimeStr {} error ", triggerTimeStr,e);
            throw new RdosDefineException(e);
        }

        return true;
    }



}
