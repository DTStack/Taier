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

package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("任务详情信息")
public class BatchJobFindTaskRuleJobResultVO {

    @ApiModelProperty(value = "任务名", example = "spark_task")
    private String name;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "租户名称", example = "dtstack")
    private String tenantName;

    @ApiModelProperty(value = "产品类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "规则类型", example = "0：无规则 1：弱规则 2：强规则")
    private Integer taskRule;

    @ApiModelProperty(value = "绑定的规则任务")
    private List<BatchJobFindTaskRuleJobResultVO> scheduleDetailsVOList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getTaskRule() {
        return taskRule;
    }

    public void setTaskRule(Integer taskRule) {
        this.taskRule = taskRule;
    }

    public List<BatchJobFindTaskRuleJobResultVO> getScheduleDetailsVOList() {
        return scheduleDetailsVOList;
    }

    public void setScheduleDetailsVOList(List<BatchJobFindTaskRuleJobResultVO> scheduleDetailsVOList) {
        this.scheduleDetailsVOList = scheduleDetailsVOList;
    }
}
