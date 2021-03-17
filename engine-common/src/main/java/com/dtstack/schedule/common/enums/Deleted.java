package com.dtstack.schedule.common.enums;


/**
 * 
 * @author sishu.yss
 *
 */
public enum Deleted {
	
	NORMAL(0),DELETED(1);
	
	private Integer status;
	
	Deleted(Integer status){
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public static Deleted getByStatus(Integer status){

		for (Deleted value : Deleted.values()) {
			if(value.status.equals(status)){
				return value;
			}
		}
		return null;
	}

}
