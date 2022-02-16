package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 12:06 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskNodeVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id")
    private Long taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型")
    private Integer taskType;

    /**
     * 调度状态：0 正常 1冻结 2停止
     */
    @ApiModelProperty(value = "调度状态：0 正常 1冻结 2停止")
    private Integer scheduleStatus;

    /**
     * 发布时间
     */
    @ApiModelProperty(value = "发布时间")
    private Timestamp gmtCreate;

    /**
     * 是否是工作流任务
     */
    @ApiModelProperty(value = "是否是工作流任务")
    private Boolean isFlowTask;

    /**
     * 子节点
     */
    private List<TaskNodeVO> childNode;

    /**
     * 父节点
     */
    private List<TaskNodeVO> parentNode;


    /**
     * 租户名称
     */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "租户id")
    private Long tenantId;

    @ApiModelProperty(value = "操作人")
    private String operatorName;

    @ApiModelProperty(value = "操作id")
    private Long operatorId;

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getFlowTask() {
        return isFlowTask;
    }

    public void setFlowTask(Boolean flowTask) {
        isFlowTask = flowTask;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Boolean getIsFlowTask() {
        return isFlowTask;
    }

    public void setIsFlowTask(Boolean flowTask) {
        isFlowTask = flowTask;
    }

    public List<TaskNodeVO> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<TaskNodeVO> childNode) {
        this.childNode = childNode;
    }

    public List<TaskNodeVO> getParentNode() {
        return parentNode;
    }

    public void setParentNode(List<TaskNodeVO> parentNode) {
        this.parentNode = parentNode;
    }


}
