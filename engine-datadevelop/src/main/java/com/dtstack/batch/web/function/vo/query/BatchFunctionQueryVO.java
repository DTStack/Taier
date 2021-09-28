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

package com.dtstack.batch.web.function.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("函数查询信息")
public class BatchFunctionQueryVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "函数修改用户id", hidden = true)
    private Long functionModifyUserId;

    @ApiModelProperty(value = "开始时间", example = "2020-08-14 14:41:55")
    private Timestamp startTime;

    @ApiModelProperty(value = "结束时间", example = "2020-08-14 14:41:55")
    private Timestamp endTime;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "页面大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "函数名称", example = "func_name")
    private String functionName;

    @ApiModelProperty(value = "函数名称", example = "desc")
    private String sort = "desc";

    @ApiModelProperty(value = "函数类型 0自定义 1系统", example = "0", required = true)
    private Integer type;
}
