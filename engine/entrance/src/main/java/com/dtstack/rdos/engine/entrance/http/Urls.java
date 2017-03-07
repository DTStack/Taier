package com.dtstack.rdos.engine.entrance.http;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年1月2日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class Urls {

	public final static String ROOT = "node";
	
	public final static String ACTION = String.format("%s/%s",ROOT,"action");
	
	public final static String START = String.format("%s/%s", ACTION,"start");
	
	public final static String STOP = String.format("%s/%s",ACTION, "stop");

}
