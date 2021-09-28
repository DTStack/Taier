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

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("运行报告返回信息")
public class BatchScheduleJobExeStaticsResultVO {

    @ApiModelProperty(value = "任务类型 0 sql，1 mr' 2 sync", example = "1")
    private Integer taskType = 0;

    @ApiModelProperty(value = "cron数量", example = "1")
    private Integer cronExeNum = 0;

    @ApiModelProperty(value = "补数据数量", example = "1")
    private Integer fillDataExeNum = 0;

    @ApiModelProperty(value = "失败数量", example = "1")
    private Integer failNum = 0;

    @ApiModelProperty(value = "任务实例列表")
    private List<BatchJobInfoResultVO> jobInfoList = new ArrayList<>();
}
