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

package com.dtstack.taiga.dao.domain;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 */
public class BatchDataSource extends TenantEntity {

    /**
     * '数据源名称'
     */
    private String dataName;

    /**
     * 数据源描述
     */
    private String dataDesc;

    /**
     * 加密的数据源信息
     */
    private String dataJson;

    /**
     * 数据源类型
     */
    private Integer type;

    /**
     * 新建用户id
     */
    private Long createUserId;


    /**
     * 修改用户id
     */
    private Long modifyUserId;

    /**
     * 是否启用
     */
    private Integer active;

    /**
     * 连接是否可用
     */
    private Integer linkState;

    /**
     * 是不是项目下的默认数据库
     */
    private int isDefault;

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
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
}
