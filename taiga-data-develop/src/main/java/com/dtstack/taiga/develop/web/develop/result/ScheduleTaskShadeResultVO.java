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

package com.dtstack.taiga.develop.web.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class ScheduleTaskShadeResultVO {

    @ApiModelProperty(value = "id")
    private Long id = 0L;

    @ApiModelProperty(value = "项目 ID")
    private Long projectId;

    @ApiModelProperty(value = "taskId")
    private Long taskId;

    @ApiModelProperty(value = "产品类型")
    private Integer appType;

    @ApiModelProperty(value = "任务名称", example = "spark_test")
    private String name;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "0")
    private Integer engineType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1")
    private Integer computeType;

    @ApiModelProperty(value = "项目名称", example = "dev")
    private String projectName;

    @ApiModelProperty(value = "租户名称", example = "dtstack")
    private String tenantName;

    @ApiModelProperty(value = "项目别名", example = "dev")
    private String projectAlias;

}
