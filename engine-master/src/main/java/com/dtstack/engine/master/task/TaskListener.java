package com.dtstack.engine.master.task;

import com.dtstack.engine.common.JobSubmitDealer;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.util.TaskIdUtil;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.cache.ZkLocalCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
public class TaskListener implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(TaskListener.class);

	private LinkedBlockingQueue<JobClient> queue;

	private EngineJobDao rdosbatchJobDAO = new EngineJobDao();

	private EngineJobCacheDao engineJobCacheDao = new EngineJobCacheDao();

	private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

	public TaskListener(){
		queue = JobSubmitDealer.getSubmittedQueue();
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
					engineJobCacheDao.deleteJob(jobClient.getTaskId());
				}
			} catch (Throwable e) {
				logger.error("TaskListener run error:{}", ExceptionUtil.getErrorMessage(e));
			}
        }
	}

}
