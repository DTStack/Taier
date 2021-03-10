package com.dtstack.engine.master.event;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public class ScheduleJobEvent {
    private String jobId;

    private Integer status;

    public String getJobId() {
        return jobId;
    }

    public ScheduleJobEvent() {
    }

    public ScheduleJobEvent(String jobId, Integer status) {
        this.jobId = jobId;
        this.status = status;
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
}
