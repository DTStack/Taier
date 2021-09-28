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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目引擎信息")
public class BatchProjectEngineBaseVO {

    @ApiModelProperty(value = "引擎类型", example = "Hadoop", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "数据库名称", example = "dataBase", required = true)
    private String database;

    @ApiModelProperty(value = "创建项目的方式", example = "1", required = true)
    private Integer createModel;

    @ApiModelProperty(value = "生命周期", example = "9999", required = true)
    private Integer lifecycle;

    @ApiModelProperty(value = "目录 ID", example = "1", required = true)
    private Long catalogueId;
}
