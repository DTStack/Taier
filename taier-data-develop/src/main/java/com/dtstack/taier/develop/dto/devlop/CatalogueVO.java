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

import com.dtstack.taier.dao.domain.DevelopCatalogue;

import java.util.List;

/**
 * Created by Administrator on 2017/4/28 0028.
 */
public class CatalogueVO {

    public static CatalogueVO toVO(DevelopCatalogue catalogue) {
        CatalogueVO vo = new CatalogueVO();
        vo.setName(catalogue.getNodeName());
        vo.setLevel(catalogue.getLevel());
        vo.setId(catalogue.getId());
        vo.setOrderVal(catalogue.getOrderVal());
        vo.setParentId(catalogue.getNodePid());
        vo.setTenantId(catalogue.getTenantId());
        return vo;
    }

    private Long id;
    private Long parentId;
    private String name;
    private Integer level;
    private String type;
    private Integer taskType;
    private Integer resourceType;
    private String catalogueType;
    private Integer orderVal;
    private List<CatalogueVO> children;
    private Integer version;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private Integer operateModel;

    /**
     * 租户Id
     */
    private Long tenantId;

    public CatalogueVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
    }

    public String getCatalogueType() {
        return catalogueType;
    }

    public void setCatalogueType(String catalogueType) {
        this.catalogueType = catalogueType;
    }

    public Integer getOrderVal() {
        return orderVal;
    }

    public void setOrderVal(Integer orderVal) {
        this.orderVal = orderVal;
    }

    public List<CatalogueVO> getChildren() {
        return children;
    }

    public void setChildren(List<CatalogueVO> children) {
        this.children = children;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getOperateModel() {
        return operateModel;
    }

    public void setOperateModel(Integer operateModel) {
        this.operateModel = operateModel;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
