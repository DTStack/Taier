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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel("任务目录信息")
public class TaskCatalogueResultVO{

    @ApiModelProperty(value = "调度状态", example = "1")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "1")
    private Integer submitStatus;

    @ApiModelProperty(value = "目录信息")
    private List<? extends CatalogueResultVO> catalogues;

    @ApiModelProperty(value = "任务信息")
    private List<DevelopTaskResultVO> tasks;

    @ApiModelProperty(value = "任务类别")
    private Integer taskType;

    @ApiModelProperty(value = "依赖任务")
    private List<DevelopTaskResultVO> dependencyTasks;

    @ApiModelProperty(value = "列表信息")
    private List<List<Object>>  lists;

    @ApiModelProperty(value = "目录id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "父目录id", example = "0")
    private Long parentId = 0L;

    @ApiModelProperty(value = "目录名称", example = "name")
    private String name;

    @ApiModelProperty(value = "目录层级", example = "1")
    private Integer level;

    @ApiModelProperty(value = "目录类型", example = "folder")
    private String type;

    @ApiModelProperty(value = "资源类型")
    private Integer resourceType;

    @ApiModelProperty(value = "目录类型", example = "SystemFunction")
    private String catalogueType;

    @ApiModelProperty(value = "创建用户", example = "test")
    private String createUser;

    @ApiModelProperty(value = "节点值", example = "1")
    private Integer orderVal;

    @ApiModelProperty(value = "子目录列表")
    private List<DevelopCatalogueResultVO> children;

    @ApiModelProperty(value = "版本", example = "1")
    private Integer version;

    @ApiModelProperty(value = "操作模式", example = "1")
    private Integer operateModel = 1;

    @ApiModelProperty(value = "engine类型", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "任务状态", example = "1")
    private Integer status;

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }

    public List<? extends CatalogueResultVO> getCatalogues() {
        return catalogues;
    }

    public void setCatalogues(List<? extends CatalogueResultVO> catalogues) {
        this.catalogues = catalogues;
    }

    public List<DevelopTaskResultVO> getTasks() {
        return tasks;
    }

    public void setTasks(List<DevelopTaskResultVO> tasks) {
        this.tasks = tasks;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public List<DevelopTaskResultVO> getDependencyTasks() {
        return dependencyTasks;
    }

    public void setDependencyTasks(List<DevelopTaskResultVO> dependencyTasks) {
        this.dependencyTasks = dependencyTasks;
    }

    public List<List<Object>> getLists() {
        return lists;
    }

    public void setLists(List<List<Object>> lists) {
        this.lists = lists;
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

    public List<DevelopCatalogueResultVO> getChildren() {
        return children;
    }

    public void setChildren(List<DevelopCatalogueResultVO> children) {
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

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
