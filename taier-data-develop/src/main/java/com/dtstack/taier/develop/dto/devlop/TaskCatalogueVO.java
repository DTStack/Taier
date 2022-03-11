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

import com.dtstack.taier.dao.domain.Task;

import java.util.List;

/**
 * @author toutian
 */
public class TaskCatalogueVO extends CatalogueVO {


    private Integer scheduleStatus;
    private Integer submitStatus;
    private List<? extends Catalogue> catalogues;
    private List<Task> tasks;
    private Integer taskType;
    private List<Task> dependencyTasks;
    private List<List<Object>>  lists;


    @Override
    public Integer getTaskType() {
        return taskType;
    }

    @Override
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public TaskCatalogueVO() {
    }

    public TaskCatalogueVO(TaskResourceParam task, Long parentId) {
        this.setId(task.getId());
        this.setName(task.getName());
        this.setType("file");
        this.setLevel(null);
        this.setChildren(null);
        this.setScheduleStatus(task.getScheduleStatus());
        this.setParentId(parentId);
        this.setTaskType(task.getTaskType());
        this.setVersion(task.getVersion());
    }

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

    public List<? extends Catalogue> getCatalogues() {
        return catalogues;
    }

    public void setCatalogues(List<? extends Catalogue> catalogues) {
        this.catalogues = catalogues;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getDependencyTasks() {
        return dependencyTasks;
    }

    public void setDependencyTasks(List<Task> dependencyTasks) {
        this.dependencyTasks = dependencyTasks;
    }

    public List<List<Object>> getLists() {
        return lists;
    }

    public void setLists(List<List<Object>> lists) {
        this.lists = lists;
    }
}
