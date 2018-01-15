package com.dtstack.rdos.engine.send;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年1月2日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class Urls {

	public final static String ROOT = "/node";

	public final static String ACTION = String.format("%s/%s",ROOT,"action");

	public final static String MIGRATION = String.format("%s/%s",ROOT,"migration");

    public final static String MIGRATE = String.format("%s/%s",MIGRATION,"migrate");

    public final static String START = String.format("%s/%s", ACTION,"start");
	
	public final static String STOP = String.format("%s/%s",ACTION, "stop");

	public final static String SUBMIT = String.format("%s/%s",ACTION, "submit");

	public final static String CHECK = String.format("%s/%s",ACTION, "checkCanDistribute");



}
