package com.dtstack.engine.dtscript.service.send;

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

    public final static String MASTER_SEND_JOBS = String.format("%s/%s",ACTION,"masterSendJobs");

	public final static String SUBMIT = String.format("%s/%s", ACTION, "submit");

	public final static String WORK_SEND_STOP = String.format("%s/%s", ACTION, "workSendStop");



}
