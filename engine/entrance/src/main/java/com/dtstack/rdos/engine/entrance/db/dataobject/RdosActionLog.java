package com.dtstack.rdos.engine.entrance.db.dataobject;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosActionLog extends DataObject{
	
	/**
	 * 操作类型
	 */
	private Byte actionType;
	
	/**
	 * 操作状态
	 */
	private Byte status;
	
	/**
	 * 任务 id
	 */
	private String taskId;
	
	/**
	 * 是否恢复最近一次的点位
	 */
	private Byte isRestoration;
	
	
	/**
	 * 发起操作的用户id
	 */
	private Long createUserId;


	public Byte getActionType() {
		return actionType;
	}


	public void setActionType(Byte actionType) {
		this.actionType = actionType;
	}


	public Byte getStatus() {
		return status;
	}


	public void setStatus(Byte status) {
		this.status = status;
	}


	public String getTaskId() {
		return taskId;
	}


	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}


	public Byte getIsRestoration() {
		return isRestoration;
	}


	public void setIsRestoration(Byte isRestoration) {
		this.isRestoration = isRestoration;
	}


	public Long getCreateUserId() {
		return createUserId;
	}


	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
}
