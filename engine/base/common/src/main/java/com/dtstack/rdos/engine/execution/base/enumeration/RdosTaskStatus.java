package com.dtstack.rdos.engine.execution.base.enumeration;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum RdosTaskStatus {

	UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELLING(6),CANCELED(7),FAILED(8), SUBMITFAILD(9),
	SUBMITTING(10), RESTARTING(11), MANUALSUCCESS(12), KILLED(13), SUBMITTED(14),NOTFOUND(15),WAITENGINE(16),WAITCOMPUTE(17);
	
	private int status;
	
	RdosTaskStatus(int status){
		this.status = status;
	}
	
	public Integer getStatus(){
		return this.status;
	}

    /**
     * 需要捕获无法转换异常
     * @param taskStatus
     * @return
     */
    public static RdosTaskStatus getTaskStatus(String taskStatus){
	   return RdosTaskStatus.valueOf(taskStatus);
    }
    
    public static boolean needClean(Byte status){
		int sta = status.intValue();
       if(sta==RdosTaskStatus.FINISHED.getStatus()||sta==RdosTaskStatus.FAILED.getStatus()||sta == RdosTaskStatus.SUBMITFAILD.getStatus()
			   || sta == RdosTaskStatus.KILLED.getStatus()||sta == RdosTaskStatus.NOTFOUND.getStatus()){
    	   return true;
       }   
       return false;
    }
}
