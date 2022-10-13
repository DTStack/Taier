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

package com.dtstack.taier.scheduler.event;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public class ScheduleJobBatchEvent {
    private List<String> jobIds;

    private Integer status;

    private ScheduleJobBatchEvent() {
    }

    public ScheduleJobBatchEvent(String jobId, Integer status) {
        this.status = status;
        this.jobIds = Lists.newArrayList(jobId);
    }

    public ScheduleJobBatchEvent(List<String> jobIds, Integer status) {
        this.jobIds = jobIds;
        this.status = status;
    }

    public List<String> getJobIds() {
        return jobIds;
    }

    public void setJobIds(List<String> jobIds) {
        this.jobIds = jobIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ScheduleJobBatchEvent{" +
                "jobIds=" + jobIds +
                ", status=" + status +
                '}';
    }
}
