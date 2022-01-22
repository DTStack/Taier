package com.dtstack.taiga.develop.vo.fill;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/9 4:46 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FillDataJobVO {

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id",example = "")
    private String jobId;

    /**
     * 实例状态
     */
    @ApiModelProperty(value = "实例状态",example = "0")
    private Integer status;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称",example = "0")
    private String taskName;

    /**
     * 任务类型
     */
    @ApiModelProperty(value = "任务类型",example = "0")
    private Integer taskType;

    /**
     * 计划时间
     */
    @ApiModelProperty(value = "计划时间",example = "2021-12-24 16:11:53")
    private String cycTime;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间",example = "2021-12-24 16:11:53")
    private String startExecTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间",example = "2021-12-24 16:11:53")
    private String endExecTime;

    /**
     * 运行时长
     */
    @ApiModelProperty(value = "运行时长",example = "0")
    private String execTime;

    /**
     * 重试次数
     */
    @ApiModelProperty(value = "重试次数",example = "0")
    private Integer retryNum;

    /**
     * 责任人
     */
    @ApiModelProperty(value = "责任人",example = "\tadmin@dtstack.com")
    private String ownerName;

    /**
     * 责任人id
     */
    @ApiModelProperty(value = "责任人id",example = "1")
    private Long ownerId;

    /**
     * 工作流id
     */
    @ApiModelProperty(value = "工作流id",example = "1")
    private String flowJobId;

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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }
}
