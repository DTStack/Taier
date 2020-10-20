package com.dtstack.engine.alert.enums;

public enum DingTypeEnums {
	
	TEXT,MARKDOWN;
	
	public static DingTypeEnums getDingTypeEnum(String type){
		if("text".equals(type)){
			return TEXT;
		} else if("markdown".equals(type)){
			return MARKDOWN;
		}
		return MARKDOWN;
	}

}
