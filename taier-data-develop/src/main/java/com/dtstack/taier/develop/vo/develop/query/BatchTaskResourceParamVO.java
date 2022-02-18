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

package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@ApiModel("任务信息")
public class BatchTaskResourceParamVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "")
    private List<Long> resourceIdList;

    @ApiModelProperty(value = "")
    private List<Long> refResourceIdList;

    @ApiModelProperty(value = "", example = "false")
    private Boolean preSave = false;

    @ApiModelProperty(value = "")
    private Map<String, Object> sourceMap;

    @ApiModelProperty(value = "")
    private Map<String, Object> targetMap;

    @ApiModelProperty(value = "设置")
    private Map<String, Object> settingMap;

    @ApiModelProperty(value = "依赖任务信息")
    private List<BatchTaskBaseVO> dependencyTasks;

    @ApiModelProperty(value = "发布描述", example = "test")
    private String publishDesc;

    @ApiModelProperty(value = "锁版本 ID", example = "1")
    private Integer lockVersion = 0;

    @ApiModelProperty(value = "是否覆盖更新", example = "false")
    private Boolean forceUpdate = false;

    @ApiModelProperty(value = "任务版本 ID")
    private List<Map> taskVariables;

    @ApiModelProperty(value = "数据源 ID", example = "24")
    private Long dataSourceId;

    @ApiModelProperty(value = "0-向导模式,1-脚本模式", example = "1")
    private Integer createModel = 0;

    @ApiModelProperty(value = "操作模式 0-资源模式，1-编辑模式D", example = "0")
    private Integer operateModel = 1;

    @ApiModelProperty(value = "同步模式 0-无增量标识，1-有增量标识", example = "1")
    private Integer syncModel = 0;

    @ApiModelProperty(value = "2-python2.x,3-python3.xD", example = "2")
    private Integer pythonVersion = 0;

    @ApiModelProperty(value = "0-TensorFlow,1-MXNet", example = "1")
    private Integer learningType = 0;

    @ApiModelProperty(value = "输入数据文件的路径", example = "")
    private String input;

    @ApiModelProperty(value = "输出模型的路径", example = "")
    private String output;

    @ApiModelProperty(value = "脚本的命令行参数", example = "")
    private String options;

    @ApiModelProperty(value = "任务流中待更新的子任务D")
    private List<BatchTaskResourceParamVO> toUpdateTasks;

    @ApiModelProperty(value = "是否是右键编辑任务", example = "false")
    private Boolean isEditBaseInfo = false;

    @ApiModelProperty(value = "工作流父任务版本号  用于子任务获取父任务锁", example = "43")
    private Integer parentReadWriteLockVersion ;

    @ApiModelProperty(value = "读写锁", example = "")
    private BatchReadWriteLockBaseVO readWriteLockVO;

    @ApiModelProperty(value = "任务名称", example = "test")
    private String name;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr，2 sync ，3 python", example = "0", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "计算类型 0实时，1 离线", example = "1", required = true)
    private Integer computeType;

    @ApiModelProperty(value = "执行引擎类型 0 flink, 1 spark", example = "1")
    private Integer engineType;

    @ApiModelProperty(value = "sql 文本", example = "show tables", required = true)
    private String sqlText;

    @ApiModelProperty(value = "任务参数", example = "{}")
    private String taskParams;

    @ApiModelProperty(value = "调度配置", example = "")
    private String scheduleConf;

    @ApiModelProperty(value = "周期类型", example = "0")
    private Integer periodType;

    @ApiModelProperty(value = "调度状态", example = "1", required = true)
    private Integer scheduleStatus;

    @ApiModelProperty(value = "提交状态", example = "0")
    private Integer submitStatus;

    @ApiModelProperty(value = "任务发布状态，前端使用D", example = "1")
    private Integer status;

    @ApiModelProperty(value = "最后修改task的用户", example = "3")
    private Long modifyUserId;
    
    @ApiModelProperty(value = "新建task的用户", example = "3")
    private Long createUserId;
    
    @ApiModelProperty(value = "负责人id", example = "111")
    private Long ownerUserId;
    
    @ApiModelProperty(value = "任务版本 ID", example = "14")
    private Integer version;

    @ApiModelProperty(value = "节点 ID", example = "7")
    private Long nodePid;
    
    @ApiModelProperty(value = "任务描述", example = "tes")
    private String taskDesc;
    
    @ApiModelProperty(value = "入口类", example = "1")
    private String mainClass;

    @ApiModelProperty(value = "参数 ID", example = "1")
    private String exeArgs;
  
    @ApiModelProperty(value = " 所属工作流id", example = "1")
    private Long flowId = 0L;
    
    @ApiModelProperty(value = "是否过期", hidden = true)
    private Integer isExpire;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID",  hidden = true)
    private Long projectId;
   
    @ApiModelProperty(value = "平台类别", hidden = true)
    private Integer appType;

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id = 0L;
    
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;
    
    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;
    
    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "组件版本号", example = "111")
    private String componentVersion;

    public String getDtToken() {
        return dtToken;
    }

    public void setDtToken(String dtToken) {
        this.dtToken = dtToken;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    @Override
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

    public Boolean getPreSave() {
        return preSave;
    }

    public void setPreSave(Boolean preSave) {
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

    public List<BatchTaskBaseVO> getDependencyTasks() {
        return dependencyTasks;
    }

    public void setDependencyTasks(List<BatchTaskBaseVO> dependencyTasks) {
        this.dependencyTasks = dependencyTasks;
    }

    public String getPublishDesc() {
        return publishDesc;
    }

    public void setPublishDesc(String publishDesc) {
        this.publishDesc = publishDesc;
    }

    public Integer getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(Integer lockVersion) {
        this.lockVersion = lockVersion;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
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

    public Integer getCreateModel() {
        return createModel;
    }

    public void setCreateModel(Integer createModel) {
        this.createModel = createModel;
    }

    public Integer getOperateModel() {
        return operateModel;
    }

    public void setOperateModel(Integer operateModel) {
        this.operateModel = operateModel;
    }

    public Integer getSyncModel() {
        return syncModel;
    }

    public void setSyncModel(Integer syncModel) {
        this.syncModel = syncModel;
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

    public List<BatchTaskResourceParamVO> getToUpdateTasks() {
        return toUpdateTasks;
    }

    public void setToUpdateTasks(List<BatchTaskResourceParamVO> toUpdateTasks) {
        this.toUpdateTasks = toUpdateTasks;
    }

    public Boolean getEditBaseInfo() {
        return isEditBaseInfo;
    }

    public void setEditBaseInfo(Boolean editBaseInfo) {
        isEditBaseInfo = editBaseInfo;
    }

    public Integer getParentReadWriteLockVersion() {
        return parentReadWriteLockVersion;
    }

    public void setParentReadWriteLockVersion(Integer parentReadWriteLockVersion) {
        this.parentReadWriteLockVersion = parentReadWriteLockVersion;
    }

    public BatchReadWriteLockBaseVO getReadWriteLockVO() {
        return readWriteLockVO;
    }

    public void setReadWriteLockVO(BatchReadWriteLockBaseVO readWriteLockVO) {
        this.readWriteLockVO = readWriteLockVO;
    }

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

    @Override
    public Long getTenantId() {
        return tenantId;
    }

    @Override
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
