package com.dtstack.task.server.send;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/29
 */
public class Urls {

	public final static String ROOT = "api/task";

	public final static String WORK_NODE = String.format("%s/%s",ROOT,"workNode");

    public final static String MASTER_SEND_JOBS = String.format("%s/%s",WORK_NODE,"masterSendJobs");

}
