package com.dtstack.rdos.engine.entrance.zk.task;

import java.util.concurrent.LinkedBlockingQueue;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
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
	
	private RdosEngineStreamJobDAO rdosStreamTaskDAO = new RdosEngineStreamJobDAO();
	
	private RdosEngineBatchJobDAO rdosbatchJobDAO = new RdosEngineBatchJobDAO();

	private RdosEngineJobCacheDao rdosEngineJobCacheDao = new RdosEngineJobCacheDao();

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
				String zkTaskId = TaskIdUtil.getZkTaskId(jobClient.getComputeType().getComputeType(),jobClient.getEngineType(),jobClient.getTaskId());
				if(jobClient.getComputeType().getComputeType()==ComputeType.STREAM.getComputeType()){
					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosStreamTaskDAO.updateTaskEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
					}else{//设置为失败
                        rdosStreamTaskDAO.updateTaskStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                        zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId,
                                RdosTaskStatus.FAILED.getStatus());
						rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
					}
					rdosStreamTaskDAO.updateEngineLog(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
				}else if(jobClient.getComputeType().getComputeType()==ComputeType.BATCH.getComputeType()){
					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosbatchJobDAO.updateJobEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
					}else{
					    rdosbatchJobDAO.updateJobStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                        zkDistributed.updateSynchronizedLocalBrokerDataAndCleanNoNeedTask(zkTaskId,
                                RdosTaskStatus.FAILED.getStatus());
						rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
					}
					rdosbatchJobDAO.updateEngineLog(jobClient.getTaskId(), jobClient.getEngineTaskId());
				}

			} catch (Throwable e) {
				logger.error("TaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
			}
		}
	}

}
