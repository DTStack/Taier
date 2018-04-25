package com.dtstack.rdos.engine.entrance.zk.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerHeartNode;



/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月07日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class HeartBeat implements Runnable{

	private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);

	private final static int HEARTBEAT = 1000;
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			int index =0 ;
			while(true){
				++index;
				BrokerHeartNode brokerHeartNode = BrokerHeartNode.initBrokerHeartNode();
				brokerHeartNode.setSeq(1L);
				brokerHeartNode.setAlive(true);
				zkDistributed.updateSynchronizedLocalBrokerHeartNode(zkDistributed.getLocalAddress(),brokerHeartNode,false);
				if(PublicUtil.count(index, 10)){logger.warn("HeartBeat start again...");}
				Thread.sleep(HEARTBEAT);
			}

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			logger.error("Heartbeat fail:{}",ExceptionUtil.getErrorMessage(e));
		}
	}
}
