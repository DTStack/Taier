package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.cache.ShardCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/10
 */
@Component
public class TaskSubmittedDealer implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TaskSubmittedDealer.class);

    private LinkedBlockingQueue<JobClient> queue;

    @Autowired
    private EngineJobDao engineJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private WorkNode workNode;

    @Autowired
    private TaskRestartDealer taskRestartDealer;

    @Autowired
    private BatchJobDao batchJobDao;

    public TaskSubmittedDealer() {
        queue = JobSubmitDealer.getSubmittedQueue();
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobClient jobClient = queue.take();

                if (taskRestartDealer.checkAndRestartForSubmitResult(jobClient)) {
                    logger.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());
                    continue;
                }

                logger.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());

                //存储执行日志
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId())) {
                    JobResult jobResult = jobClient.getJobResult();
                    String appId = jobResult.getData(JobResult.EXT_ID_KEY);
                    engineJobDao.updateJobSubmitSuccess(jobClient.getTaskId(), jobClient.getEngineTaskId(), appId, jobClient.getJobResult().getJsonStr());
                    batchJobDao.updateJobInfoByJobId(jobClient.getTaskId(), EJobCacheStage.SUBMITTED.getStage(), new Timestamp(System.currentTimeMillis()), null, null, null);
                    workNode.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
                    jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITTED.getStatus());
                } else {
                    engineJobDao.jobFail(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus(), jobClient.getJobResult().getJsonStr());
                    batchJobDao.updateJobInfoByJobId(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus(), null,null , null, null);
                    logger.info("jobId:{} update job status:{}, job is finished.", jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                    engineJobCacheDao.delete(jobClient.getTaskId());
                }
            } catch (Throwable e) {
                logger.error("TaskListener run error:{}", e);
            }
        }
    }

}
