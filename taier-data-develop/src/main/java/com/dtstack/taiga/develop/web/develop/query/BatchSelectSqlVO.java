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

package com.dtstack.taiga.develop.web.develop.query;

import com.dtstack.taiga.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("执行选中的sql或者脚本")
public class BatchSelectSqlVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "工作任务 ID", example = "3", required = true)
    private String jobId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "类别", example = "2", required = true)
    private Integer type;

    @ApiModelProperty(value = "SQL ID", example = "5", required = true)
    private String sqlId;

    @ApiModelProperty(value = "是否需要结果 默认是false", example = "false", required = true)
    private Boolean needResult = false;
}
