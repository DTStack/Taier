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

import java.util.List;

@Data
@ApiModel("重跑任务结果返回信息")
public class BatchRestartJobResultVO {

    @ApiModelProperty(value = "子任务信息")
    private List<BatchRestartJobResultVO> childs;

    @ApiModelProperty(value = "任务调度时间", example = "yyyymmddhhmmss")
    private String cycTime;

    @ApiModelProperty(value = "任务名称", example = "1")
    private String taskName;

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "任务实例ID", example = "1")
    private String jobId;

    @ApiModelProperty(value = "任务实例主键", example = "1")
    private String jobKey;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer jobStatus;

    @ApiModelProperty(value = "任务ID", example = "name")
    private Long taskId;
}
