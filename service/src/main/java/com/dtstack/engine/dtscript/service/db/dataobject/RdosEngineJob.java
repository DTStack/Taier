package com.dtstack.engine.dtscript.service.db.dataobject;


import java.util.Date;

/**
 * 
 * @author sishu.yss
 *
 */
public class RdosEngineJob extends DataObject{

    /**
     * 工作任务id
     */
    private String jobId;

    private String jobName;

    /**
     * 执行引擎任务id
     */
    private String engineJobId;

    /**
     * 任务状态
     * UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)
     */
    private Byte status;

    private Date execStartTime;

    private Date execEndTime;

    private Long execTime;

    private String logInfo;

    private String engineLog;

    private String retryTaskParams;

    private long pluginInfoId;

    private Integer sourceType;

    private String applicationId;
    
    private Integer computeType;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    private Integer retryNum;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
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

    public Long getExecTime() {
       return execTime;
    }

    public void setExecTime(Long execTime) {
       this.execTime = execTime;
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

    public long getPluginInfoId() {
        return pluginInfoId;
    }

    public void setPluginInfoId(long pluginInfoId) {
        this.pluginInfoId = pluginInfoId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public String getRetryTaskParams() {
        return retryTaskParams;
    }

    public void setRetryTaskParams(String retryTaskParams) {
        this.retryTaskParams = retryTaskParams;
    }

	public Integer getComputeType() {
		return computeType;
	}

	public void setComputeType(Integer computeType) {
		this.computeType = computeType;
	}
}
