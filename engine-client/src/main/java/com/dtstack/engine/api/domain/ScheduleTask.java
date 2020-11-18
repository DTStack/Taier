package com.dtstack.engine.api.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleTask extends AppTenantEntity {

    /**
     * '任务名称'
     */
    @NotNull(message = "任务名称(name)必填")
    @ApiModelProperty(notes = "任务名称")
    private String name;

    /**
     * '任务类型 0 sql，1 mr，2 sync ，3 python
     */
    @NotNull(message = "任务类型(taskType)必填")
    @ApiModelProperty(notes = "任务类型 0 sql，1 mr，2 sync ，3 python")
    private Integer taskType;

    /**
     * '计算类型 0实时，1 离线'
     */
    @NotNull(message = "计算类型(computeType)必填")
    @ApiModelProperty(notes = "计算类型 0实时，1 离线")
    private Integer computeType;

    /**
     * '执行引擎类型 0 flink, 1 spark'
     * {@link com.dtstack.engine.api.enums.ScheduleEngineType}
     */
    @NotNull(message = "引擎类型(engineType)必填")
    @ApiModelProperty(notes = "执行引擎类型 0 flink, 1 spark")
    private Integer engineType;

    /**
     * 'sql 文本'
     */
    @NotNull(message = "sql 文本(sqlText)必填")
    @ApiModelProperty(notes = "sql 文本")
    private String sqlText;

    /**
     * '任务参数'
     */
    @NotNull(message = "任务参数(taskParams)必填")
    @ApiModelProperty(notes = "任务参数")
    private String taskParams;

    /**
     * 调度配置
     */
    @NotNull(message = "调度配置(scheduleConf)必填")
    @ApiModelProperty(notes = "调度配置")
    private String scheduleConf;

    /**
     * 周期类型
     */
    @NotNull(message = "周期类型(periodType)必填")
    @ApiModelProperty(notes = "周期类型")
    private Integer periodType;

    /**
     * 调度状态
     */
    @ApiModelProperty(notes = "调度状态")
    @NotNull(message = "调度状态(scheduleStatus)必填")
    private Integer scheduleStatus;

    /**
     * 启动:0
     * 停止:1
     */
    @ApiModelProperty(notes = "启动:0 停止:1")
    private Integer projectScheduleStatus;

    private Integer submitStatus;

    /**
     * 最后修改task的用户
     */
    @ApiModelProperty(notes = "最后修改task的用户")
    private Long modifyUserId;

    /**
     * 新建task的用户
     */
    @ApiModelProperty(notes = "新建task的用户")
    private Long createUserId;

    /**
     * 负责人id
     */
    @ApiModelProperty(notes = "负责人id")
    private Long ownerUserId;


    private Long nodePid;

    /**
     * 任务描述
     */
    @ApiModelProperty(notes = "任务描述")
    private String taskDesc;

    /**
     * 入口类
     */
    @ApiModelProperty(notes = "入口类")
    private String mainClass;

    private String exeArgs;

    /**
     * 所属工作流id
     */
    @ApiModelProperty(notes = "所属工作流id")
    private Long flowId;

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

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Integer getProjectScheduleStatus() {
        return projectScheduleStatus;
    }

    public void setProjectScheduleStatus(Integer projectScheduleStatus) {
        this.projectScheduleStatus = projectScheduleStatus;
    }
}
