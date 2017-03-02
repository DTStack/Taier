package com.dtstack.rdos.engine.entrance.zk.data;

public class BrokerHeartNode {
	
	private  Long seq;
	
	private  Boolean alive;

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Boolean getAlive() {
		return alive;
	}

	public void setAlive(Boolean alive) {
		this.alive = alive;
	}
	
	public static BrokerHeartNode initBrokerHeartNode(){
		BrokerHeartNode brokerHeartNode = new BrokerHeartNode();
		brokerHeartNode.setAlive(true);
		brokerHeartNode.setSeq(0l);
		return brokerHeartNode;
	}
	
	public static void copy(BrokerHeartNode source,BrokerHeartNode target){
    	if(source.getSeq()!=null){
    		target.setSeq(source.getSeq()+target.getSeq());
    	}
    	if(source.getAlive()!=null){
    		target.setAlive(source.getAlive());
    	}
	}
}
