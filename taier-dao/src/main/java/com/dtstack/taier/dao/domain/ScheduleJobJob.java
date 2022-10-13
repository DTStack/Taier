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
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@TableName("schedule_job_job")
public class ScheduleJobJob {

    /**
     * 唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 实例key
     */
    private String jobKey;

    /**
     * 父实例key
     */
    private String parentJobKey;

    /**
     * parentJobKey类型： RelyType
     * 1. 自依赖实例key
     * 2. 上游任务key
     * 3. 上游任务的下一个周期key
     */
    private Integer jobKeyType;

    /**
     * 依赖规则: RelyRule
     * 1. 父实例运行完成，可以运行
     * 2. 父实例运行成功，可以运行
     */
    private Integer rule;

    /**
     * 生成时间
     */
    private Timestamp gmtCreate;

    /**
     * 最近一次修改的时间
     */
    private Timestamp gmtModified;

    /**
     * 是否逻辑删除
     */
    private Integer isDeleted;

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

    public String getJobKey() {
        return jobKey;
    }

    public void setJobKey(String jobKey) {
        this.jobKey = jobKey;
    }

    public String getParentJobKey() {
        return parentJobKey;
    }

    public void setParentJobKey(String parentJobKey) {
        this.parentJobKey = parentJobKey;
    }

    public Integer getJobKeyType() {
        return jobKeyType;
    }

    public void setJobKeyType(Integer jobKeyType) {
        this.jobKeyType = jobKeyType;
    }

    public Integer getRule() {
        return rule;
    }

    public void setRule(Integer rule) {
        this.rule = rule;
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
        ScheduleJobJob that = (ScheduleJobJob) o;
        return Objects.equals(id, that.id) && Objects.equals(tenantId, that.tenantId) && Objects.equals(jobKey, that.jobKey) && Objects.equals(parentJobKey, that.parentJobKey) && Objects.equals(jobKeyType, that.jobKeyType) && Objects.equals(rule, that.rule) && Objects.equals(gmtCreate, that.gmtCreate) && Objects.equals(gmtModified, that.gmtModified) && Objects.equals(isDeleted, that.isDeleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId, jobKey, parentJobKey, jobKeyType, rule, gmtCreate, gmtModified, isDeleted);
    }

    @Override
    public String toString() {
        return "ScheduleJobJob{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", jobKey='" + jobKey + '\'' +
                ", parentJobKey='" + parentJobKey + '\'' +
                ", jobKeyType=" + jobKeyType +
                ", rule=" + rule +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
