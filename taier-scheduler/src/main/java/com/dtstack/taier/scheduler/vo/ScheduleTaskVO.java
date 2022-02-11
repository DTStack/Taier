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

package com.dtstack.taier.scheduler.vo;

import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.UserDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleTaskVO extends ScheduleTaskShade {

    private UserDTO createUser;
    private UserDTO modifyUser;
    private UserDTO ownerUser;
    protected Integer taskPeriodId;
    protected String taskPeriodType;
    private String nodePName;
    private Long userId;
    private Integer lockVersion;
    private List<Map> taskVariables;
    private boolean forceUpdate;

    private Long dataSourceId;

    private ScheduleTaskVO subNodes;

    private List<ScheduleTaskVO> relatedTasks;
    private List<ScheduleTaskVO> taskRuleList;

    private String tenantName;

    private String projectAlias;

    /**
     * 0-向导模式，1-脚本模式
     */
    @ApiModelProperty(notes = "0-向导模式，1-脚本模式")
    private int createModel;

    /**
     * 操作模式 0-资源模式，1-编辑模式
     */
    @ApiModelProperty(notes = "操作模式 0-资源模式，1-编辑模式")
    private int operateModel;

    /**
     * 2-python2.x,3-python3.x
     */
    @ApiModelProperty(notes = "2-python2.x,3-python3.x")
    private int pythonVersion;

    /**
     * 0-TensorFlow,1-MXNet
     */
    @ApiModelProperty(notes = "0-TensorFlow,1-MXNet")
    private int learningType;

    /**
     * 输入数据文件的路径
     */
    @ApiModelProperty(notes = "输入数据文件的路径")
    private String input;

    /**
     * 输出模型的路径
     */
    @ApiModelProperty(notes = "输出模型的路径")
    private String output;

    /**
     * 脚本的命令行参数
     */
    @ApiModelProperty(notes = "脚本的命令行参数")
    private String options;

    private String flowName;

    /**
     * 同步模式
     */
    @ApiModelProperty(notes = "同步模式")
    private int syncModel;

    private String increColumn;

    /**
     * 是否是当前项目
     */
    @ApiModelProperty(notes = "是否是当前项目")
    private boolean currentProject;

    /**
     * 运行资源的入口类
     */
    private String mainClass;

    private List<ScheduleTaskVO> taskVOS;
    private List<ScheduleTaskVO> subTaskVOS;
    protected String cron;
    private Boolean existsOnRule;

    private Integer taskType;

    private Long nodePid;

    private String taskParams;

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public boolean isCurrentProject() {
        return currentProject;
    }

    @Override
    public String getTaskParams() {
        return taskParams;
    }

    @Override
    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public Long getNodePid() {
        return nodePid;
    }

    public void setNodePid(Long nodePid) {
        this.nodePid = nodePid;
    }

    @Override
    public Integer getTaskType() {
        return taskType;
    }

    @Override
    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public boolean getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(boolean currentProject) {
        this.currentProject = currentProject;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getIncreColumn() {
        return increColumn;
    }

    public void setIncreColumn(String increColumn) {
        this.increColumn = increColumn;
    }

    public int getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(int syncModel) {
        this.syncModel = syncModel;
    }

    public int getOperateModel() {
        return operateModel;
    }

    public void setOperateModel(int operateModel) {
        this.operateModel = operateModel;
    }

    public int getPythonVersion() {
        return pythonVersion;
    }

    public void setPythonVersion(int pythonVersion) {
        this.pythonVersion = pythonVersion;
    }

    public int getLearningType() {
        return learningType;
    }

    public void setLearningType(int learningType) {
        this.learningType = learningType;
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

    public boolean getforceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public Integer getTaskPeriodId() {
        return taskPeriodId;
    }

    public void setTaskPeriodId(Integer taskPeriodId) {
        this.taskPeriodId = taskPeriodId;
    }

    public String getTaskPeriodType() {
        return taskPeriodType;
    }

    public void setTaskPeriodType(String taskPeriodType) {
        this.taskPeriodType = taskPeriodType;
    }

    public List<ScheduleTaskVO> getTaskVOS() {
        return taskVOS;
    }

    public ScheduleTaskVO setTaskVOS(List<ScheduleTaskVO> taskVOS) {
        this.taskVOS = taskVOS;
        return this;
    }

    public String getNodePName() {
        return nodePName;
    }

    public void setNodePName(String nodePName) {
        this.nodePName = nodePName;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public UserDTO getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserDTO createUser) {
        this.createUser = createUser;
    }

    public UserDTO getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(UserDTO modifyUser) {
        this.modifyUser = modifyUser;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Map> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(List<Map> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public List<ScheduleTaskVO> getSubTaskVOS() {
        return subTaskVOS;
    }

    public void setSubTaskVOS(List<ScheduleTaskVO> subTaskVOS) {
        this.subTaskVOS = subTaskVOS;
    }

    public int getCreateModel() {
        return createModel;
    }

    public void setCreateModel(int createModel) {
        this.createModel = createModel;
    }

    public UserDTO getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(UserDTO ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public ScheduleTaskVO getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(ScheduleTaskVO subNodes) {
        this.subNodes = subNodes;
    }

    public List<ScheduleTaskVO> getRelatedTasks() {
        return relatedTasks;
    }

    public void setRelatedTasks(List<ScheduleTaskVO> relatedTasks) {
        this.relatedTasks = relatedTasks;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public List<ScheduleTaskVO> getTaskRuleList() {
        return taskRuleList;
    }

    public void setTaskRuleList(List<ScheduleTaskVO> taskRuleList) {
        this.taskRuleList = taskRuleList;
    }

    public Boolean getExistsOnRule() {
        return existsOnRule;
    }

    public void setExistsOnRule(Boolean existsOnRule) {
        this.existsOnRule = existsOnRule;
    }

    public String getProjectAlias() {
        return projectAlias;
    }

    public void setProjectAlias(String projectAlias) {
        this.projectAlias = projectAlias;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
}
