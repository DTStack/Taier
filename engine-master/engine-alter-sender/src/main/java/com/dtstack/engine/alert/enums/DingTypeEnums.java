package com.dtstack.engine.alert.enums;

public enum DingTypeEnums {
	
	TEXT("text"),LINK("link"),MARKDOWN("markdown"),ACTION_CARD("actionCard"),FEED_CARD("feedCard");

	private String msg;

	DingTypeEnums(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}

	public static DingTypeEnums getDingTypeEnum(String type){
		if("text".equals(type)){
			return TEXT;
		} else if("markdown".equals(type)){
			return MARKDOWN;
		}
		return MARKDOWN;
	}

}
