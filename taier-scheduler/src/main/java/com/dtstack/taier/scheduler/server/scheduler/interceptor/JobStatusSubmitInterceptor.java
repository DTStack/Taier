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

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.DateUtil;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2022/3/13 12:36 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class JobStatusSubmitInterceptor extends SubmitInterceptorAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStatusSubmitInterceptor.class);

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public Integer getSort() {
        return 0;
    }

    @Override
    public Boolean beforeSubmit(ScheduleJobDetails scheduleJobDetails) {
        ScheduleJob scheduleJob = scheduleJobDetails.getScheduleJob();
        Integer status = scheduleJobService.getJobStatusByJobId(scheduleJob.getJobId());

        // 判断实例状态是不是等待提交
        if (!TaskStatus.UNSUBMIT.getStatus().equals(status) && !EScheduleJobType.WORK_FLOW.getType().equals(scheduleJobDetails.getScheduleJob().getTaskType())) {
            return Boolean.FALSE;
        }

        // 判断实例是否到达运行时间
        long cycTime = Long.parseLong(scheduleJob.getCycTime());
        long currTime = Long.parseLong(new DateTime().toString(DateUtil.UN_STANDARD_DATETIME_FORMAT));

        if (currTime < cycTime) {
            return Boolean.FALSE;
        }

        return super.beforeSubmit(scheduleJobDetails);
    }
}
