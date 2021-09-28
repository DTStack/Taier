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

package com.dtstack.engine.datasource.param.datasource;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.datasource.param.PubSvcBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("新增数据源整体入参")
public class AddDataSourceParam extends PubSvcBaseParam {

    @ApiModelProperty("数据源主键id")
    private Long id = 0L;

    @ApiModelProperty(value = "数据源类型", required = true)
    private String dataType;

    @ApiModelProperty("数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "数据源名称", required = true)
    private String dataName;

    @ApiModelProperty(value = "数据源描述", required = true)
    private String dataDesc;

    @ApiModelProperty("授权产品编码 可为空")
    private List<Integer> appTypeList;

    @ApiModelProperty(value = "数据源表单填写数据JsonString", required = true)
    private String dataJsonString;

    @ApiModelProperty(value = "数据源表单填写数据Json参数", required = true)
    private JSONObject dataJson;

}
