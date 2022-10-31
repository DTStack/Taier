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

package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Reason:
 * Date: 2017/11/6
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@TableName("schedule_engine_job_cache")
public class ScheduleEngineJobCache {

    /**
     * 唯一标识
     */
    @TableId(value="id", type= IdType.AUTO)
    private Long id;

    /**
     * 周期实例id
     */
    private String jobId;

    /**
     * 实例名称
     */
    private String jobName;

    /**
     * 计算类型stream/batch
     */
    private Integer computeType;

    /**
     * 处于master等待队列：1 还是exe等待队列 2
     */
    private Integer stage;

    /**
     * job信息
     */
    private String jobInfo;

    /**
     * 节点地址
     */
    private String nodeAddress;

    /**
     * job的计算引擎资源类型
     */
    private String jobResource;

    /**
     * 任务优先级
     */
    private Long jobPriority;

    /**
     * 0：不是，1：由故障恢复来的任务
     */
    private Integer isFailover;

    /**
     * 任务等待原因
     */
    private String waitReason;

    /**
     * 租户 id
     */
    private Long tenantId;

    /**
     * 新增时间
     */
    private Timestamp gmtCreate;

    /**
     * 修改时间
     */
    private Timestamp gmtModified;

    /**
     * 0正常 1逻辑删除
     */
    private Integer isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getJobResource() {
        return jobResource;
    }

    public void setJobResource(String jobResource) {
        this.jobResource = jobResource;
    }

    public Long getJobPriority() {
        return jobPriority;
    }

    public void setJobPriority(Long jobPriority) {
        this.jobPriority = jobPriority;
    }

    public Integer getIsFailover() {
        return isFailover;
    }

    public void setIsFailover(Integer isFailover) {
        this.isFailover = isFailover;
    }

    public String getWaitReason() {
        return waitReason;
    }

    public void setWaitReason(String waitReason) {
        this.waitReason = waitReason;
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

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleEngineJobCache that = (ScheduleEngineJobCache) o;
        return Objects.equals(id, that.id) && Objects.equals(jobId, that.jobId) && Objects.equals(jobName, that.jobName) && Objects.equals(computeType, that.computeType) && Objects.equals(stage, that.stage) && Objects.equals(jobInfo, that.jobInfo) && Objects.equals(nodeAddress, that.nodeAddress) && Objects.equals(jobResource, that.jobResource) && Objects.equals(jobPriority, that.jobPriority) && Objects.equals(isFailover, that.isFailover) && Objects.equals(waitReason, that.waitReason) && Objects.equals(tenantId, that.tenantId) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobId, jobName, computeType, stage, jobInfo, nodeAddress, jobResource, jobPriority, isFailover, waitReason, tenantId, gmtCreate, gmtModified, isDeleted);
    }

    @Override
    public String toString() {
        return "EngineJobCache{" +
                "id=" + id +
                ", jobId='" + jobId + '\'' +
                ", jobName='" + jobName + '\'' +
                ", computeType=" + computeType +
                ", stage=" + stage +
                ", jobInfo='" + jobInfo + '\'' +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", jobResource='" + jobResource + '\'' +
                ", jobPriority=" + jobPriority +
                ", isFailover=" + isFailover +
                ", waitReason='" + waitReason + '\'' +
                ", tenantId=" + tenantId +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
