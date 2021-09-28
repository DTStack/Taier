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

package com.dtstack.engine.lineage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author chener
 * @Classname LineageColumnColumnVO
 * @Description 字段级血缘关系
 * @Date 2020/10/30 11:20
 * @Created chener@dtstack.com
 */
@ApiModel("字段级血缘关系")
public class LineageColumnColumnVO {

    @ApiModelProperty("uic租户id")
    private Long dtUicTenantId;

    /**
     * 应用类型
     */
    @ApiModelProperty("应用类型")
    private Integer appType;

    /**
     * 输入表信息
     */
    @ApiModelProperty("输入表信息")
    private LineageTableVO inputTableInfo;

    /**
     * 输入字段名
     */
    @ApiModelProperty("输入字段名称")
    private String inputColumnName;

    /**
     * 输出表信息
     */
    @ApiModelProperty("输出表信息")
    private LineageTableVO resultTableInfo;

    /**
     * 输出字段名
     */
    @ApiModelProperty("输出字段名称")
    private String resultColumnName;

    /**
     * 血缘批次唯一码
     */
    @ApiModelProperty("批次唯一码")
    private String uniqueKey;

    @ApiModelProperty(value = "使用双亲表示法，标识树数据结构",notes = "当前节点的父节点在列表中的下标")
    private Integer parentIndex;

    /**
     * 是否手动维护
     */
    @ApiModelProperty("是否手动维护")
    private Boolean isManual;

    public Boolean getManual() {
        return isManual;
    }

    public void setManual(Boolean manual) {
        isManual = manual;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public String getInputColumnName() {
        return inputColumnName;
    }

    public void setInputColumnName(String inputColumnName) {
        this.inputColumnName = inputColumnName;
    }

    public String getResultColumnName() {
        return resultColumnName;
    }

    public void setResultColumnName(String resultColumnName) {
        this.resultColumnName = resultColumnName;
    }

    public LineageTableVO getInputTableInfo() {
        return inputTableInfo;
    }

    public void setInputTableInfo(LineageTableVO inputTableInfo) {
        this.inputTableInfo = inputTableInfo;
    }

    public LineageTableVO getResultTableInfo() {
        return resultTableInfo;
    }

    public void setResultTableInfo(LineageTableVO resultTableInfo) {
        this.resultTableInfo = resultTableInfo;
    }

    public Integer getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(Integer parentIndex) {
        this.parentIndex = parentIndex;
    }

    public Long getDtUicTenantId() {
        return dtUicTenantId;
    }

    public void setDtUicTenantId(Long dtUicTenantId) {
        this.dtUicTenantId = dtUicTenantId;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
