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

package com.dtstack.taier.scheduler.jobdealer.bo;


import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobRetry;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;

/**
 * @author toutian
 */
public class JobRetry extends ScheduleJobRetry {

    public static JobRetry toEntity(ScheduleJob scheduleJob, JobClient jobClient, String engineLog) {
        JobRetry scheduleJobRetry = new JobRetry();
        scheduleJobRetry.setJobId(scheduleJob.getJobId());
        scheduleJobRetry.setExecStartTime(scheduleJob.getExecStartTime());
        scheduleJobRetry.setExecEndTime(scheduleJob.getExecEndTime());
        scheduleJobRetry.setRetryNum(scheduleJob.getRetryNum() + 1);
        scheduleJobRetry.setStatus(scheduleJob.getStatus());
        scheduleJobRetry.setGmtCreate(scheduleJob.getGmtCreate());
        scheduleJobRetry.setGmtModified(scheduleJob.getGmtModified());
        scheduleJobRetry.setEngineLog(engineLog);

        if (scheduleJob.getApplicationId() == null) {
            scheduleJobRetry.setApplicationId(jobClient.getApplicationId());
        } else {
            scheduleJobRetry.setApplicationId(scheduleJob.getApplicationId());
        }
        if (scheduleJob.getEngineJobId() == null) {
            scheduleJobRetry.setEngineJobId(jobClient.getEngineTaskId());
        } else {
            scheduleJobRetry.setEngineJobId(scheduleJob.getEngineJobId());
        }
        try {
            if (jobClient.getJobResult() != null) {
                scheduleJobRetry.setLogInfo(jobClient.getJobResult().getMsgInfo());
            }
        } catch (Throwable e) {
            scheduleJobRetry.setLogInfo("commit job error，parses log error:" + ExceptionUtil.getErrorMessage(e));
        }
        return scheduleJobRetry;
    }

}
