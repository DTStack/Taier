package com.dtstack.rdos.engine.db.dataobject;

/**
 *
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosBatchActionLog extends TenantProjectObject{

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 操作状态
     */
    private Integer status;

    /**
     * 任务 id
     */
    private String jobId;


    /**
     * 发起操作的用户id
     */
    private Long createUserId;


    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
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

	public Long getCreateUserId() {
        return createUserId;
    }


    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}
