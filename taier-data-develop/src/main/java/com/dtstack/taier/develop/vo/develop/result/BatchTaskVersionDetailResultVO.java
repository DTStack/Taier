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
import java.util.List;

@ApiModel("任务版本详细信息")
public class BatchTaskVersionDetailResultVO {

    @ApiModelProperty(value = "用户名", example = "测试用户")
    private String userName;

    @ApiModelProperty(value = "依赖任务名称")
    private List<String> dependencyTaskNames;

    @ApiModelProperty(value = "依赖任务信息")
    private JSONObject dependencyTasks;

    @ApiModelProperty(value = "任务 ID", example = "3")
    private Long taskId;

    @ApiModelProperty(value = "sql")
    private String originSql;

    @ApiModelProperty(value = "sql 文本", example = "use dev")
    private String sqlText;

    @ApiModelProperty(value = "发布备注", example = "test")
    private String publishDesc;

    @ApiModelProperty(value = "新建task的用户", example = "35")
    private Long createUserId;

    @ApiModelProperty(value = "task版本", example = "23")
    private Integer version;

    @ApiModelProperty(value = "环境参数", example = "")
    private String taskParams;

    @ApiModelProperty(value = "调度信息", example = "")
    private String scheduleConf;

    @ApiModelProperty(value = "调度状态", example = "1")
    private Integer scheduleStatus;

    @ApiModelProperty(value = "依赖的任务id", example = "23")
    private String dependencyTaskIds;

    @ApiModelProperty(value = "数栈租户 ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "平台类型", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getDependencyTaskNames() {
        return dependencyTaskNames;
    }

    public void setDependencyTaskNames(List<String> dependencyTaskNames) {
        this.dependencyTaskNames = dependencyTaskNames;
    }

    public JSONObject getDependencyTasks() {
        return dependencyTasks;
    }

    public void setDependencyTasks(JSONObject dependencyTasks) {
        this.dependencyTasks = dependencyTasks;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getOriginSql() {
        return originSql;
    }

    public void setOriginSql(String originSql) {
        this.originSql = originSql;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
    }

    public String getPublishDesc() {
        return publishDesc;
    }

    public void setPublishDesc(String publishDesc) {
        this.publishDesc = publishDesc;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public String getScheduleConf() {
        return scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public String getDependencyTaskIds() {
        return dependencyTaskIds;
    }

    public void setDependencyTaskIds(String dependencyTaskIds) {
        this.dependencyTaskIds = dependencyTaskIds;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

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
}
