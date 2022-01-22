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

package com.dtstack.taiga.dao.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@TableName("schedule_job")
public class ScheduleJob implements Serializable {

    /**
     * 实例唯一标识
     */
    @TableId(value="id", type= IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * jobKey
     */
    private String jobKey;

    /**
     * 实例名称
     */
    private String jobName;

    /**
     * 任务名称
     */
    private Long taskId;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 修改时间
     */
    private Timestamp gmtModified;

    /**
     * 创建人id
     */
    private Long createUserId;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

    /**
     * 任务类型： 0 周期实例，1补数据 2 立即运行
     */
    private Integer type;

    /**
     * 是否重试
     */
    private Integer isRestart;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 依赖类型
     */
    private Integer dependencyType;

    /**
     * 工作流目标节点
     */
    private String flowJobId;

    /**
     * 调度类型
     * 分钟:MIN(0),
     * 小时:HOUR(1),
     * 天:DAY(2),
     * 周:WEEK(3),
     * 月:MONTH(4),
     * 自定义cron表达式:CRON(5)
     */
    private Integer periodType;

    /**
     * 实例状态
     */
    private Integer status;

    /**
     * 任务类型：
     * 虚节点:VIRTUAL(-)1,
     * SparkSQL:SPARK_SQL(0),
     * Spark:SPARK(1),
     * 数据同步:SYNC(2),
     * Shell: SHELL(3),
     * 工作流:WORK_FLOW(1)
     */
    private Integer taskType;

    /**
     * 补数据id
     */
    private Long fillId;

    /**
     * 实际开始时间
     */
    private Timestamp execStartTime;

    /**
     * 实际结束时间
     */
    private Timestamp execEndTime;

    /**
     * 运行时长
     */
    private Long execTime;

    /**
     * 最大重试次数
     */
    private Integer maxRetryNum;

    /**
     * 当前重试次数
     */
    private Integer retryNum;

    /**
     * 运行实例的ip
     */
    private String nodeAddress;

    /**
     * 实例版本
     */
    private Integer versionId;

    /**
     * 下一个实例计划运行时间
     */
    private String nextCycTime;

    /**
     * yarn运行实例id
     */
    private String engineJobId;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 计算类型
     */
    private Integer computeType;

    /**
     * 阶段类型
     */
    private Integer phaseStatus;

    /**
     * 排序
     */
    private Long jobExecuteOrder;


    /**
     * 补数据实例状态：0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 中间实例
     */
    private Integer fillType;

    /**
     * 任务提交的用户名称
     */
    private String submitUserName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsRestart() {
        return isRestart;
    }

    public void setIsRestart(Integer isRestart) {
        this.isRestart = isRestart;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(Integer dependencyType) {
        this.dependencyType = dependencyType;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Long getFillId() {
        return fillId;
    }

    public void setFillId(Long fillId) {
        this.fillId = fillId;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public Integer getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(Integer maxRetryNum) {
        this.maxRetryNum = maxRetryNum;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public String getNextCycTime() {
        return nextCycTime;
    }

    public void setNextCycTime(String nextCycTime) {
        this.nextCycTime = nextCycTime;
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

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public Integer getPhaseStatus() {
        return phaseStatus;
    }

    public void setPhaseStatus(Integer phaseStatus) {
        this.phaseStatus = phaseStatus;
    }

    public Long getJobExecuteOrder() {
        return jobExecuteOrder;
    }

    public void setJobExecuteOrder(Long jobExecuteOrder) {
        this.jobExecuteOrder = jobExecuteOrder;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getFillType() {
        return fillType;
    }

    public void setFillType(Integer fillType) {
        this.fillType = fillType;
    }

    public String getSubmitUserName() {
        return submitUserName;
    }

    public void setSubmitUserName(String submitUserName) {
        this.submitUserName = submitUserName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleJob that = (ScheduleJob) o;
        return Objects.equals(id, that.id) && Objects.equals(tenantId, that.tenantId) && Objects.equals(jobId, that.jobId) && Objects.equals(jobKey, that.jobKey) && Objects.equals(jobName, that.jobName) && Objects.equals(taskId, that.taskId) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified) && Objects.equals(createUserId, that.createUserId) && Objects.equals(isDeleted, that.isDeleted) && Objects.equals(type, that.type) && Objects.equals(isRestart, that.isRestart) && Objects.equals(cycTime, that.cycTime) && Objects.equals(dependencyType, that.dependencyType) && Objects.equals(flowJobId, that.flowJobId) && Objects.equals(periodType, that.periodType) && Objects.equals(status, that.status) && Objects.equals(taskType, that.taskType) && Objects.equals(fillId, that.fillId) && Objects.equals(execStartTime, that.execStartTime) && Objects.equals(execEndTime, that.execEndTime) && Objects.equals(execTime, that.execTime) && Objects.equals(maxRetryNum, that.maxRetryNum) && Objects.equals(retryNum, that.retryNum) && Objects.equals(nodeAddress, that.nodeAddress) && Objects.equals(versionId, that.versionId) && Objects.equals(nextCycTime, that.nextCycTime) && Objects.equals(engineJobId, that.engineJobId) && Objects.equals(applicationId, that.applicationId) && Objects.equals(computeType, that.computeType) && Objects.equals(phaseStatus, that.phaseStatus) && Objects.equals(jobExecuteOrder, that.jobExecuteOrder) && Objects.equals(fillType, that.fillType) && Objects.equals(submitUserName, that.submitUserName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, jobId, jobKey, jobName, taskId, gmtCreate, gmtModified, createUserId, isDeleted, type, isRestart, cycTime, dependencyType, flowJobId, periodType, status, taskType, fillId, execStartTime, execEndTime, execTime, maxRetryNum, retryNum, nodeAddress, versionId, nextCycTime, engineJobId, applicationId, computeType, phaseStatus, jobExecuteOrder, fillType, submitUserName);
    }
}
