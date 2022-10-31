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

package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * SLOGAN:改变世界！改变未来！
 *
 * @author tz
 * @Description:
 * @Date: 2021/12/30 11:19
 */
@ApiModel("数据源列表信息")
public class DsInfoVO {

    @ApiModelProperty("数据源Id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "mysql")
    private String dataName;

    @ApiModelProperty(value = "数据源类型枚举")
    private Integer dataTypeCode;

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }
}
