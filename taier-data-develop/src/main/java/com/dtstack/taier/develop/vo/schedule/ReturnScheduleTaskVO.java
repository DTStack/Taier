package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 9:07 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class ReturnScheduleTaskVO {

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id", example = "1L")
    private Long taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称", example = "这是一个任务")
    private String name;

    /**
     * 修改时间
     */
    @ApiModelProperty(value = "提交时间", example = "12312333333")
    private Timestamp gmtModified;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型", example = "1")
    private Integer taskType;

    /**
     * 调度类型
     */
    @ApiModelProperty(value = "调度类型", example = "1")
    private Integer periodType;

    /**
     * 责任人ID
     */
    @ApiModelProperty(value = "责任人ID", example = "1")
    private Long ownerUserId;

    /**
     * 责任人
     */
    @ApiModelProperty(value = "责任人名称", example = "admin@dtstack.com")
    private String ownerUserName;

    /**
     * 调度状态：0 正常 1冻结 2停止
     */
    @ApiModelProperty(value = "调度状态：0 正常 1冻结 2停止", example = "0")
    private Integer scheduleStatus;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public Integer getScheduleStatus() {
        return scheduleStatus;
    }

    public void setScheduleStatus(Integer scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
