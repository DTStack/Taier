package com.dtstack.rdos.engine.entrance.zk.data;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
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
