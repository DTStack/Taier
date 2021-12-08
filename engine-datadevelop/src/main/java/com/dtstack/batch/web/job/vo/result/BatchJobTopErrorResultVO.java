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
@ApiModel("近30天任务出错排行")
public class BatchJobTopErrorResultVO {

    @ApiModelProperty(value = "任务id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "错误数量", example = "2")
    private Integer errorCount = 0;

    @ApiModelProperty(value = "任务名称", example = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createUser;
}
