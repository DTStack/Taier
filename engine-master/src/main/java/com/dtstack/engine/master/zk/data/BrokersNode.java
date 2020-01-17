package com.dtstack.engine.master.zk.data;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class BrokersNode {

	private String master;

	public String getMaster() {
		return master;
	}

	public void setMaster(String master) {
		this.master = master;
	}
	
	public static BrokersNode initBrokersNode(){
		return new BrokersNode();
	}
}
