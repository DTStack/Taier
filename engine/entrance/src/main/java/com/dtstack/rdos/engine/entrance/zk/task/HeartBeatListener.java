package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.db.dao.RdosNodeMachineDAO;
import com.dtstack.rdos.engine.entrance.enumeration.RdosNodeMachineType;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerHeartNode;
import com.google.common.collect.Maps;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月07日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class HeartBeatListener implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(HeartBeatListener.class);
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	private final static int HEATBEATCHECK = 2000;
	
	private final static int EXCEEDCOUNT = 3;
	
	private MasterListener masterListener;
	
	private RdosNodeMachineDAO rdosNodeMachineDAO = new RdosNodeMachineDAO();

	public HeartBeatListener(MasterListener masterListener){
		this.masterListener = masterListener;
	}
	
	public Map<String,BrokerNodeCount> brokerNodeCounts =  Maps.newConcurrentMap();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int index = 0;
			while(true){
				++index;
				if(this.masterListener.isMaster()){
					healthCheck();
					if(PublicUtil.count(index, 5))logger.warn("HeartBeatListener start check again...");
				}
				Thread.sleep(HEATBEATCHECK);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(ExceptionUtil.getErrorMessage(e));
		}
	}
	
	private void healthCheck(){
		List<String> childrens = this.zkDistributed.getBrokersChildren();
		if(childrens!=null){
			for(String node:childrens){
				BrokerHeartNode brokerNode = this.zkDistributed.getBrokerHeartNode(node);
				if(brokerNode!=null&&brokerNode.getAlive()){
					BrokerNodeCount brokerNodeCount = brokerNodeCounts.get(node);
					if(brokerNodeCount==null){
						brokerNodeCount = new BrokerNodeCount(0,brokerNode);
					}
					if(brokerNodeCount.getBrokerHeartNode().getSeq().longValue()==brokerNode.getSeq().longValue()){
						brokerNodeCount.setCount(brokerNodeCount.getCount()+1);
					}else{
						brokerNodeCount.setCount(0);
					}
					if(brokerNodeCount.getCount() > EXCEEDCOUNT){//node died
						this.zkDistributed.disableBrokerHeartNode(node);
					    this.zkDistributed.dataMigration(node);
						this.brokerNodeCounts.remove(node);
					}else{
						brokerNodeCount.setBrokerHeartNode(brokerNode);
						this.brokerNodeCounts.put(node, brokerNodeCount);
					}
				}else{
					this.brokerNodeCounts.remove(node);
				}
			}
		}
	}
	
	static class BrokerNodeCount{
		
		private int count;
		
		private BrokerHeartNode brokerHeartNode;
		
		public BrokerNodeCount(int count,BrokerHeartNode brokerHeartNode){
			this.count = count;
			this.brokerHeartNode  = brokerHeartNode;
		}

		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public BrokerHeartNode getBrokerHeartNode() {
			return brokerHeartNode;
		}
		public void setBrokerHeartNode(BrokerHeartNode brokerHeartNode) {
			this.brokerHeartNode = brokerHeartNode;
		}
	}
}
