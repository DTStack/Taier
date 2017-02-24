package com.dtstack.rdos.engine.entrance.enumeration;

public enum RdosTaskStatus {

	SUBMIT(0),RUNNING(1),CANCLE(2),STOPED(3),FAIL(4);
	
	private int status;
	
	RdosTaskStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return this.status;
	}
	
}
