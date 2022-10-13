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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.List;

@ApiModel("杀死任务实例信息")
public class ActionJobKillVO {
    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(value = "执行开始日期", example = "1609084800", required = true)
    private Long cycStartDay;

    @ApiModelProperty(value = "执行结束日期", example = "1609084800", required = true)
    private Long cycEndDay;

    @ApiModelProperty(value = "0周期任务；1补数据实例 默认 1", example = "0")
    private Integer type;

    @ApiModelProperty(value = "调度周期")
    private List<Integer> taskPeriods;

    @ApiModelProperty(value = "选择指定任务时，需要传该字段")
    private List<Long> taskIds;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getCycStartDay() {
        return cycStartDay;
    }

    public Timestamp getCycStartTime() {
        if (cycStartDay != null) {
            return new Timestamp(cycStartDay);
        }
        return null;
    }

    public void setCycStartDay(Long cycStartDay) {
        this.cycStartDay = cycStartDay;
    }

    public Long getCycEndDay() {
        return cycEndDay;
    }

    public Timestamp getCycEndTime() {
        if (cycEndDay != null) {
            return new Timestamp(cycEndDay);
        }
        return null;
    }

    public void setCycEndDay(Long cycEndDay) {
        this.cycEndDay = cycEndDay;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public List<Integer> getTaskPeriods() {
        return taskPeriods;
    }

    public void setTaskPeriods(List<Integer> taskPeriods) {
        this.taskPeriods = taskPeriods;
    }

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }
}
