package com.dtstack.rdos.engine.db.dataobject;

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

	/**
	 * 计算引擎任务id
	 */
    private String engineTaskId;

    /**
     * 任务状态
     */
    private Byte status;

	private Date execStartTime;

	private Date execEndTime;

	private Long exeTime;
	
    private String logInfo;

    private String engineLog;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
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

    public Long getExeTime() {
        return exeTime;
    }

    public void setExeTime(Long exeTime) {
        this.exeTime = exeTime;
    }
}
