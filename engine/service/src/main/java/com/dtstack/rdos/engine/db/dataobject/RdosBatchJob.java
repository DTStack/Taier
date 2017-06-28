package com.dtstack.rdos.engine.db.dataobject;


import java.util.Date;

/**
 * 
 * @author sishu.yss
 *
 */
public class RdosBatchJob extends TenantProjectObject{
	

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
	     * 任务状态 	UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8)
	     */
	    private int status;


	    /**
	     * 任务id
	     */
	    private long taskId;

	    /**
	     * 发起操作的用户
	     */
	    private long createUserId;

	    /**
	     * 0正常调度 1补数据
	     */
	    private int type;

	    /**
	     * 业务日期 yyyymmddhhmmss
	     */
	    private String businessDate;

		private Date execStartTime;

		private Date execEndTime;

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

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public long getTaskId() {
			return taskId;
		}

		public void setTaskId(long taskId) {
			this.taskId = taskId;
		}

		public long getCreateUserId() {
			return createUserId;
		}

		public void setCreateUserId(long createUserId) {
			this.createUserId = createUserId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getBusinessDate() {
			return businessDate;
		}

		public void setBusinessDate(String businessDate) {
			this.businessDate = businessDate;
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
