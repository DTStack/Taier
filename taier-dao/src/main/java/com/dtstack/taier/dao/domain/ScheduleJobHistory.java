package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("schedule_job_history")
public class ScheduleJobHistory {

    @TableId(value="id", type= IdType.AUTO)
    private Long id;

    private String jobId;

    private Date execStartTime;

    private Date execEndTime;

    private String engineJobId;

    private String applicationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Date getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Date execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Date getExecEndTime() {
        return execEndTime;
    }

    public void setExecEndTime(Date execEndTime) {
        this.execEndTime = execEndTime;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
