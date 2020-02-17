package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.JobSubmitDealer;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.cache.ZkLocalCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class TaskSubmittedDealer implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(TaskSubmittedDealer.class);

	private LinkedBlockingQueue<JobClient> queue;

	@Autowired
	private EngineJobDao engineJobDao;

	@Autowired
	private EngineJobCacheDao engineJobCacheDao;

	@Autowired
	private ZkLocalCache zkLocalCache;

	@Autowired
	private WorkNode workNode;

	public TaskSubmittedDealer(){
		queue = JobSubmitDealer.getSubmittedQueue();
	}

	@Override
	public void run() {
		while(true){
			try {
				JobClient jobClient  = queue.take();

				if(TaskRestartDealer.getInstance().checkAndRestartForSubmitResult(jobClient)){
					logger.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());
					continue;
				}

				logger.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());

				//存储执行日志
				if(StringUtils.isNotBlank(jobClient.getEngineTaskId())){
					JobResult jobResult = jobClient.getJobResult();
					String appId = jobResult.getData(JobResult.EXT_ID_KEY);
					engineJobDao.updateJobEngineId(jobClient.getTaskId(), jobClient.getEngineTaskId(),appId);
					engineJobDao.updateSubmitLog(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
					workNode.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
					jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
					zkLocalCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITTED.getStatus());
				}else{
					engineJobDao.jobFail(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus(), jobClient.getJobResult().getJsonStr());
					zkLocalCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
					engineJobCacheDao.delete(jobClient.getTaskId());
				}
			} catch (Throwable e) {
				logger.error("TaskListener run error:{}", ExceptionUtil.getErrorMessage(e));
			}
        }
	}

}
