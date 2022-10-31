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


package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class JobHistoryVO {


    @ApiModelProperty(value = "jobId")
    private String jobId;

    @ApiModelProperty(value = "执行开始时间")
    private Date execStartTime;

    @ApiModelProperty(value = "执行结束时间")
    private Date execEndTime;

    @ApiModelProperty(value = "engineJobId")
    private String engineJobId;

    @ApiModelProperty(value = "applicationId")
    private String applicationId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Date execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Date getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Date execEndTime) {
        this.execEndTime = execEndTime;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
