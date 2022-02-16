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

import java.sql.Timestamp;

@ApiModel("任务信息")
public class BatchTaskResultVO {

    @ApiModelProperty(value = "任务名称", example = "spark_test", required = true)
    private String name;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1", required = true)
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "0", required = true)
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本", example = "shwo tables;", required = true)
    private String sqlText;

    @ApiModelProperty(value = "任务参数", example = "", required = true)
    private String taskParams;

    @ApiModelProperty(value = "调度配置", example = "{\"selfReliance\":false}", required = true)
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "1", required = true)
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "0", required = true)
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "1")
    private Integer submitStatus;

    @ApiModelProperty(value = "任务发布状态，前端使用", example = "spark_test")
    private Integer status;

    @ApiModelProperty(value = "最后修改task的用户", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "新建task的用户", example = "3")
    private Long createUserId;

    @ApiModelProperty(value = "负责人id", example = "11")
    private Long ownerUserId;

    @ApiModelProperty(value = "task版本", example = "11")
    private Integer version;

    @ApiModelProperty(value = "节点父ID", example = "7")
    private Long nodePid;

    @ApiModelProperty(value = "任务描述", example = "测试")
    private String taskDesc;

    @ApiModelProperty(value = "入口类", example = "Abc.java")
    private String mainClass;

    @ApiModelProperty(value = "参数", example = "1,2")
    private String exeArgs;

    @ApiModelProperty(value = "所属工作流id", example = "1")
    private Long flowId = 0L;

    @ApiModelProperty(value = "是否过期", example = "0")
    private Integer isExpire;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "id", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", required = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "组件版本")
    private String componentVersion;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getComputeType() {
        return computeType;
    }

    public void setComputeType(Integer computeType) {
        this.computeType = computeType;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public String getSqlText() {
        return sqlText;
    }

    public void setSqlText(String sqlText) {
        this.sqlText = sqlText;
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

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getExeArgs() {
        return exeArgs;
    }

    public void setExeArgs(String exeArgs) {
        this.exeArgs = exeArgs;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getIsExpire() {
        return isExpire;
    }

    public void setIsExpire(Integer isExpire) {
        this.isExpire = isExpire;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }
}
