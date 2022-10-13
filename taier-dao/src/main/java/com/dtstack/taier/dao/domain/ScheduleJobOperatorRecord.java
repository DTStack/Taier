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
import java.util.Date;
import java.util.Objects;


@TableName("schedule_job_operator_record")
public class ScheduleJobOperatorRecord {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实例id
     */
    private String jobId;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 操作过期时间
     */
    private Date operatorExpired;

    /**
     * 操作类型 0杀死 1重跑 2 补数据
     */
    private Integer operatorType;

    /**
     * 强制标志 0非强制 1强制
     */
    private Integer forceCancelFlag;

    /**
     * 节点地址
     */
    private String nodeAddress;

    /**
     * 创建时间
     */
    private Timestamp gmtCreate;

    /**
     * 最近修改的时间
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getOperatorExpired() {
        return operatorExpired;
    }

    public void setOperatorExpired(Date operatorExpired) {
        this.operatorExpired = operatorExpired;
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

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleJobOperatorRecord that = (ScheduleJobOperatorRecord) o;
        return Objects.equals(id, that.id) && Objects.equals(jobId, that.jobId) && Objects.equals(version, that.version) && Objects.equals(operatorExpired, that.operatorExpired) && Objects.equals(operatorType, that.operatorType) && Objects.equals(forceCancelFlag, that.forceCancelFlag) && Objects.equals(nodeAddress, that.nodeAddress) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, jobId, version, operatorExpired, operatorType, forceCancelFlag, nodeAddress, gmtCreate, gmtModified, isDeleted);
    }

    @Override
    public String toString() {
        return "ScheduleJobOperatorRecord{" +
                "id=" + id +
                ", jobId='" + jobId + '\'' +
                ", version=" + version +
                ", operatorExpired=" + operatorExpired +
                ", operatorType=" + operatorType +
                ", forceCancelFlag=" + forceCancelFlag +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
