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

package com.dtstack.engine.datasource.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据源基本信息")
public class DsDetailVO {

    @ApiModelProperty("数据源id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源类型", example = "Mysql")
    private String dataType;

    @ApiModelProperty("数据源报表")
    private String dataVersion;

//    @ApiModelProperty("数据源类型")
//    private String dataTypeName;

    @ApiModelProperty("数据源名称")
    private String dataName;

    @ApiModelProperty("数据源描述")
    private String dataDesc;

    @ApiModelProperty(value = "数据源信息", notes = "数据源填写的表单信息, 保存为json, key键要与表单的name相同")
    private String dataJson;

}
