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
