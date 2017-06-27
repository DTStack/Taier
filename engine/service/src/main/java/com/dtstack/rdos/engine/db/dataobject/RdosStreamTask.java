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
public class RdosStreamTask extends TenantProjectObject{
	
	/**
	 * 任务id
	 */
	private String taskId;
	
	/**
	 * 计算引擎任务id
	 */
    private String engineTaskId;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务状态
     */
    private Byte status;
    
    /**
     * 任务类型
     */
    private Byte taskType;

	/**
	 * 执行引擎类型
	 */
	private Byte engineType;
    
    /**
     * 计算类型
     */
    private Byte computeType;
    
    
    /**
     * sql 脚本
     */
    private String sqlText;
    
    /**
     * 任务参数
     */
    private String taskParams;
    
    /**
     * 最后修改任务用户id
     */
    private Long modifyUserId;
    
    /**
     * 新建认为用户id
     */
    private Long createUserId;
    
    private Long version;

	private Date execStartTime;

	private Date execEndTime;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getTaskType() {
		return taskType;
	}

	public void setTaskType(Byte taskType) {
		this.taskType = taskType;
	}

	public Byte getComputeType() {
		return computeType;
	}

	public void setComputeType(Byte computeType) {
		this.computeType = computeType;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(String taskParams) {
		this.taskParams = taskParams;
	}

	public Long getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(Long modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public Long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Byte getEngineType() {
		return engineType;
	}

	public void setEngineType(Byte engineType) {
		this.engineType = engineType;
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
}
