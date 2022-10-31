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

package com.dtstack.taier.develop.vo.fill;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 4:46 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataJobVO {

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id",example = "")
    private String jobId;

    /**
     * 实例状态
     */
    @ApiModelProperty(value = "实例状态",example = "0")
    private Integer status;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称",example = "0")
    private String taskName;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型",example = "0")
    private Integer taskType;

    /**
     * 计划时间
     */
    @ApiModelProperty(value = "计划时间",example = "2021-12-24 16:11:53")
    private String cycTime;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间",example = "2021-12-24 16:11:53")
    private String startExecTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间",example = "2021-12-24 16:11:53")
    private String endExecTime;

    /**
     * 运行时长
     */
    @ApiModelProperty(value = "运行时长",example = "0")
    private String execTime;

    /**
     * 重试次数
     */
    @ApiModelProperty(value = "重试次数",example = "0")
    private Integer retryNum;

    /**
     * 操作名称
     */
    @ApiModelProperty(value = "责任人",example = "\tadmin@dtstack.com")
    private String operatorName;

    /**
     * 操作人
     */
    @ApiModelProperty(value = "责任人id",example = "1")
    private Long operatorId;

    /**
     * 工作流id
     */
    @ApiModelProperty(value = "工作流id",example = "1")
    private String flowJobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public String getStartExecTime() {
        return startExecTime;
    }

    public void setStartExecTime(String startExecTime) {
        this.startExecTime = startExecTime;
    }

    public String getEndExecTime() {
        return endExecTime;
    }

    public void setEndExecTime(String endExecTime) {
        this.endExecTime = endExecTime;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }
}
