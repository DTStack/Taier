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

package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取任务的状态统计信息")
public class BatchJobQueryJobsStatusStatisticsVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "开始日期", example = "1525942614000")
    private Long bizStartDay;

    @ApiModelProperty(value = "结束日期", example = "1525942614000")
    private Long bizEndDay;

    @ApiModelProperty(value = "实例状态", example = "1")
    private String jobStatuses;

    @ApiModelProperty(value = "任务名称", example = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务类型", example = "1")
    private String taskType;

    @ApiModelProperty(value = "负责ID", example = "1L")
    private Long ownerId;

    @ApiModelProperty(value = "类型",example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage;

    @ApiModelProperty(value = "总页数", example = "10")
    private Integer pageSize;

    @ApiModelProperty(value = "exec时间", example = "1525942614000")
    private Long execTime;

    @ApiModelProperty(value = "exec时间排序", example = "1525942614000")
    private String execTimeSort;

    @ApiModelProperty(value = "exec开始时间排序", example = "1525942614000")
    private String execStartSort;

    @ApiModelProperty(value = "exec结束时间排序", example = "1525942614000")
    private String execEndSort;

    @ApiModelProperty(value = "cyc排序", example = "desc")
    private String cycSort;

    @ApiModelProperty(value = "商业日期排序", example = "desc")
    private String businessDateSort;

    @ApiModelProperty(value = "重跑数量排序", example = "desc")
    private String retryNumSort;

    @ApiModelProperty(value = "补数据任务名称", example = "1")
    private String fillTaskName;

    @ApiModelProperty(value = "cyc开始日期", example = "1609084800")
    private Long cycStartDay;

    @ApiModelProperty(value = "cyc结束日期", example = "1609084800")
    private Long cycEndDay;

    @ApiModelProperty(value = "exec开始日期", example = "1525942614000")
    private Long execStartDay;

    @ApiModelProperty(value = "exec结束日期", example = "1525942614000")
    private Long execEndDay;

    @ApiModelProperty(value = "分隔符", example = "false")
    private Boolean splitFiledFlag;

    @ApiModelProperty(value = "app类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "任务Id", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "项目Id列表")
    private List<Long> projectIds;

    @ApiModelProperty(value = "任务Id列表")
    private List<Long> taskIds;

    @ApiModelProperty(value = "调度周期",example = "1")
    private String taskPeriodId;
}
