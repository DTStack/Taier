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

@Data
@ApiModel("补数据信息")
public class BatchJobFillTaskDataVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "任务的json数据", example = "[{\"task\":39}]", required = true)
    private String taskJson;

    @ApiModelProperty(value = "补数据名称", example = "P_test_asdasd_2020_12_29_37_20", required = true)
    private String fillName;

    @ApiModelProperty(value = "开始日期", example = "1609084800", required = true)
    private Long fromDay;

    @ApiModelProperty(value = "结束日期", example = "1609171199", required = true)
    private Long toDay;

    @ApiModelProperty(value = "开始时间", example = "2020-10-10", required = true)
    private String concreteStartTime;

    @ApiModelProperty(value = "结束时间", example = "2020-10-10", required = true)
    private String concreteEndTime;
}
