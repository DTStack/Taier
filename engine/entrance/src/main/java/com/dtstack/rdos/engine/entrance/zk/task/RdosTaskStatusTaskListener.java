package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.db.dao.RdosTaskDAO;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.google.common.collect.Maps;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class RdosTaskStatusTaskListener implements Runnable{
	
	private static Logger logger = LoggerFactory.getLogger(RdosTaskStatusTaskListener.class);
	
	private LinkedBlockingQueue<JobClient> queue = new LinkedBlockingQueue<JobClient>();
	
	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
	
	private volatile Map<String,String> taskIdToEngineTaskId = Maps.newConcurrentMap();
	
	private static long listener = 2000;
	
	private RdosTaskDAO rdosTaskDAO = new RdosTaskDAO();
	
	public RdosTaskStatusTaskListener(){
		JobClient.setQueue(queue);
		new Thread(new TaskStatusTaskListener()).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				JobClient jobClient  = queue.take();
				logger.warn("{}:{} addTaskIdToEngineTaskId...",jobClient.getTaskId(),jobClient.getEngineTaskId());
				if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
					taskIdToEngineTaskId.put(jobClient.getTaskId(),jobClient.getEngineTaskId());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("RdosTaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}
	
	private class TaskStatusTaskListener implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				  int index = 0;
				  while(true){
					  ++index;
					  Thread.sleep(listener);
					  if(PublicUtil.count(index, 5))logger.warn("TaskStatusTaskListener start again...");
					  updateTaskStatus();
				  }
			}catch(Exception e){
				logger.error("TaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
		
		private void updateTaskStatus(){
            if(taskIdToEngineTaskId.size() > 0){
          	  Set<Map.Entry<String,String>> entrys = taskIdToEngineTaskId.entrySet();
          	  for(Map.Entry<String,String> entry:entrys){
          		  String taskId = entry.getKey();
          		  String engineTaskId = entry.getValue();
          		  RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTaskId);
          		  if(rdosTaskStatus!=null){
          			  Integer status = rdosTaskStatus.getStatus();
              		  zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(taskId,status);
              		  rdosTaskDAO.updateTaskEngineIdAndStatus(taskId, engineTaskId,status);
          		      if(RdosTaskStatus.needClean(status.byteValue())){
          		    	taskIdToEngineTaskId.remove(taskId);
          		      }
          		  }
          	  }
           }	
		}
	}
}
