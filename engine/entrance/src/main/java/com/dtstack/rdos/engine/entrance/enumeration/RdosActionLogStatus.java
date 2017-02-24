package com.dtstack.rdos.engine.entrance.enumeration;

public enum RdosActionLogStatus {

	UNSTART(0),SUCCESS(1),FAIL(2);
	
	private int status;
	
	RdosActionLogStatus(int status){
		this.status = status;
	}
	
	public int getStatus(){
		return this.status;
	}
}
