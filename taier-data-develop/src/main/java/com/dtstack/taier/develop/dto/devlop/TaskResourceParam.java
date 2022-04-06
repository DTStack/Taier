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
import com.dtstack.taier.dao.domain.Task;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/6/5
 */
public class TaskResourceParam extends Task {

    private Long userId;

    private Long parentId;

    private List<Long> resourceIdList;

    private List<Long> refResourceIdList;

    private boolean preSave= false;

    /**
     * flinksql 源表
     */
    private List<JSONObject> source;

    /**
     * flinksql 结果表
     */
    private List<JSONObject> sink;

    /**
     * 维表
     */
    private List<JSONObject> side;

    private Map<String, Object> sourceMap;

    private Map<String, Object> targetMap;

    private Map<String, Object> settingMap;

    private List<Task> dependencyTasks;

    private String publishDesc;

    private int lockVersion;

    private List<Map> taskVariables;

    private Long dataSourceId;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    private int operateModel = 1;

    /**
     * 同步模式 0-无增量标识，1-有增量标识
     */
    private int syncModel;

    /**
     * 输入数据文件的路径
     */
    private String input;

    /**
     * 输出模型的路径
     */
    private String output;

    /**
     * 脚本的命令行参数
     */
    private String options;

    /**
     * 任务流中待更新的子任务
     */
    private List<TaskResourceParam> toUpdateTasks;

    /**
     * 是否是右键编辑任务
     */
    private Boolean isEditBaseInfo = false;

    /**
     * 是否更新数据源
     */
    private Boolean updateSource = true;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getUpdateSource() {
        return updateSource;
    }

    public void setUpdateSource(Boolean updateSource) {
        this.updateSource = updateSource;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getResourceIdList() {
        return resourceIdList;
    }

    public void setResourceIdList(List<Long> resourceIdList) {
        this.resourceIdList = resourceIdList;
    }

    public List<Long> getRefResourceIdList() {
        return refResourceIdList;
    }

    public void setRefResourceIdList(List<Long> refResourceIdList) {
        this.refResourceIdList = refResourceIdList;
    }

    public boolean isPreSave() {
        return preSave;
    }

    public void setPreSave(boolean preSave) {
        this.preSave = preSave;
    }

    public Map<String, Object> getSourceMap() {
        return sourceMap;
    }

    public void setSourceMap(Map<String, Object> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public Map<String, Object> getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(Map<String, Object> targetMap) {
        this.targetMap = targetMap;
    }

    public Map<String, Object> getSettingMap() {
        return settingMap;
    }

    public void setSettingMap(Map<String, Object> settingMap) {
        this.settingMap = settingMap;
    }

    public List<Task> getDependencyTasks() {
        return dependencyTasks;
    }

    public void setDependencyTasks(List<Task> dependencyTasks) {
        this.dependencyTasks = dependencyTasks;
    }

    public String getPublishDesc() {
        return publishDesc;
    }

    public void setPublishDesc(String publishDesc) {
        this.publishDesc = publishDesc;
    }

    public int getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(int lockVersion) {
        this.lockVersion = lockVersion;
    }

    public List<Map> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(List<Map> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public int getOperateModel() {
        return operateModel;
    }

    public void setOperateModel(int operateModel) {
        this.operateModel = operateModel;
    }

    public int getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(int syncModel) {
        this.syncModel = syncModel;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public List<TaskResourceParam> getToUpdateTasks() {
        return toUpdateTasks;
    }

    public void setToUpdateTasks(List<TaskResourceParam> toUpdateTasks) {
        this.toUpdateTasks = toUpdateTasks;
    }

    public Boolean getEditBaseInfo() {
        return isEditBaseInfo;
    }

    public void setEditBaseInfo(Boolean editBaseInfo) {
        isEditBaseInfo = editBaseInfo;
    }


    public void setSource(List<JSONObject> source) {
        this.source  = source;
    }

    public List<JSONObject> getSource() {
        return source;
    }

    public void setSink(List<JSONObject> sink) {
        this.sink  = sink;
    }

    public List<JSONObject> getSink() {
        return sink;
    }

    public void setSide(List<JSONObject> side) {
        this.side  = side;
    }

    public List<JSONObject> getSide() {
        return side;
    }

}
