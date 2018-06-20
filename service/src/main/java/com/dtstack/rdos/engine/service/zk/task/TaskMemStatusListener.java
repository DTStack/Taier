package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月08日 下午1:16:37
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class TaskMemStatusListener implements Runnable{

	private static long listener = 1000;
	
	private static final Logger logger = LoggerFactory.getLogger(TaskMemStatusListener.class);
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

	
	@Override
	public void run() {
		int index = 0;

		while(true){
			try{
				Thread.sleep(listener);
				++index;
				if(PublicUtil.count(index, 5)){logger.warn("TaskMemStatusListener start again");}
				zkDistributed.initMemTaskStatus();
			}catch(Throwable e){
				logger.error("AllTaskStatusListener error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}
}
