package com.dtstack.rdos.engine.service.db.dataobject;

import java.util.Date;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosEngineStreamJob extends DataObject{
	
	/**
	 * 任务id
	 */
	private String taskId;

	private String taskName;

	/**
	 * 计算引擎任务id
	 */
    private String engineTaskId;

    private String applicationId;

    /**
     * 任务状态
     */
    private Byte status;

	private Date execStartTime;

	private Date execEndTime;

	private Long execTime;
	
    private String logInfo;

    private String engineLog;

    private long pluginInfoId;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getEngineTaskId() {
		return engineTaskId;
	}

	public void setEngineTaskId(String engineTaskId) {
		this.engineTaskId = engineTaskId;
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

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
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

    public Long getExecTime() {
        return execTime;
    }

    public void setExecTime(Long execTime) {
        this.execTime = execTime;
    }

    public long getPluginInfoId() {
        return pluginInfoId;
    }

    public void setPluginInfoId(long pluginInfoId) {
        this.pluginInfoId = pluginInfoId;
    }

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
}
