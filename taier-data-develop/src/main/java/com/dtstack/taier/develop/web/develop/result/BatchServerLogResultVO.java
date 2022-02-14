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
import java.util.Map;

@Data
@ApiModel("根据jobId获取日志结果信息")
public class BatchServerLogResultVO {

    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType = 0;

    @ApiModelProperty(value = "日志详情", example = "1")
    private String logInfo;

    @ApiModelProperty(value = "日志类型", example = "1")
    private String name;

    @ApiModelProperty(value = "开始时间", example = "2020-07-20 10:50:46")
    private Timestamp execStartTime;

    @ApiModelProperty(value = "结束时间", example = "2020-07-20 10:50:46")
    private Timestamp execEndTime;

    @ApiModelProperty(value = "总页数", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex;

    @ApiModelProperty(value = "计算类型", example = "1")
    private Integer computeType = 0;

    @ApiModelProperty(value = "读取数量", example = "1")
    private Integer readNum = 0;

    @ApiModelProperty(value = "写入数量", example = "1")
    private Integer writeNum = 0;

    @ApiModelProperty(value = "目录", example = "0.0")
    private Float dirtyPercent = 0.0F;

    @ApiModelProperty(value = "exe时间", example = "1")
    private Long execTime = 0L;

    @ApiModelProperty(value = "下载日志", example = "1")
    private String downloadLog;

    @ApiModelProperty(value = "sub节点下载日志")
    private Map<String, String> subNodeDownloadLog;
}
