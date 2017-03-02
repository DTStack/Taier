package com.dtstack.rdos.engine.entrance.zk.data;

import java.util.HashMap;
import java.util.Map;

public class BrokerDataNode {
	
	private Map<String,Byte> metas ;

	public Map<String, Byte> getMetas() {
		return metas;
	}

	public void setMetas(Map<String, Byte> metas) {
		this.metas = metas;
	}
	
	public static void copy(BrokerDataNode source,BrokerDataNode target){
    	if(source.getMetas()!=null){
    		target.setMetas(source.getMetas());
    	}
	}
	
	
	public static BrokerDataNode initBrokerDataNode(){
		BrokerDataNode brokerNode = new BrokerDataNode();
		brokerNode.setMetas(new HashMap<String,Byte>());
		return brokerNode;
	}
	
	public static BrokerDataNode initNullBrokerNode(){
		BrokerDataNode brokerNode = new BrokerDataNode();
		return brokerNode;
	}
}
