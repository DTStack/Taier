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

package com.dtstack.engine.master.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class KillJobVO {

    private Long tenantId;

    private Long projectId;
    /**
     *  业务日期
     */
    @ApiModelProperty(notes = "业务日期")
    private Long bizStartDay;

    /**
     * 业务日期
     */
    @ApiModelProperty(notes = "业务日期")
    private Long bizEndDay;

    /**
     * 0周期任务；1补数据实例。
     */
    @ApiModelProperty(notes = "0周期任务；1补数据实例。")
    private Integer type;

    /**
     * 调度周期,用逗号分割
     */
    @ApiModelProperty(notes = "调度周期,用逗号分割")
    private String taskPeriodId;

    /**
     * 任务状态，不能是成功状态。用逗号分割
     */
    @ApiModelProperty(notes = "任务状态，不能是成功状态。用逗号分割")
    private String jobStatuses;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getBizStartDay() {
        return bizStartDay;
    }

    public void setBizStartDay(Long bizStartDay) {
        this.bizStartDay = bizStartDay;
    }

    public Long getBizEndDay() {
        return bizEndDay;
    }

    public void setBizEndDay(Long bizEndDay) {
        this.bizEndDay = bizEndDay;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(String taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public String getJobStatuses() {
        return jobStatuses;
    }

    public void setJobStatuses(String jobStatuses) {
        this.jobStatuses = jobStatuses;
    }
}
