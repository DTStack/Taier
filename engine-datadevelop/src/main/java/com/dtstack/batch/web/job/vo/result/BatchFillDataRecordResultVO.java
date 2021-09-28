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

import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskResultVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("补数据记录返回信息")
public class BatchFillDataRecordResultVO {

    @ApiModelProperty(value = "补数据记录ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "日期", example = "2020-12-24 17:33:25")
    private String bizDay;

    @ApiModelProperty(value = "用户名称", example = "1")
    private String dutyUserName;

    @ApiModelProperty(value = "调度任务信息", example = "1")
    private BatchScheduleTaskResultVO batchTask;

    @ApiModelProperty(value = "任务实例名称", example = "name")
    private String jobName;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "任务实例ID", example = "1")
    private String jobId;

    @ApiModelProperty(value = "重试次数", example = "3")
    private Integer retryNum;

    @ApiModelProperty(value = "子记录")
    private List<BatchFillDataRecordResultVO> relatedRecords;

    @ApiModelProperty(value = "是否是脏数据", example = "1")
    private Integer isDirty;

    @ApiModelProperty(value = "工作流ID", example = "3")
    private String flowJobId;

    @ApiModelProperty(value = "是否重启", example = "1")
    private Integer isRestart;

    @ApiModelProperty(value = "任务类型", example = "3")
    private Integer taskType;

    @ApiModelProperty(value = "任务调度时间", example = "yyyymmddhhmmss")
    private String cycTime;

    @ApiModelProperty(value = "开始时间", example = "1525942614000")
    private String exeStartTime;

    @ApiModelProperty(value = "运行时长", example = "1525942614000")
    private String exeTime;

}
