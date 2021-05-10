package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.Objects;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
@ApiModel
public class ScheduleEngineJob extends AppTenantEntity {

    private Integer status;

    private String jobId;

    private String engineJobId;

    private String logInfo;

    private String engineLog;

    private Timestamp execStartTime;

    private Timestamp execEndTime;

    /**
     * 当前执行时间单位为s
     */
    @ApiModelProperty(notes = "当前执行时间单位为s")
    private Long execTime;

    private Integer retryNum = 0;

    private Integer versionId;

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public ScheduleEngineJob() {
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo = logInfo;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
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

    public ScheduleEngineJob(ScheduleJob scheduleJob) {
        if (null != scheduleJob) {
            this.retryNum = scheduleJob.getRetryNum();
            this.status = scheduleJob.getStatus();
            this.execTime = scheduleJob.getExecTime();
            if (null != scheduleJob.getExecStartTime()) {
                this.execStartTime = new Timestamp(scheduleJob.getExecStartTime().getTime());
            }
            if (null != scheduleJob.getExecEndTime()) {
                this.execEndTime = new Timestamp(scheduleJob.getExecEndTime().getTime());
            }
            this.jobId = scheduleJob.getJobId();
            this.logInfo = scheduleJob.getLogInfo();
        }
    }
}
