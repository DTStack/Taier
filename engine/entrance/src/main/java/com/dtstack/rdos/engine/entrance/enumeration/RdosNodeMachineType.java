package com.dtstack.rdos.engine.entrance.enumeration;

public enum RdosNodeMachineType {
	
	MASTER(0),SLAVE(1);
	
	private int type ;
	
	RdosNodeMachineType(int type){
		this.type = type;
	}
	
	public int getType(){
		return this.type;
	}

}
