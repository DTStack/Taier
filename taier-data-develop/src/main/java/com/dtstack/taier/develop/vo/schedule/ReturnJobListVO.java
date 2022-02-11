package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/23 4:16 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnJobListVO {

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id",example = "123123")
    private String jobId;

    /**
     * 实例状态
     */
    @ApiModelProperty(value = "实例状态",example = "0")
    private Integer status;

    /**
     * 任务id
     */
    @ApiModelProperty(value = "任务id",example = "0")
    private Long taskId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称",example = "任务名称")
    private String taskName;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型",example = "0")
    private Integer taskType;

    /**
     * 调度类型
     */
    @ApiModelProperty(value = "调度类型",example = "0")
    private Integer periodType;

    /**
     * 计划时间
     */
    @ApiModelProperty(value = "计划时间",example = "2021-12-21 21:00:00")
    private String cycTime;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间",example = "2021-12-21 21:00:00")
    private String startExecTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间",example = "2021-12-21 21:00:00")
    private String endExecTime;

    /**
     * 运行时长
     */
    @ApiModelProperty(value = "运行时长",example = "38秒")
    private String execTime;

    /**
     * 当前重试次数
     */
    @ApiModelProperty(value = "当前重试次数",example = "3")
    private Integer retryNum;

    /**
     * 责任人id
     */
    @ApiModelProperty(value = "责任人id",example = "1")
    private Long ownerId;

    /**
     * 责任人名称
     */
    @ApiModelProperty(value = "责任人名称",example = "1")
    private String ownerName;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Integer getPeriodType() {
        return periodType;
    }

    public void setPeriodType(Integer periodType) {
        this.periodType = periodType;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public String getStartExecTime() {
        return startExecTime;
    }

    public void setStartExecTime(String startExecTime) {
        this.startExecTime = startExecTime;
    }

    public String getEndExecTime() {
        return endExecTime;
    }

    public void setEndExecTime(String endExecTime) {
        this.endExecTime = endExecTime;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
