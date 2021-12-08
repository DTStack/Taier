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

package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目添加引擎信息")
public class BatchProjectAddNewEngineVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "UIC 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "引擎类型", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "引擎 DB 信息", example = "dev", required = true)
    private String database;

    @ApiModelProperty(value = "添加 DB 模式， 0 导入已有 DB，1 创建", example = "0", required = true)
    private Integer createModel;

    @ApiModelProperty(value = "表的生命周期", example = "9999", required = true)
    private Integer lifecycle;

    @ApiModelProperty(value = "所属类目", example = "18", required = true)
    private Long catalogueId;
}
