package com.dtstack.rdos.engine.entrance.zk.data;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
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
	
	public static BrokerHeartNode initNullBrokerHeartNode(){
		BrokerHeartNode brokerHeartNode = new BrokerHeartNode();
		brokerHeartNode.setAlive(false);
		return brokerHeartNode;
	}
	
	public static void copy(BrokerHeartNode source,BrokerHeartNode target,boolean isCover){
		if(isCover){
			target.setSeq(source.getSeq());
		}else{
	    	if(source.getSeq()!=null){
	    		target.setSeq(source.getSeq()+target.getSeq());
	    	}
		}
    	if(source.getAlive()!=null){
    		target.setAlive(source.getAlive());
    	}
	}
}
