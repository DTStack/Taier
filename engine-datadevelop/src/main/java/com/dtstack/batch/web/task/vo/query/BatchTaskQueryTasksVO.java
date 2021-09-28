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

package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskQueryTasksVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务名称", example = "test", required = true)
    private String name;

    @ApiModelProperty(value = "任务拥有人 ID", example = "1")
    private Long ownerId;

    @ApiModelProperty(value = "开始时间", example = "176312314")
    private Long startTime;

    @ApiModelProperty(value = "截止时间", example = "176312314")
    private Long endTime;

    @ApiModelProperty(value = "调度状态", example = "1")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "任务类别 list", example = "")
    private String taskType;

    @ApiModelProperty(value = "周期类别 list", example = "0,1")
    private String taskPeriodId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "展示页面", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "查询类型", example = "", required = true)
    private String searchType;
}
