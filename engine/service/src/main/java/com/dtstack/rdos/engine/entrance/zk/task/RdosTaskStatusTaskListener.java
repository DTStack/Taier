package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.dtstack.rdos.engine.db.dao.RdosBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosBatchServerLogDao;
import com.dtstack.rdos.engine.db.dao.RdosStreamServerLogDao;
import com.dtstack.rdos.engine.db.dao.RdosStreamTaskDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosBatchJob;
import com.dtstack.rdos.engine.db.dataobject.RdosStreamTask;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;

import com.dtstack.rdos.engine.util.TaskIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
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
	
	private RdosStreamTaskDAO rdosStreamTaskDAO = new RdosStreamTaskDAO();
	
	private RdosBatchJobDAO rdosbatchJobDAO = new RdosBatchJobDAO();


	private RdosStreamServerLogDao rdosStreamServerLogDAO = new RdosStreamServerLogDao();
	
	private RdosBatchServerLogDao rdosBatchServerLogDAO = new RdosBatchServerLogDao();

	
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
				//存储执行日志
				if(jobClient.getComputeType().getComputeType()==ComputeType.STREAM.getComputeType()){
					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosStreamTaskDAO.updateTaskEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
					}
					rdosStreamServerLogDAO.insertLog(jobClient.getTaskId(), jobClient.getEngineTaskId(),
							jobClient.getActionLogId(), jobClient.getJobResult().getJsonStr());
				}else if(jobClient.getComputeType().getComputeType()==ComputeType.BATCH.getComputeType()){
					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosbatchJobDAO.updateJobEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
					}
					
					rdosBatchServerLogDAO.insertLog(jobClient.getTaskId(), jobClient.getEngineTaskId(),
							jobClient.getActionLogId(), jobClient.getJobResult().getJsonStr());				
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
		  	int index = 0;
		  	while(true){
		  		try{
			  		++index;
			  		Thread.sleep(listener);
			  		if(PublicUtil.count(index, 5))logger.warn("TaskStatusTaskListener start again...");
			  		updateTaskStatus();
				}catch(Exception e){
					logger.error("TaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
				}
		    }
		}
		
		private void updateTaskStatus(){
			  Set<Map.Entry<String,Byte>> entrys = brokerDatas.get(zkDistributed.getLocalAddress()).getMetas().entrySet();
          	  for(Map.Entry<String,Byte> entry:entrys){
          		  if(!RdosTaskStatus.needClean(entry.getValue())){
              		  String taskId = entry.getKey();
					  int computeType = TaskIdUtil.getComputeType(taskId);
					  int engineTypeVal = TaskIdUtil.getEngineType(taskId);
					  if(computeType == ComputeType.STREAM.getComputeType()){
						  RdosStreamTask rdosTask = rdosStreamTaskDAO.getRdosTaskByTaskId(taskId);
						  if(rdosTask!=null){
							  String engineTaskid = rdosTask.getEngineTaskId();
							  if(StringUtils.isNotBlank(engineTaskid)){
								  EngineType engineType = EngineType.getEngineType(engineTypeVal);
								  RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineType, engineTaskid);
								  if(rdosTaskStatus!=null){
									  Integer status = rdosTaskStatus.getStatus();
									  zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(taskId,status);
									  rdosStreamTaskDAO.updateTaskEngineIdAndStatus(taskId,engineTaskid,status);
								  }
							  }
						  }
					  }else if(computeType == ComputeType.BATCH.getComputeType()){
						  RdosBatchJob rdosBatchJob  = rdosbatchJobDAO.getRdosTaskByTaskId(taskId);
						  String engineTaskid = rdosBatchJob.getEngineJobId();
						  if(StringUtils.isNotBlank(engineTaskid)){
							  EngineType engineType = EngineType.getEngineType(engineTypeVal);
							  if(engineType.getVal()==EngineType.Datax.getVal()){
								  zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(taskId,rdosBatchJob.getStatus());
							  }else{
								  RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineType, engineTaskid);
								  if(rdosTaskStatus!=null){
									  Integer status = rdosTaskStatus.getStatus();
									  zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(taskId,status);
									  rdosStreamTaskDAO.updateTaskEngineIdAndStatus(taskId,engineTaskid,status);
								  }
							  }
						  }
					  }
          		  }
          	  }
		 }
	}
}
