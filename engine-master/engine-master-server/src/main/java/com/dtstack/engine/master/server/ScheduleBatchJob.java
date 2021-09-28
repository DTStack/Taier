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

package com.dtstack.engine.master.server;

import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleJobJob;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Reason:
 * Date: 2018/1/3
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class ScheduleBatchJob {

    private ScheduleJob scheduleJob;

    private List<ScheduleJobJob> jobJobList = Lists.newArrayList();

    /**
     * 下游的任务上一个周期依赖-->null表示还未从数据库拉取过数据
     */
    private List<ScheduleJob> dependencyChildPrePeriodList;

    public ScheduleBatchJob(ScheduleJob scheduleJob) {
        this.scheduleJob = scheduleJob;
    }

    public ScheduleJob getScheduleJob() {
        return scheduleJob;
    }

    public List<ScheduleJobJob> getBatchJobJobList() {
        return jobJobList;
    }

    public void setJobJobList(List<ScheduleJobJob> jobJobList) {
        this.jobJobList = jobJobList;
    }

    public void addBatchJobJob(ScheduleJobJob scheduleJobJob) {
        jobJobList.add(scheduleJobJob);
    }

    public Long getId() {
        return scheduleJob.getId();
    }

    public Integer getStatus() {
        return scheduleJob.getStatus();
    }

    public String getJobId() {
        return scheduleJob.getJobId();
    }

    public String getCycTime() {
        return scheduleJob.getCycTime();
    }

    public String getNextCycTime() {
        return scheduleJob.getNextCycTime();
    }

    public Long getTaskId() {
        return scheduleJob.getTaskId();
    }

    public Integer getAppType() {
        return scheduleJob.getAppType();
    }

    public Integer getScheduleType() {
        return scheduleJob.getType();
    }

    public String getJobKey() {
        return scheduleJob.getJobKey();
    }

    public Integer getIsRestart() {
        return scheduleJob.getIsRestart();
    }

    public List<ScheduleJob> getDependencyChildPrePeriodList() {
        return dependencyChildPrePeriodList;
    }

    public void setDependencyChildPrePeriodList(List<ScheduleJob> dependencyChildPrePeriodList) {
        this.dependencyChildPrePeriodList = dependencyChildPrePeriodList;
    }

    public Long getJobExecuteOrder() {
        return scheduleJob.getJobExecuteOrder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScheduleBatchJob that = (ScheduleBatchJob) o;

        return scheduleJob != null ? scheduleJob.getJobId().equals(that.getJobId()) : that.getJobId() == null;
    }

}
