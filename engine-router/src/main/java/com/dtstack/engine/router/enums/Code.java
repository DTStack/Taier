package com.dtstack.engine.router.enums;


/**
 * 
 * @author sishu.yss
 *
 */
public enum Code {
	
	NORMAL(1),REDIRECT(0),FAIL(-1);
	
	private int type;
	
	Code(int type){
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
