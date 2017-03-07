package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosTaskStatusTaskListener implements Runnable{
	
	private static Logger looger = LoggerFactory.getLogger(RdosTaskStatusTaskListener.class);
	
	private LinkedBlockingQueue<JobClient> queue = new LinkedBlockingQueue<JobClient>();
	
	private ZkDistributed zkDistributed =ZkDistributed.getZkDistributed();
	
	public RdosTaskStatusTaskListener(){
		JobClient.setQueue(queue);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				JobClient jobClient  = queue.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				looger.error("RdosTaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}
}
