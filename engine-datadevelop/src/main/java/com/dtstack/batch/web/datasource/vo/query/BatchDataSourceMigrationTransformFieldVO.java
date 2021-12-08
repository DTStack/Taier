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

package com.dtstack.batch.web.datasource.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("整库同步字段转换信息")
public class BatchDataSourceMigrationTransformFieldVO {
    @ApiModelProperty(value = "目标字段", example = "id")
    private String convertDest;

    @ApiModelProperty(value = "源字段", example = "id")
    private String convertSrc;

    @ApiModelProperty(value = "转换类型 1表名 2字段名 3段类型", example = "id")
    private Integer convertType;

    @ApiModelProperty(value = "转换方式 1字符替换 2添加前缀 3添加后缀", example = "1")
    private Integer convertObject;
}
