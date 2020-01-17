package com.dtstack.task.server.vo;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchPeriodInfoVO {
    private Long jobId;
    private String cycTime;
    private Integer status;

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
