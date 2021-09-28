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

package com.dtstack.engine.datasource.param.datasource.api;

import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("第三方创建或迁移数据源参数类")
public class CreateDsParam extends PubSvcBaseParam {

    @ApiModelProperty(value = "来源产品type", example = "1-离线, 2-数据质量", required = true)
    private Integer appType;

    @ApiModelProperty(value = "数据源type", notes = "映射 com.dtstack.engine.datasource.common.enums.datasource.DataSourceTypeEnum val值", required = true)
    private Integer type;

    @ApiModelProperty(value = "数据源名称", required = true)
    private String dataName;

    @ApiModelProperty("数据源简介")
    private String dataDesc;

    @ApiModelProperty(value = "数据源表单填写数据JsonString, 默认以Base64密文传输", required = true)
    private String dataJson;

//    @ApiModelProperty("数据源表单填写数据Json参数")
//    private JSONObject dataJson;

    @ApiModelProperty(value = "是否为默认数据源", required = true)
    private Integer isMeta;

    @ApiModelProperty("连接状态 0-连接失败 1-成功")
    private Integer status;

    @ApiModelProperty(value = "创建数据源的租户主键id", required = true)
    private Long dsTenantId;

    @ApiModelProperty(value = "创建数据源的dtuic 租户id", required = true)
    private Long dsDtuicTenantId;

    @ApiModelProperty(value = "项目id ", required = false)
    private Long projectId;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("创建用户Id")
    private Long createUserId;

    @ApiModelProperty("修改用户Id")
    private Long modifyUserId;

    @ApiModelProperty("数据库名称")
    private String schemaName;

}
