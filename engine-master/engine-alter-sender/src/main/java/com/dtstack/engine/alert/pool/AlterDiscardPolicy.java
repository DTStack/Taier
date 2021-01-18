package com.dtstack.engine.alert.pool;

import com.dtstack.engine.alert.exception.AlterRejectedExecution;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class AlterDiscardPolicy implements RejectedExecutionHandler{
	
	private Logger logger = LoggerFactory.getLogger(getClass());

	private String threadName;

	private String type;
	
	public AlterDiscardPolicy() {
	}

	public AlterDiscardPolicy(String threadName, String type) {
		this.threadName = threadName;
		this.type = type;
	}

	public AlterDiscardPolicy(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		logger.warn("线程："+threadName + "is discard,type: "+type);
		throw new AlterRejectedExecution();
	}


	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
