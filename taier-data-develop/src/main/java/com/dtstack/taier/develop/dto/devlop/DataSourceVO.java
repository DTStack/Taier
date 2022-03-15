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

package com.dtstack.taier.develop.dto.devlop;


import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.dao.domain.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 */
public class DataSourceVO{

    /**
     * 数据源 ID
     */
    private Long id = 0L;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 数据源名称
     */
    private String dataName;

    /**
     * 数据源描述
     */
    private String dataDesc;

    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 数据源类型
     */
    private String dataType;

    /**数据源类型编码**/
    private Integer dataTypeCode;

    /**
     * 数据源版本
     */
    private String dataVersion;

    /**
     * 是否被使用
     */
    private Integer active;

    /**
     * 是否有效
     */
    private Integer linkState;

    /**
     * 修改人 ID
     */
    private Long modifyUserId;

    /**
     * 创建人 ID
     */
    private Long createUserId;

    /**
     * 修改人
     */
    private User modifyUser;

    /**
     * 数据源相关信息
     */
    private JSONObject dataJson;

    /**
     * 数据源加密字符
     */
    private String dataJsonString;

    /**
     * Kerberos 信息
     */
    private Map<String, Object> kerberosConfig;

    /**
     * 本地 Kerberos 地址
     */
    private String localKerberosConf;

    /**
     * 授权产品编码 可为空
     */
    private List<Integer> appTypeList;

    /**
     * 数组字符串
     */
    private String appTypeListString;

    /**
     * 是否为默认数据源 0-否 1-是
     */
    private Integer isMeta;

    /**数据库名称**/
    private String schemaName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public Integer getLinkState() {
        return linkState;
    }

    public void setLinkState(Integer linkState) {
        this.linkState = linkState;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public User getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(User modifyUser) {
        this.modifyUser = modifyUser;
    }

    public JSONObject getDataJson() {
        return dataJson;
    }

    public void setDataJson(JSONObject dataJson) {
        this.dataJson = dataJson;
    }

    public String getDataJsonString() {
        return dataJsonString;
    }

    public void setDataJsonString(String dataJsonString) {
        this.dataJsonString = dataJsonString;
    }

    public Map<String, Object> getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(Map<String, Object> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public String getLocalKerberosConf() {
        return localKerberosConf;
    }

    public void setLocalKerberosConf(String localKerberosConf) {
        this.localKerberosConf = localKerberosConf;
    }

    public List<Integer> getAppTypeList() {
        return appTypeList;
    }

    public void setAppTypeList(List<Integer> appTypeList) {
        this.appTypeList = appTypeList;
    }

    public String getAppTypeListString() {
        return appTypeListString;
    }

    public void setAppTypeListString(String appTypeListString) {
        this.appTypeListString = appTypeListString;
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
