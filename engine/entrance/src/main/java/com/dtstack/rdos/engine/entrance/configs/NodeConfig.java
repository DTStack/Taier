package com.dtstack.rdos.engine.entrance.configs;

public class NodeConfig {
	
	private Integer slots;
	
	private String executionEngineUrl;
	
	private String executionEngineZkAddress;
	
	private String jarTmpDir;
	
	private String localAddress;
	
	private String nodeZkAddress;
	
	public Integer getSlots() {
		return slots;
	}
	public void setSlots(Integer slots) {
		this.slots = slots;
	}
	public String getExecutionEngineUrl() {
		return executionEngineUrl;
	}
	public void setExecutionEngineUrl(String executionEngineUrl) {
		this.executionEngineUrl = executionEngineUrl;
	}
	public String getExecutionEngineZkAddress() {
		return executionEngineZkAddress;
	}
	public void setExecutionEngineZkAddress(String executionEngineZkAddress) {
		this.executionEngineZkAddress = executionEngineZkAddress;
	}
	public String getJarTmpDir() {
		return jarTmpDir;
	}
	public void setJarTmpDir(String jarTmpDir) {
		this.jarTmpDir = jarTmpDir;
	}
	public String getLocalAddress() {
		return localAddress;
	}
	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}
	public String getNodeZkAddress() {
		return nodeZkAddress;
	}
	public void setNodeZkAddress(String nodeZkAddress) {
		this.nodeZkAddress = nodeZkAddress;
	}
}
