package com.dtstack.rdos.engine.entrance.configs;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class NodeConfig {
	
	private Integer slots;
	
	private String engineUrl;
	
	private String zkNamespace;
	
	private String jarTmpDir;
	
	private String localAddress;
	
	private String nodeZkAddress;
	
	public Integer getSlots() {
		return slots;
	}
	public void setSlots(Integer slots) {
		this.slots = slots;
	}
	public String getEngineUrl() {
		return engineUrl;
	}
	public void setEngineUrl(String engineUrl) {
		this.engineUrl = engineUrl;
	}
	public String getZkNamespace() {
		return zkNamespace;
	}
	public void setZkNamespace(String zkNamespace) {
		this.zkNamespace = zkNamespace;
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
