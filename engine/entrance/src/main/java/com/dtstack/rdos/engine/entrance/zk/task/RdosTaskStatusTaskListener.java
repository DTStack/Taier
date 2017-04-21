package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.dtstack.rdos.engine.entrance.db.dao.RdosServerLogDao;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.entrance.db.dao.RdosTaskDAO;
import com.dtstack.rdos.engine.entrance.db.dataobject.RdosTask;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;

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
	
	private static long listener = 2000;
	
	private RdosTaskDAO rdosTaskDAO = new RdosTaskDAO();

	private RdosServerLogDao rdosServerLogDao = new RdosServerLogDao();
	
	private Map<String,BrokerDataNode> brokerDatas = zkDistributed.getMemTaskStatus();
	
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
					rdosTaskDAO.updateTaskEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
				}

				//存储执行日志
				rdosServerLogDao.insertLog(jobClient.getTaskId(), jobClient.getEngineTaskId(),
						jobClient.getActionLogId(), jobClient.getJobResult().getJsonStr());

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
			  Set<Map.Entry<String,Byte>> entrys = brokerDatas.get(zkDistributed.getLocalAddress()).getMetas().entrySet();
          	  for(Map.Entry<String,Byte> entry:entrys){
          		  if(!RdosTaskStatus.needClean(entry.getValue())){
              		  String taskId = entry.getKey();
              		  RdosTask rdosTask = rdosTaskDAO.getRdosTaskByTaskId(taskId);
              		  if(rdosTask!=null){
              			  String engineTaskid = rdosTask.getEngineTaskId();
              			  int engineTypeVal = rdosTask.getEngineType();
                          EngineType engineType = EngineType.getEngineType(engineTypeVal);
                  		  RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineType, engineTaskid);
                  		  if(rdosTaskStatus!=null){
                  			  Integer status = rdosTaskStatus.getStatus();
                      		  zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(taskId,status);
                      		  rdosTaskDAO.updateTaskEngineIdAndStatus(taskId,engineTaskid,status);
                  		  } 
              		  }
          		  }
          	  }
		}
	}
}
