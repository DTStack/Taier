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
 *
 *
 * @author 全阅
 * @Description:
 * @Date: 2021/3/9 11:54
 */
@ApiModel("数据源基本信息")
public class DsDetailVO {

    @ApiModelProperty("数据源id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源类型", example = "Mysql")
    private String dataType;

    @ApiModelProperty("数据源版本 如1.x, 0.9")
    private String dataVersion;

    @ApiModelProperty("数据源名称")
    private String dataName;

    @ApiModelProperty("数据源描述")
    private String dataDesc;

    @ApiModelProperty(value = "数据源信息", notes = "数据源填写的表单信息, 保存为json, key键要与表单的name相同")
    private String dataJson;

    @ApiModelProperty(value = "0普通，1默认数据源，2从控制台添加的")
    private String isMeta;

    public Long getDataInfoId() {
        return dataInfoId;
    }

    public void setDataInfoId(Long dataInfoId) {
        this.dataInfoId = dataInfoId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public String getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(String isMeta) {
        this.isMeta = isMeta;
    }
}
