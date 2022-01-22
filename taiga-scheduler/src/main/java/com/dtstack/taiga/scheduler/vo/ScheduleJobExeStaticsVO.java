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

package com.dtstack.taiga.scheduler.vo;


import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
@ApiModel
public class ScheduleJobExeStaticsVO {

    private int taskType;

    private int cronExeNum;

    private int fillDataExeNum;

    private int failNum;

    private List<BatchJobInfo> jobInfoList = new ArrayList<>();

    public void addBatchJob(BatchJobInfo jobInfo) {
        jobInfoList.add(jobInfo);
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public int getCronExeNum() {
        return cronExeNum;
    }

    public void setCronExeNum(int cronExeNum) {
        this.cronExeNum = cronExeNum;
    }

    public int getFillDataExeNum() {
        return fillDataExeNum;
    }

    public void setFillDataExeNum(int fillDataExeNum) {
        this.fillDataExeNum = fillDataExeNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public List<BatchJobInfo> getJobInfoList() {
        return jobInfoList;
    }

    public void setJobInfoList(List<BatchJobInfo> jobInfoList) {
        this.jobInfoList = jobInfoList;
    }

    public static class BatchJobInfo {

        private String jobId;

        private Long exeStartTime;

        private Integer exeTime;

        private Integer totalCount;

        private Integer dirtyNum;

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public Long getExeStartTime() {
            return exeStartTime;
        }

        public void setExeStartTime(Long exeStartTime) {
            this.exeStartTime = exeStartTime;
        }

        public Integer getExeTime() {
            return exeTime;
        }

        public void setExeTime(Integer exeTime) {
            this.exeTime = exeTime;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getDirtyNum() {
            return dirtyNum;
        }

        public void setDirtyNum(Integer dirtyNum) {
            this.dirtyNum = dirtyNum;
        }
    }
}
