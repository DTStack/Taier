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
import com.dtstack.taier.pluginapi.JobClient;

/**
 * @author toutian
 */
public class EngineJobRetry extends com.dtstack.taier.dao.domain.ScheduleEngineJobRetry {

    public static EngineJobRetry toEntity(ScheduleJob batchJob, JobClient jobClient) {
        EngineJobRetry batchJobRetry = new EngineJobRetry();
        batchJobRetry.setJobId(batchJob.getJobId());
        batchJobRetry.setExecStartTime(batchJob.getExecStartTime());
        batchJobRetry.setExecEndTime(batchJob.getExecEndTime());
        batchJobRetry.setRetryNum(batchJob.getRetryNum());
        batchJobRetry.setStatus(batchJob.getStatus());

        if (batchJob.getApplicationId() == null) {
            batchJobRetry.setApplicationId(jobClient.getApplicationId());
        } else {
            batchJobRetry.setApplicationId(batchJob.getApplicationId());
        }
        if (batchJob.getEngineJobId() == null) {
            batchJobRetry.setEngineJobId(jobClient.getEngineTaskId());
        } else {
            batchJobRetry.setEngineJobId(batchJob.getEngineJobId());
        }
        return batchJobRetry;
    }

}
