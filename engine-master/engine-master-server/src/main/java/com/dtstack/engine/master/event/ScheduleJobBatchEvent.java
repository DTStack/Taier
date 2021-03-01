package com.dtstack.engine.master.event;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public class ScheduleJobBatchEvent {
    private List<String> jobIds;

    private Integer status;

    private ScheduleJobBatchEvent() {
    }

    public ScheduleJobBatchEvent(String jobId,Integer status) {
        this.status = status;
        this.jobIds = Lists.newArrayList(jobId);
    }

    public ScheduleJobBatchEvent(List<String> jobIds, Integer status) {
        this.jobIds = jobIds;
        this.status = status;
    }

    public List<String> getJobIds() {
        return jobIds;
    }

    public void setJobIds(List<String> jobIds) {
        this.jobIds = jobIds;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ScheduleJobBatchEvent{" +
                "jobIds=" + jobIds +
                ", status=" + status +
                '}';
    }
}
