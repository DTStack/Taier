package com.dtstack.rdos.engine.db.dataobject;

/**
 * sishu.yss
 */

public class RdosBatchServerLog extends TenantProjectObject{

    private String jobId;

    private String logInfo;

    private String engineLog;

    private Long actionLogId;

    public Long getActionLogId() {
        return actionLogId;
    }

    public void setActionLogId(Long actionLogId) {
        this.actionLogId = actionLogId;
    }


    public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
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
}
