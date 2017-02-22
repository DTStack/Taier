package com.dtstack.rdos.engine.entrance.db.dataobject;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2016年02月21日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosNodeMachine extends DataObject{
	
	/**
	 * node 主机ip
	 */
	private String ip;
	
	/**
	 * node 端口
	 */
	private Long port;
	
	/**
	 * 0 master 1 slave
	 */
	private Byte machineType;

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

	public Byte getMachineType() {
		return machineType;
	}

	public void setMachineType(Byte machineType) {
		this.machineType = machineType;
	}

}
