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

import com.dtstack.taier.dao.domain.BatchCatalogue;

import java.util.List;

/**
 * Created by Administrator on 2017/4/28 0028.
 */
public class CatalogueVO {

    public static CatalogueVO toVO(BatchCatalogue catalogue) {
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
    private String createUser;
    private Integer orderVal;
    private List<CatalogueVO> children;
    private ReadWriteLockVO readWriteLockVO;
    private Integer version;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private Integer operateModel;

    /**
     * 2-python2.x,3-python3.x
     */
    private Integer pythonVersion;

    /**
     * 0-TensorFlow,1-MXNet
     */
    private Integer learningType;

    private Integer scriptType;

    /**
     * 0-普通任务，1-工作流中的子任务
     */
    private Integer isSubTask = 0;

    /**
     * 租户Id
     */
    private Long tenantId;

    /**
     * 任务状态 0：未提交 ；1：已提交
     */
    private Integer status;

    public CatalogueVO(){}

    public CatalogueVO(long id, long parentId, String name, Integer level, String type, Long tenantId) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.level = level;
        this.type = type;
        this.tenantId = tenantId;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
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

    public ReadWriteLockVO getReadWriteLockVO() {
        return readWriteLockVO;
    }

    public void setReadWriteLockVO(ReadWriteLockVO readWriteLockVO) {
        this.readWriteLockVO = readWriteLockVO;
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

    public Integer getPythonVersion() {
        return pythonVersion;
    }

    public void setPythonVersion(Integer pythonVersion) {
        this.pythonVersion = pythonVersion;
    }

    public Integer getLearningType() {
        return learningType;
    }

    public void setLearningType(Integer learningType) {
        this.learningType = learningType;
    }

    public Integer getScriptType() {
        return scriptType;
    }

    public void setScriptType(Integer scriptType) {
        this.scriptType = scriptType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsSubTask() {
        return isSubTask;
    }

    public void setIsSubTask(Integer isSubTask) {
        this.isSubTask = isSubTask;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
