package com.dtstack.rdos.engine.entrance.enumeration;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
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
