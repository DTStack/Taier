package com.dtstack.rdos.engine.entrance.zk.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月08日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class AllTaskStatusListener implements Runnable{

	private static long listener = 1000;
	
	Logger logger = LoggerFactory.getLogger(AllTaskStatusListener.class);
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int index = 0;
		while(true){
			try{
				Thread.sleep(listener);
				++index;
				if(PublicUtil.count(index, 5))logger.warn("AllTaskStatusListener start again");
				zkDistributed.initMemTaskStatus();
			}catch(Exception e){
				logger.error("AllTaskStatusListener error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}
}
