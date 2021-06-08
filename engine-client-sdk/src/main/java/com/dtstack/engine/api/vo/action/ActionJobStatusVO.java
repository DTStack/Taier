package com.dtstack.engine.api.vo.action;

import java.sql.Timestamp;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 1:40 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ActionJobStatusVO {

    private String jobId;
    private Integer status;
    private Timestamp execStartTime;
    private Timestamp execEndTime;
    private Long execTime;
    private Integer retryNum;

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

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Timestamp getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Timestamp execEndTime) {
        this.execEndTime = execEndTime;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }
}
