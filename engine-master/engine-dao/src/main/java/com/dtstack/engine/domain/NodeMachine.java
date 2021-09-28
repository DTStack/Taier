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

package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 *
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
@ApiModel
public class NodeMachine extends DataObject{
	
	/**
	 * node 主机ip
	 */
	@ApiModelProperty(notes = "主机ip")
	private String ip;
	
	/**
	 * node 端口
	 */
	@ApiModelProperty(notes = "端口")
	private Long port;
	
	/**
	 * 0 master 1 slave
	 */
	@ApiModelProperty(notes = "0 master 1 slave")
	private Integer machineType;

	/**
	 * web(web应用),engine(执行引擎应用)
	 */
	@ApiModelProperty(notes = "web(web应用),engine(执行引擎应用)")
	private String appType;

	private String deployInfo;

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public NodeMachine(String ip2, Long port2, Integer machineType2, String appType, String deployInfo) {
		this.ip=ip2;
		this.port = port2;
		this.machineType = machineType2;
		this.appType = appType;
		this.deployInfo = deployInfo;
	}
	
	public NodeMachine(){
		
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Long getPort() {
		return port;
	}

	public void setPort(Long port) {
		this.port = port;
	}

	public Integer getMachineType() {
		return machineType;
	}

	public void setMachineType(Integer machineType) {
		this.machineType = machineType;
	}

	public String getDeployInfo() {
		return deployInfo;
	}

	public void setDeployInfo(String deployInfo) {
		this.deployInfo = deployInfo;
	}
}
