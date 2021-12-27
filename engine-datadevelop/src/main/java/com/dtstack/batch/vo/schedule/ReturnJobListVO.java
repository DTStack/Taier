package com.dtstack.batch.vo.schedule;

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
    private String jobId;

    /**
     * 任务id
     */
    private Long taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 调度类型
     */
    private Integer periodType;

    /**
     * 计划时间
     */
    private String cycTime;

    /**
     * 开始时间
     */
    private String startExecTime;

    /**
     * 结束时间
     */
    private String endExecTime;

    /**
     * 运行时长
     */
    private String execTime;

    /**
     * 当前重试次数
     */
    private Integer retryNum;

    /**
     * 责任人id
     */
    private Long userId;

    /**
     * 责任人名称
     */
    private String userName;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
