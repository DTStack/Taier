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

import java.io.Serializable;
import java.util.List;

/**
 * 数据表单模版视图类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
@ApiModel("数据表单模版视图类")
public class DsFormTemplateVo implements Serializable {

    @ApiModelProperty(value = "数据源类型", notes = "具体查看com.dtstack.pubsvc.common.enums.datasource.DsType枚举类")
    private String dataType;

    @ApiModelProperty("数据源版本 可为空")
    private String dataVersion;

    @ApiModelProperty("模版表单属性详情列表")
    private List<DsFormFieldVo> fromFieldVoList;


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

    public List<DsFormFieldVo> getFromFieldVoList() {
        return fromFieldVoList;
    }

    public void setFromFieldVoList(List<DsFormFieldVo> fromFieldVoList) {
        this.fromFieldVoList = fromFieldVoList;
    }
}
