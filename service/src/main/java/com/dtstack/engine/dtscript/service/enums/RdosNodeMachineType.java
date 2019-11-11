package com.dtstack.engine.dtscript.service.enums;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
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
