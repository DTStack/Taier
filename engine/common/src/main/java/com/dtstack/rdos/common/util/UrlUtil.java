package com.dtstack.rdos.common.util;

public class UrlUtil {
	
	public static String getHttpUrl(String node,String path){
		return String.format("http://%s/%s", node,path);
	}

}
