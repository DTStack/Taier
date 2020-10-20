package com.dtstack.engine.alert.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class CustomDiscardPolicy implements RejectedExecutionHandler{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public CustomDiscardPolicy() {
		
	}
	public CustomDiscardPolicy(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		logger.warn("Task " + r.toString() +        " discard ");
		
	}
	
}
