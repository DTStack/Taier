package com.dtstack.engine.api.vo;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class SchedulePeriodInfoVO {
    private Long jobId;
    private String cycTime;
    private Integer status;

    private Long taskId;

    private Integer version;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
