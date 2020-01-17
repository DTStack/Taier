package com.dtstack.task.domain;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BatchJobAlarm  extends AppTenantEntity {

    private Long jobId;

    private Long taskId;

    private Integer taskStatus;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }
}
