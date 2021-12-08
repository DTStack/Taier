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

package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("任务信息")
public class BatchTaskShadePageQueryResultVO {

    @ApiModelProperty(value = "id", example = "3")
    private Long id;

    @ApiModelProperty(value = "任务名称", example = "test")
    private String taskName;

    @ApiModelProperty(value = "任务类别", example = "3")
    private Integer taskType;

    @ApiModelProperty(value = "创建用户", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "负责人", example = "admin")
    private String chargeUser;

    @ApiModelProperty(value = "修改用户", example = "admin")
    private String modifyUser;

    @ApiModelProperty(value = "修改时间")
    private Timestamp modifyTime;

    @ApiModelProperty(value = "任务描述", example = "1")
    private String taskDesc;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted;

}
