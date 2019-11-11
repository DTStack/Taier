package com.dtstack.engine.dtscript.web;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年2月17日 下午9:21:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class HttpCommon {

	public static Object[] getUrlPort(String address){
		Object[] object =new Object[2]; 
		String[] la = address.split(":");
		object[0] = la[0].trim();
		object[1] = Integer.parseInt(la[1].trim());
		return object;
	}
}
