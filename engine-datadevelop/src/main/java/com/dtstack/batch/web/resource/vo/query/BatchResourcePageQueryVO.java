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

package com.dtstack.batch.web.resource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("资源信息")
public class BatchResourcePageQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "资源修改用户 ID", example = "1", required = true)
    private Long resourceModifyUserId;

    @ApiModelProperty(value = "开始时间", example = "2020-12-30 11:42:14", required = true)
    private Timestamp startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-12-30 11:42:14", required = true)
    private Timestamp endTime;

    @ApiModelProperty(value = "总页数", example = "10", required = true)
    private Integer pageSize = 10;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "资源名称", example = "资源1", required = true)
    private String resourceName;

    @ApiModelProperty(value = "排序规则", example = "desc", required = true)
    private String sort = "desc";
}
