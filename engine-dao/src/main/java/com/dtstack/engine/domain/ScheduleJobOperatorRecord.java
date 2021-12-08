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

import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;


@TableName("schedule_job_operator_record")
public class ScheduleJobOperatorRecord extends BaseEntity {

    private String jobId;
    private Integer forceCancelFlag;
    private int version;
    private Date operatorExpired;
    private Integer operatorType;
    private String nodeAddress;

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getForceCancelFlag() {
        return forceCancelFlag;
    }

    public void setForceCancelFlag(Integer forceCancelFlag) {
        this.forceCancelFlag = forceCancelFlag;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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


    @Override
    public String toString() {
        return "EngineJobStopRecord{" +
                "jobId='" + jobId + '\'' +
                ", version='" + version + '\'' +
                ", operatorExpired='" + operatorExpired + '\'' +
                '}';
    }

}
