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

package com.dtstack.taier.develop.vo.develop.result;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.Map;

@ApiModel("数据源结果信息")
public class BatchDataSourceResultVO {
    @ApiModelProperty(value = "数据源id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "数据源名称", example = "link_name")
    private String dataName;

    @ApiModelProperty(value = "数据源描述", example = "desc")
    private String dataDesc;

    @ApiModelProperty(value = "数据源类型", example = "1")
    private Integer type;

    @ApiModelProperty(value = "应用状态 0未启用 1使用中", example = "1")
    private Integer active;

    @ApiModelProperty(value = "连接状态 0连接丢失，1连接可用", example = "1")
    private Integer linkState;

    @ApiModelProperty(value = "修改用户id")
    private Long modifyUserId;

    @ApiModelProperty(value = "创建用户id")
    private Long createUserId;

    @ApiModelProperty(value = "修改用户")
    private BatchDataSourceUserVO modifyUser;

    @ApiModelProperty(value = "数据源连接url信息（json格式）")
    private JSONObject dataJson;

    @ApiModelProperty(value = "string格式数据")
    private String dataJsonString;

    @ApiModelProperty(value = "关联数据源id", example = "1")
    private Long linkSourceId;

    @ApiModelProperty(value = "关联数据源名称", example = "link_name")
    private String linkSourceName;

    @ApiModelProperty(value = "是否为默认数据源", example = "1")
    private Integer isDefault;

    @ApiModelProperty(value = "kerberos配置")
    private Map<String, Object> kerberosConfig;

    @ApiModelProperty(value = "本地kerberos配置")
    private String localKerberosConf;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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

    public BatchDataSourceUserVO getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(BatchDataSourceUserVO modifyUser) {
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

    public Long getLinkSourceId() {
        return linkSourceId;
    }

    public void setLinkSourceId(Long linkSourceId) {
        this.linkSourceId = linkSourceId;
    }

    public String getLinkSourceName() {
        return linkSourceName;
    }

    public void setLinkSourceName(String linkSourceName) {
        this.linkSourceName = linkSourceName;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
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
}