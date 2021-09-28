/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
