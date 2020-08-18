package com.dtstack.engine.api.vo.schedule.job;

import java.util.Date;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 11:04 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ScheduleJobTaskRecentInfoVO {

    private Date execStartTime;
    private String jobId;
    private String type;
    private String status;
    private Long execTime;

    public Date getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Date execStartTime) {
        this.execStartTime = execStartTime;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }
}
