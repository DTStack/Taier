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

package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * @author chener
 * @Classname LineageTableTable
 * @Description 存储纯粹的血缘关系
 * @Date 2020/10/22 20:15
 * @Created chener@dtstack.com
 */
@ApiModel
public class LineageTableTable extends DtUicTenantEntity {

    @ApiModelProperty(notes = "应用类型")
    private Integer appType;

    @ApiModelProperty(notes = "输入表id")
    private Long inputTableId;

    @ApiModelProperty("输入物理表定位key")
    private String inputTableKey;

    @ApiModelProperty(notes = "输出表id")
    private Long resultTableId;

    @ApiModelProperty("输出物理表定位key")
    private String resultTableKey;

    @ApiModelProperty(notes = "表级血缘关系定位码")
    private String tableLineageKey;

    @ApiModelProperty(notes = "血缘来源：0-sql解析；1-手动维护；2-json解析")
    private Integer lineageSource;

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Long getInputTableId() {
        return inputTableId;
    }

    public void setInputTableId(Long inputTableId) {
        this.inputTableId = inputTableId;
    }

    public Long getResultTableId() {
        return resultTableId;
    }

    public void setResultTableId(Long resultTableId) {
        this.resultTableId = resultTableId;
    }

    public String getTableLineageKey() {
        return tableLineageKey;
    }

    public void setTableLineageKey(String tableLineageKey) {
        this.tableLineageKey = tableLineageKey;
    }

    public Integer getLineageSource() {
        return lineageSource;
    }

    public void setLineageSource(Integer lineageSource) {
        this.lineageSource = lineageSource;
    }

    public String getInputTableKey() {
        return inputTableKey;
    }

    public void setInputTableKey(String inputTableKey) {
        this.inputTableKey = inputTableKey;
    }

    public String getResultTableKey() {
        return resultTableKey;
    }

    public void setResultTableKey(String resultTableKey) {
        this.resultTableKey = resultTableKey;
    }

    @Override
    public int hashCode() {
        if (Objects.nonNull(tableLineageKey)){
            return Objects.hashCode(tableLineageKey);
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)){
            return false;
        }
        if (obj instanceof LineageTableTable){
            return this.tableLineageKey.equalsIgnoreCase(((LineageTableTable) obj).getTableLineageKey());
        }
        return super.equals(obj);
    }
}
