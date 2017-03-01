package com.dtstack.rdos.engine.entrance.zk;

import com.dtstack.rdos.engine.entrance.configs.NodeConfig;

public class ZkDistributed {

	private NodeConfig nodeConfig;
	
	private static ZkDistributed zkDistributed;
	
	public ZkDistributed(NodeConfig nodeConfig) {
		// TODO Auto-generated constructor stub
		this.nodeConfig  = nodeConfig;
	}

	public static ZkDistributed createZkDistributed(NodeConfig nodeConfig){
		if(zkDistributed == null){
			synchronized(ZkDistributed.class){
				if(zkDistributed == null){
					zkDistributed = new ZkDistributed(nodeConfig);
				}
			}
		}
		return zkDistributed;
	}

	public void release() {
		// TODO Auto-generated method stub
		
	}

}
