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

package com.dtstack.taier.dao.domain.po;


import java.util.Date;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/26
 */
public class DsListBO {

    private Long dataInfoId;

    private String dataName;

    private String dataType;

    private String dataVersion;

    private String appNames;

    private String dataDesc;

    private String linkJson;

    private String dataJson;

    private Integer status;

    private Integer isMeta;

    private Date gmtModified;

    private String schemaName;

    private Integer dataTypeCode;

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }




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

    public String getAppNames() {
        return appNames;
    }

    public void setAppNames(String appNames) {
        this.appNames = appNames;
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

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
