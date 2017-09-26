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
public class TaskListener implements Runnable{
	
	private static Logger logger = LoggerFactory.getLogger(TaskListener.class);
	
	private LinkedBlockingQueue<JobClient> queue = new LinkedBlockingQueue<JobClient>();
	
	private RdosStreamTaskDAO rdosStreamTaskDAO = new RdosStreamTaskDAO();
	
	private RdosBatchJobDAO rdosbatchJobDAO = new RdosBatchJobDAO();


	private RdosStreamServerLogDao rdosStreamServerLogDAO = new RdosStreamServerLogDao();
	
	private RdosBatchServerLogDao rdosBatchServerLogDAO = new RdosBatchServerLogDao();

	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

	public TaskListener(){
		JobClient.setQueue(queue);
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
					}else{//设置为失败
                        rdosStreamTaskDAO.updateTaskStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                        zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(jobClient.getTaskId(),
                                RdosTaskStatus.FAILED.getStatus());
					}
					rdosStreamServerLogDAO.insertLog(jobClient.getTaskId(), jobClient.getEngineTaskId(),
							jobClient.getActionLogId(), jobClient.getJobResult().getJsonStr());
				}else if(jobClient.getComputeType().getComputeType()==ComputeType.BATCH.getComputeType()){
					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosbatchJobDAO.updateJobEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
					}else{
					    rdosbatchJobDAO.updateJobStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                        zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(jobClient.getTaskId(),
                                RdosTaskStatus.FAILED.getStatus());
                    }
					
					rdosBatchServerLogDAO.insertLog(jobClient.getTaskId(), jobClient.getEngineTaskId(),
							jobClient.getActionLogId(), jobClient.getJobResult().getJsonStr());				
					}


			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("TaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}

}
