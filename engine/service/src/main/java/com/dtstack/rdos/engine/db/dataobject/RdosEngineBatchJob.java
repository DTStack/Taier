package com.dtstack.rdos.engine.db.dataobject;


import java.util.Date;

/**
 * 
 * @author sishu.yss
 *
 */
public class RdosEngineBatchJob extends DataObject{

    /**
     * 工作任务id
     */
    private String jobId;

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

    private long pluginInfoId;

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
}
