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

package com.dtstack.taier.develop.dto.devlop;

import io.swagger.annotations.ApiModelProperty;


/**
 * 获取指标详细信息
 *
 * @author ：wangchuan
 * date：Created in 上午11:05 2021/4/16
 * company: www.dtstack.com
 */
public class GetMetricValueVO {

    @ApiModelProperty(value = "UIC 租户 id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "结束时间", example = "1618976997027", required = true)
    private Long end;

    @ApiModelProperty(value = "时间跨度", example = "1m", required = true)
    private String timespan;

    @ApiModelProperty(value = "指标名称", example = "flink_taskmanager_job_task_operator_dtNumRecordsInRate", required = true)
    private String chartName;

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }
}
