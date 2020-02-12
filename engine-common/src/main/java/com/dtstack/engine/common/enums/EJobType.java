package com.dtstack.engine.common.enums;

/**
 * 
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public enum EJobType{

    SQL(0),
    MR(1),//默认离线MR任务---java_job
    SYNC(2),//数据同步任务
    PYTHON(3),//离线MR任务--python_job
    KYLIN(4);
    
    private int type;
    
    EJobType(int type){
    	this.type = type;
    }
    
    public static EJobType getEJobType(int type){
    	EJobType[] eJobTypes = EJobType.values();
    	for(EJobType eJobType:eJobTypes){
    		if(eJobType.type == type){
    			return eJobType;
    		}
    	}
    	return null;
    }
    
    public int getType(){
    	return this.type;
    }
}