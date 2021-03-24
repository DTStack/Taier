package com.dtstack.engine.alert.pool;

import com.dtstack.engine.alert.exception.AlterRejectedExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class AlterDiscardPolicy implements RejectedExecutionHandler{
	
	private Logger LOGGER = LoggerFactory.getLogger(getClass());

	private String threadName;

	private String type;
	
	public AlterDiscardPolicy() {
	}

	public AlterDiscardPolicy(String threadName, String type) {
		this.threadName = threadName;
		this.type = type;
	}

	public AlterDiscardPolicy(Logger logger) {
		this.LOGGER = logger;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		LOGGER.warn("Thread：{} is discard,type: {}",threadName,type);
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
