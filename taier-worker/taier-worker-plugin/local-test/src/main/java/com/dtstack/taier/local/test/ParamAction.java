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

package com.dtstack.taier.local.test;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ParamAction {

	private static final Logger logger = LoggerFactory.getLogger(ParamAction.class);

	private String jobId;

	private String engineTaskId;

	private String applicationId;

	private String name;

    private Integer taskType;

    private String engineType;

    private Integer computeType;

	//实时独有
	private String externalPath;

	private String sqlText;

	private String taskParams;

	private String exeArgs;

	private String groupName;

	private long priority;

	private long generateTime = System.currentTimeMillis();

	private Integer maxRetryNum;

	private long lackingCount;

	private Long tenantId;

	private String deployMode;

	/**
	 * 重试超时时间
	 */
	private long submitExpiredTime;

	/**
	 * 重试间隔时间
	 */
	private Long retryIntervalTime;

    /**
     * 任务运行版本 如flink 1.8 1.10
     */
	private String componentVersion;

	private Map<String, Object> pluginInfo;

	private Integer type;

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Map<String, Object> getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(Map<String, Object> pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getComponentVersion() {
		return componentVersion;
	}

	public void setComponentVersion(String componentVersion) {
		this.componentVersion = componentVersion;
	}

	public String getDeployMode() {
		return deployMode;
	}

	public void setDeployMode(String deployMode) {
		this.deployMode = deployMode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getComputeType() {
		return computeType;
	}

	public void setComputeType(Integer computeType) {
		this.computeType = computeType;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(String taskParams) {
		this.taskParams = taskParams;
	}

	public String getEngineTaskId() {
		return engineTaskId;
	}

	public void setEngineTaskId(String engineTaskId) {
		this.engineTaskId = engineTaskId;
	}

	public String getExternalPath() {
		return externalPath;
	}

	public void setExternalPath(String externalPath) {
		this.externalPath = externalPath;
	}

	public String getEngineType() {
		return engineType;
	}

	public void setEngineType(String engineType) {
		this.engineType = engineType;
	}


	public String getExeArgs() {
		return exeArgs;
	}


	public void setExeArgs(String exeArgs) {
		this.exeArgs = exeArgs;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public long getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(long generateTime) {
		this.generateTime = generateTime;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getMaxRetryNum() {
		return maxRetryNum;
	}

	public void setMaxRetryNum(Integer maxRetryNum) {
		this.maxRetryNum = maxRetryNum;
	}


	public long getLackingCount() {
		return lackingCount;
	}

	public void setLackingCount(long lackingCount) {
		this.lackingCount = lackingCount;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}


	public long getSubmitExpiredTime() {
		return submitExpiredTime;
	}

	public void setSubmitExpiredTime(long submitExpiredTime) {
		this.submitExpiredTime = submitExpiredTime;
	}

	public Long getRetryIntervalTime() {
		return retryIntervalTime;
	}

	public void setRetryIntervalTime(Long retryIntervalTime) {
		this.retryIntervalTime = retryIntervalTime;
	}

	@Override
	public String toString() {
		String jsonStr = "";
		try{
			jsonStr = JSONObject.toJSONString(this);
		}catch (Exception e){
			//不应该发生
			logger.error("", e);
		}

		return jsonStr;
	}
}
