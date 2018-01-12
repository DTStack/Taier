package com.dtstack.rdos.engine.entrance.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerQueueNode;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author sishu.yss
 *
 */
public class OtherListener implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(OtherListener.class);
	
	private final static int listener = 5 * 1000;

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

	public OtherListener(){
	}
	
	@Override
	public void run() {
		try{
			logger.warn("OtherListener start ....");
			while(true){

			    //更新当前节点的queue
                Map<String, BrokerQueueNode> queueNodeMap = zkDistributed.getAllBrokerQueueNode();
                Map<String, Map<String, Integer>> queueInfo = Maps.newHashMap();
                queueNodeMap.forEach( (address, queueNode) -> queueInfo.put(address, queueNode.getGroupQueueInfo()));

                ExeQueueMgr.getInstance().updateZkGroupPriorityInfo(queueInfo);

                //获取所有节点的queue 信息
                Map<String, Integer>  localQueueSet = ExeQueueMgr.getInstance().getZkGroupPriorityInfo();
                String localAddr = zkDistributed.getLocalAddress();
                BrokerQueueNode localQueueNode = new BrokerQueueNode();
                localQueueNode.setGroupQueueInfo(localQueueSet);
                zkDistributed.updateSynchronizedLocalQueueNode(localAddr, localQueueNode);

				Thread.sleep(listener);
			}
		}catch(Throwable e){
			logger.error("OtherListener error:{}",ExceptionUtil.getErrorMessage(e));
		}
	}

}
