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

package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.MapUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author toutian
 */
@ApiModel
public class EngineJobStopRecord extends DataObject {

    private String taskId;
    private Integer taskType;
    private String engineType;
    private Integer computeType;
    private String jobResource;
    private Integer forceCancelFlag;
    private int version;
    private Date operatorExpired;


    public Integer getForceCancelFlag() {
        return forceCancelFlag;
    }

    public void setForceCancelFlag(Integer forceCancelFlag) {
        this.forceCancelFlag = forceCancelFlag;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getOperatorExpired() {
        return operatorExpired;
    }

    public void setOperatorExpired(Date operatorExpired) {
        this.operatorExpired = operatorExpired;
    }

    public static EngineJobStopRecord toEntity(Map<String, Object> jsrMap) {
        EngineJobStopRecord jobStopRecord = new EngineJobStopRecord();
        jobStopRecord.setTaskId(MapUtils.getString(jsrMap, "taskId"));
        jobStopRecord.setTaskType(MapUtils.getInteger(jsrMap, "taskType"));
        jobStopRecord.setEngineType(MapUtils.getString(jsrMap, "engineType"));
        jobStopRecord.setComputeType(MapUtils.getInteger(jsrMap, "computeType"));
        jobStopRecord.setJobResource(MapUtils.getString(jsrMap, "jobResource"));
        return jobStopRecord;
    }


    @Override
    public String toString() {
        return "EngineJobStopRecord{" +
                "taskId='" + taskId + '\'' +
                ", taskType=" + taskType +
                ", engineType='" + engineType + '\'' +
                ", computeType=" + computeType +
                ", jobResource='" + jobResource + '\'' +
                ", version='" + version + '\'' +
                ", operatorExpired='" + operatorExpired + '\'' +
                '}';
    }

}
