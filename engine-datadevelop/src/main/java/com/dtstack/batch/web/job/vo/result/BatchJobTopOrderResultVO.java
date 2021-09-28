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

package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("运行时长top排序返回信息")
public class BatchJobTopOrderResultVO {
    @ApiModelProperty(value = "运行时间", example = "10")
    private String runTime;

    @ApiModelProperty(value = "创建用户", example = "test")
    private String createUser;

    @ApiModelProperty(value = "任务名称", example = "taskName")
    private String taskName;

    @ApiModelProperty(value = "类型",example = "1")
    private Integer type;

    @ApiModelProperty(value = "任务调度时间", example = "yyyymmddhhmmss")
    private String cycTime;

    @ApiModelProperty(value = "任务 ID", example = "1")
    private Long taskId = 0L;

    @ApiModelProperty(value = "任务实例ID", example = "100")
    private Long jobId = 0L;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "任务类型名称", example = "1")
    private String taskTypeName;

    @ApiModelProperty(value = "创建用户id", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr' 2 sync", example = "1")
    private Integer taskType;
}
