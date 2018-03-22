package com.dtstack.rdos.common.util;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月10日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class UrlUtil {
	
	public static String getHttpUrl(String node,String path){
		return String.format("http://%s/%s", node,path);
	}

}
