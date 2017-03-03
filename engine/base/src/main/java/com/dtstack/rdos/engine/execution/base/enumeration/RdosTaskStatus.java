package com.dtstack.rdos.engine.execution.base.enumeration;

public enum RdosTaskStatus {

	UNSUBMIT(0),CREATED(1),SCHEDULED(2),DEPLOYING(3),RUNNING(4),FINISHED(5),CANCELING(6),CANCELED(7),FAILED(8);
	
	private int status;
	
	RdosTaskStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return this.status;
	}
}
