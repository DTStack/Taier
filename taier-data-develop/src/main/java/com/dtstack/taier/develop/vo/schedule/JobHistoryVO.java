
package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class JobHistoryVO {


    @ApiModelProperty(value = "jobId")
    private String jobId;

    @ApiModelProperty(value = "执行开始时间")
    private Date execStartTime;

    @ApiModelProperty(value = "执行结束时间")
    private Date execEndTime;

    @ApiModelProperty(value = "engineJobId")
    private String engineJobId;

    @ApiModelProperty(value = "applicationId")
    private String applicationId;

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
