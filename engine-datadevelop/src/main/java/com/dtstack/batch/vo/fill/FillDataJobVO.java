package com.dtstack.batch.vo.fill;

import java.util.Objects;

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
    private String jobId;

    /**
     * 实例状态
     */
    private Integer status;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 开始时间
     */
    private String exeStartTime;

    /**
     * 运行时长
     */
    private String execTime;

    /**
     * 重试次数
     */
    private Integer retryNum;

    /**
     * 责任人
     */
    private String userName;

    /**
     * 责任人id
     */
    private Long userId;

    /**
     * 工作流id
     */
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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public String getExeStartTime() {
        return exeStartTime;
    }

    public void setExeStartTime(String exeStartTime) {
        this.exeStartTime = exeStartTime;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFlowJobId() {
        return flowJobId;
    }

    public void setFlowJobId(String flowJobId) {
        this.flowJobId = flowJobId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FillDataJobVO that = (FillDataJobVO) o;
        return Objects.equals(jobId, that.jobId) && Objects.equals(status, that.status) && Objects.equals(jobName, that.jobName) && Objects.equals(taskType, that.taskType) && Objects.equals(cycTime, that.cycTime) && Objects.equals(exeStartTime, that.exeStartTime) && Objects.equals(execTime, that.execTime) && Objects.equals(retryNum, that.retryNum) && Objects.equals(userName, that.userName) && Objects.equals(userId, that.userId) && Objects.equals(flowJobId, that.flowJobId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, status, jobName, taskType, cycTime, exeStartTime, execTime, retryNum, userName, userId, flowJobId);
    }

    @Override
    public String toString() {
        return "FillDataJobVO{" +
                "jobId='" + jobId + '\'' +
                ", status=" + status +
                ", jobName='" + jobName + '\'' +
                ", taskType=" + taskType +
                ", cycTime='" + cycTime + '\'' +
                ", exeStartTime='" + exeStartTime + '\'' +
                ", exeTime='" + execTime + '\'' +
                ", retryNum=" + retryNum +
                ", userName='" + userName + '\'' +
                ", userId=" + userId +
                ", flowJobId='" + flowJobId + '\'' +
                '}';
    }
}
