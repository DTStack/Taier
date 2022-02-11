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

package com.dtstack.taier.develop.web.develop.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("调度引擎任务返回结果")
public class BatchScheduleEngineJobResultVO {

    @ApiModelProperty(value = "引擎任务ID", example = "1")
    private String engineJobId;

    @ApiModelProperty(value = "日志详情", example = "1")
    private String logInfo;

    @ApiModelProperty(value = "引擎日志", example = "1")
    private String engineLog;

    @ApiModelProperty(value = "重跑次数", example = "1")
    private Integer retryNum = 0;

    @ApiModelProperty(value = "版本ID", example = "1")
    private Integer versionId;

    @ApiModelProperty(value = "任务实例ID", example = "1")
    private String jobId;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "应用类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "开始时间", example = "1525942614000")
    private Timestamp execStartTime;

    @ApiModelProperty(value = "结束时间", example = "1525942614000")
    private Timestamp execEndTime;

    @ApiModelProperty(value = "当前执行时间单位为s", example = "s")
    private String execTime;

    @ApiModelProperty(value = "租户ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "是否删除", example = "dtstack")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;
}
