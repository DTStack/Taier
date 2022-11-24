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

package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.server.scheduler.interceptor.InterceptorInvocation;
import com.dtstack.taier.scheduler.server.scheduler.interceptor.SubmitInterceptor;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.service.ScheduleTaskShadeService;
import com.dtstack.taier.scheduler.zookeeper.ZkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 3:41 PM
 * @Email: dazhi@dtstack.com
 * @Description: 任务扫描执行器
 */
public abstract class AbstractJobScanningScheduler implements Scheduler, InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(AbstractJobScanningScheduler.class);

    @Autowired
    protected ZkService zkService;

    @Autowired
    protected EnvironmentContext env;

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Autowired
    protected ScheduleTaskShadeService scheduleTaskService;

    /**
     * 获得实例列表
     * @param startSort 开始id
     * @param nodeAddress 查询的实例对应的节点
     * @param isEq sql中是否包含第一个
     * @return 实例列表
     */
    protected abstract List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq);

    /**
     * 获得排序最小序号
     * @return 最小序号
     */
    protected abstract Long getMinSort();

    /**
     * 获得拦截器列表
     *
     * @return 拦截器列表
     */
    protected abstract List<SubmitInterceptor> getInterceptor();


    /**
     * 扫描实例
     */
    private void scanningJob () {
        try {
            if (!env.isOpenJobSchedule()) {
                return;
            }
            String nodeAddress = zkService.getLocalAddress();
            // 1. 获得节点信息
            if (StringUtils.isBlank(nodeAddress)) {
                return;
            }
            LOGGER.info("scanningJob start scheduleType : {} nodeAddress:{}", getSchedulerName(),nodeAddress);

            // 2. 获得排序最小序号
            Long minSort = getMinSort();
            LOGGER.info("scanning start param: scheduleType {} nodeAddress {} minSort {} ", getSchedulerName(), nodeAddress, minSort);

            // 3. 扫描实例
            List<ScheduleJobDetails> scheduleJobDetails = listExecJob(minSort, nodeAddress, Boolean.TRUE);
            while (CollectionUtils.isNotEmpty(scheduleJobDetails)) {
                // 查询任务
                List<Long> taskIds = scheduleJobDetails.stream().map(ScheduleJobDetails::getScheduleJob).map(ScheduleJob::getTaskId).collect(Collectors.toList());
                Map<Long, ScheduleTaskShade> scheduleTaskShadeMap = scheduleTaskService.lambdaQuery()
                        .in(ScheduleTaskShade::getTaskId, taskIds)
                        .eq(ScheduleTaskShade::getIsDeleted, Deleted.NORMAL.getStatus())
                        .list()
                        .stream()
                        .collect(Collectors.toMap(ScheduleTaskShade::getTaskId, g -> (g)));

                for (ScheduleJobDetails scheduleJobDetail : scheduleJobDetails) {
                    // 提交实例
                    ScheduleJob scheduleJob = scheduleJobDetail.getScheduleJob();
                    ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeMap.get(scheduleJob.getTaskId());

                    if (scheduleTaskShade == null) {
                        String errMsg = JobCheckStatus.NO_TASK.getMsg();
                        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(), TaskStatus.AUTOCANCELED.getStatus(), errMsg);
                        LOGGER.warn("jobId:{} scheduleType:{} submit failed for taskId:{} already deleted.", scheduleJob.getJobId(), getSchedulerName(), scheduleJob.getTaskId());
                        continue;
                    }
                    scheduleJobDetail.setScheduleTaskShade(scheduleTaskShade);

                    // 提交任务
                    InterceptorInvocation interceptorInvocation = new InterceptorInvocation(this, getInterceptor());
                    interceptorInvocation.submit(scheduleJobDetail);

                    if (minSort < scheduleJob.getJobExecuteOrder()) {
                        minSort = scheduleJob.getJobExecuteOrder();
                    }
                }
                scheduleJobDetails = listExecJob(minSort, nodeAddress, Boolean.FALSE);
            }
        } catch (Exception e) {
            LOGGER.error("scheduleType:{} emitJob2Queue error:", getSchedulerName(), e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("Initializing scheduleType:{} acquireQueueJobInterval:{} queueSize:{}", getSchedulerName(), env.getJobAcquireQueueJobInterval(), env.getQueueSize());
        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(getSchedulerName() + "_AcquireJob"));
        scheduledService.scheduleWithFixedDelay(this::scanningJob, 0, env.getJobAcquireQueueJobInterval(), TimeUnit.MILLISECONDS);
    }


}
