package com.dtstack.rdos.engine.service.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.node.WorkNode;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
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

	private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

	public TaskListener(){
		queue = JobSubmitExecutor.getInstance().getQueueForTaskListener();
	}

	@Override
	public void run() {
		while(true){
			try {
				JobClient jobClient  = queue.take();

				if(RestartDealer.getInstance().checkAndRestartForSubmitResult(jobClient)){
					logger.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());
					continue;
				}

				logger.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());

				//存储执行日志
				String zkTaskId = TaskIdUtil.getZkTaskId(jobClient.getComputeType().getType(), jobClient.getEngineType(), jobClient.getTaskId());

				if(ComputeType.STREAM.getType().equals(jobClient.getComputeType().getType())){
					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						JobResult jobResult = jobClient.getJobResult();
						String appId = jobResult.getData(JobResult.EXT_ID_KEY);
						rdosStreamTaskDAO.updateTaskEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId(), appId);
						WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.IN_SUBMIT_QUEUE.getStage());
						jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
						zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.SUBMITTED.getStatus());
					}else{
						//设置为失败
                        rdosStreamTaskDAO.updateTaskStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                        zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.FAILED.getStatus());
						rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
					}
					rdosStreamTaskDAO.updateSubmitLog(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
				}else if(ComputeType.BATCH.getType().equals(jobClient.getComputeType().getType())){

					if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
						JobResult jobResult = jobClient.getJobResult();
						String appId = jobResult.getData(JobResult.EXT_ID_KEY);
						rdosbatchJobDAO.updateJobEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId(),appId);
						rdosbatchJobDAO.updateSubmitLog(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
						WorkNode.getInstance().updateCache(jobClient, EJobCacheStage.IN_SUBMIT_QUEUE.getStage());
						jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
						zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.SUBMITTED.getStatus());
					}else{
					    rdosbatchJobDAO.submitFail(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus(), jobClient.getJobResult().getJsonStr());
						zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.FAILED.getStatus());
                        rdosEngineJobCacheDao.deleteJob(jobClient.getTaskId());
					}
				}
			} catch (Throwable e) {
				logger.error("TaskListener run error:{}", ExceptionUtil.getErrorMessage(e));
			}
        }
	}

}
