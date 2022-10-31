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

package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author 全阅
 * @Description: 数据源详细信息
 * @Date: 2021/3/10
 */
@TableName("datasource_info")
public class DsInfo extends TenantModel {

    /**
     * 数据源类型唯一
     */
    @TableField("data_type")
    private String dataType;

    /**
     * 数据源类型编码
     */
    @TableField("data_type_code")
    private Integer dataTypeCode;

    /**
     * 数据源版本
     */
    @TableField("data_version")
    private String dataVersion;

    /**
     * 数据源名称
     */
    @TableField("data_name")
    private String dataName;

    /**
     * 数据源描述
     */
    @TableField("data_desc")
    private String dataDesc;

    /**
     * 数据源连接信息
     */
    @TableField("link_json")
    private String linkJson;

    /**
     * 数据源填写的表单信息
     */
    @TableField("data_json")
    private String dataJson;

    /**
     * 连接状态
     */
    @TableField("status")
    private Integer status;

    /**
     * 是否有meta标志
     */
    @TableField("is_meta")
    private Integer isMeta;

    /**
     * 数据库名称
     */
    @TableField("schema_name")
    private String schemaName;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
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

    public String getLinkJson() {
        return linkJson;
    }

    public void setLinkJson(String linkJson) {
        this.linkJson = linkJson;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(Integer isMeta) {
        this.isMeta = isMeta;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}