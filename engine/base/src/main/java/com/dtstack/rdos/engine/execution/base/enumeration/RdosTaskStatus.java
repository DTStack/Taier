package com.dtstack.rdos.engine.execution.base.enumeration;

public enum RdosTaskStatus {

	UNSUBMIT(0),SUBMIT(1),RUNNING(2),CANCLE(3),STOPED(4),FAIL(5);
	
	private int status;
	
	RdosTaskStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return this.status;
	}
}
