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

package com.dtstack.taier.scheduler.server.scheduler.interceptor;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.JobCheckStatus;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.enums.RelyRule;
import com.dtstack.taier.scheduler.enums.RelyType;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/3/13 12:40 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobUpStreamSubmitInterceptor extends SubmitInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobUpStreamSubmitInterceptor.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Integer getSort() {
        return 0;
    }

    @Override
    public Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails) {
        List<ScheduleJobJob> jobJobList = scheduleJobDetails.getJobJobList();
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();

        if (CollectionUtils.isNotEmpty(jobJobList)) {
            List<String> parentJobKeys = jobJobList.stream()
                    .map(ScheduleJobJob::getParentJobKey)
                    .filter(key -> !key.equals(scheduleJob.getJobKey()))
                    .collect(Collectors.toList());

            Map<String, ScheduleJob> scheduleJobMap = scheduleJobService.lambdaQuery()
                    .select(ScheduleJob::getStatus, ScheduleJob::getJobId,
                            ScheduleJob::getJobKey, ScheduleJob::getJobName)
                    .in(ScheduleJob::getJobKey, parentJobKeys)
                    .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                    .list().stream().collect(Collectors.toMap(ScheduleJob::getJobKey, g -> (g)));


            for (ScheduleJobJob scheduleJobJob : jobJobList) {
                ScheduleJob parentScheduleJob = scheduleJobMap.get(scheduleJobJob.getParentJobKey());
                // 父实例没有生成 这里有两种情况：
                // 1. 补数据实例父实例没有生成,就不去判断父实例状态
                // 2. 周期实例父实例没有生成，就直接设置成实例失败，原因是父实例没有生成
                if (parentScheduleJob == null) {
                    if (EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType())) {
                        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(),
                                TaskStatus.FAILED.getStatus(),
                                String.format(
                                        JobCheckStatus.FATHER_NO_CREATED.getMsg(),
                                        scheduleJob.getJobName(),
                                        scheduleJob.getJobId(),
                                        scheduleJobJob.getParentJobKey()));
                        return Boolean.FALSE;
                    } else {
                        continue;
                    }
                }

                // 判断上游依赖规则
                //  1. 父实例运行完成，可以运行,也就是说父实例状态不影响子任务状态
                //  2. 父实例运行成功，可以运行,也就是说父实例影响子任务状态
                Integer rule = scheduleJobJob.getRule();
                Integer status = parentScheduleJob.getStatus();
                if (RelyRule.RUN_SUCCESS.getType().equals(rule)) {
                    Integer jobKeyType = scheduleJobJob.getJobKeyType();
                    // 父任务有运行失败的
                    if (TaskStatus.FAILED.getStatus().equals(status)
                            || TaskStatus.SUBMITFAILD.getStatus().equals(status)
                            || TaskStatus.PARENTFAILED.getStatus().equals(status)) {
                        LOGGER.info("jobId:{}, parent job:{} {}, unable put to queue", scheduleJob.getJobId(), parentScheduleJob.getJobId(), status);
                        return Boolean.FALSE;
                    }

                    // 父实例是冻结(但是这些实例不能是自依赖,自依赖实例是用自己任务的状态判断是否冻结)
                    if (TaskStatus.FROZEN.getStatus().equals(status)
                            && !RelyType.SELF_RELIANCE.getType().equals(jobKeyType)) {
                        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(),
                                TaskStatus.FROZEN.getStatus(),
                                String.format(JobCheckStatus.FATHER_JOB_FROZEN.getMsg(),
                                        parentScheduleJob.getJobName(),
                                        parentScheduleJob.getJobId()));
                        return Boolean.FALSE;
                    }

                    // 父实例是取消
                    if (TaskStatus.CANCELED.getStatus().equals(status)
                            || TaskStatus.KILLED.getStatus().equals(status)
                            || TaskStatus.AUTOCANCELED.getStatus().equals(status)) {
                        scheduleJobService.updateStatusAndLogInfoById(scheduleJob.getJobId(),
                                TaskStatus.KILLED.getStatus(),
                                String.format(JobCheckStatus.DEPENDENCY_JOB_CANCELED.getMsg(),
                                        scheduleJob.getJobName(),
                                        scheduleJob.getJobId(),
                                        parentScheduleJob.getJobName(),
                                        parentScheduleJob.getJobId()));
                        return Boolean.FALSE;
                    }
                }

                if (!TaskStatus.FINISHED.getStatus().equals(status) &&
                        !TaskStatus.MANUALSUCCESS.getStatus().equals(status)) {
                    return Boolean.FALSE;
                }
            }
        }

        return super.beforeSubmit(scheduleJobDetails);
    }
}
