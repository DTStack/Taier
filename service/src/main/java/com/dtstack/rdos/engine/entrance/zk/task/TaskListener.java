package com.dtstack.rdos.engine.entrance.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.task.RestartDealer;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

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
	
	private LinkedBlockingQueue<JobClient> queue;
	
	private RdosEngineStreamJobDAO rdosStreamTaskDAO = new RdosEngineStreamJobDAO();
	
	private RdosEngineBatchJobDAO rdosbatchJobDAO = new RdosEngineBatchJobDAO();

	private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();

	private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

	public TaskListener(){
		queue = JobSubmitExecutor.getInstance().getQueueForTaskListener();
	}

	@Override
	public void run() {
		while(true){
			try {
				JobClient jobClient  = queue.take();
                if(RestartDealer.getInstance().checkAndRestartForSubmitResult(jobClient)){
                    continue;
                }

				logger.warn("{}:{} addTaskIdToEngineTaskId...", jobClient.getTaskId(), jobClient.getEngineTaskId());
				//存储执行日志
				String zkTaskId = TaskIdUtil.getZkTaskId(jobClient.getComputeType().getType(), jobClient.getEngineType(), jobClient.getTaskId());

				if(ComputeType.STREAM.getType().equals(jobClient.getComputeType().getType())){

					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosStreamTaskDAO.updateTaskEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
					}else{//设置为失败
                        rdosStreamTaskDAO.updateTaskStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                        zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId,
                                RdosTaskStatus.FAILED.getStatus());
					}

					rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
					rdosStreamTaskDAO.updateSubmitLog(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());

				}else if(ComputeType.BATCH.getType().equals(jobClient.getComputeType().getType())){

					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						rdosbatchJobDAO.updateJobEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId());
						rdosbatchJobDAO.updateSubmitLog(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
					}else{
					    rdosbatchJobDAO.submitFail(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus(), jobClient.getJobResult().getJsonStr());
                        zkDistributed.updateSyncLocalBrokerDataAndCleanNoNeedTask(zkTaskId,
                                RdosTaskStatus.FAILED.getStatus());
					}

                    rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
				}

			} catch (Throwable e) {
				logger.error("TaskListener run error:{}", ExceptionUtil.getErrorMessage(e));
			}
        }
	}

}
