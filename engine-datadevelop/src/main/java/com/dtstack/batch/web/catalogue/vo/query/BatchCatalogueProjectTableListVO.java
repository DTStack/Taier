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

package com.dtstack.batch.web.catalogue.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("目录项目表信息")
public class BatchCatalogueProjectTableListVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "表名称", example = "test", required = true)
    private String tableName;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "script类型", example = "1", required = true)
    private Integer scriptType;

    @ApiModelProperty(value = "表唯一标识", example = "dev", required = true)
    private String projectIdentifier;

    @ApiModelProperty(value = "是否为root", hidden = true)
    private Boolean isRoot;
}
