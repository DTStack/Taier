package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/12 10:25 上午
 */

public class TaskListResultVO {

    @ApiModelProperty(value = "ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "任务id", example = "s00001")
    private String jobId;

    @ApiModelProperty(value = "任务名称", example = "name")
    private String name;

    @ApiModelProperty(value = "任务状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "任务类型 0 sql，1 mr", example = "1")
    private Integer taskType;

    @ApiModelProperty(value = "创建用户名称", example = "admin@dtstack.com")
    private String createUserName;

    @ApiModelProperty(value = "修改用户名称", example = "admin@dtstack.com")
    private String modifyUserName;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "最近修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;
    
    @ApiModelProperty(value = "描述", example = "描述")
    private String  taskDesc;

    @ApiModelProperty(value = "最近提交时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp submitModified;

    @ApiModelProperty(value = "开始时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp execStartTime;

    /**
     * 前端用来标示 finkSql 重跑向导模式选择支持选择time消费
     */
    @ApiModelProperty(value = "0是向导模式，1是脚本模式", example = "1")
    private Integer createModel = 0;

    @ApiModelProperty(value = "组件版本")
    private String componentVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getModifyUserName() {
        return modifyUserName;
    }

    public void setModifyUserName(String modifyUserName) {
        this.modifyUserName = modifyUserName;
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

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public Timestamp getSubmitModified() {
        return submitModified;
    }

    public void setSubmitModified(Timestamp submitModified) {
        this.submitModified = submitModified;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Integer getCreateModel() {
        return createModel;
    }

    public void setCreateModel(Integer createModel) {
        this.createModel = createModel;
    }

    public String getComponentVersion() {
        return componentVersion;
    }

    public void setComponentVersion(String componentVersion) {
        this.componentVersion = componentVersion;
    }
}
