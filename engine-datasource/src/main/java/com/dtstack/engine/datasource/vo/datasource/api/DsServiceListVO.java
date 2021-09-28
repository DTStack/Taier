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

package com.dtstack.engine.datasource.vo.datasource.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("外部产品引入数据源列表视图类")
public class DsServiceListVO implements Serializable {

    @ApiModelProperty("数据源主键id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "mysql")
    private String dataName;

    @ApiModelProperty(value = "数据源type", notes = "映射 com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum val值")
    private Integer type;

    @ApiModelProperty("数据源类型")
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "数据源描述")
    private String dataDesc;

    @ApiModelProperty("数据源连接信息 json")
    private String linkJson;

    @ApiModelProperty("数据源信息 json")
    private String dataJson;

    @ApiModelProperty("是否有meta标志 0-否 1-是")
    private Integer isMeta;

//    @ApiModelProperty("数据源所有连接信息")
//    private String dataJson;

    @ApiModelProperty("连接状态 0-连接失败, 1-正常")
    private Integer status;

    @ApiModelProperty("是否开启kerberos ")
    private Boolean openKerberos;

    @ApiModelProperty("最近修改时间")
    private Date gmtModified;

    @ApiModelProperty("数据库名称")
    private String schemaName;

}
