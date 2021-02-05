package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.JobGraphUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
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
public class JobSubmittedDealer implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(JobSubmittedDealer.class);

    private LinkedBlockingQueue<JobClient> queue;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private JobRestartDealer jobRestartDealer;

    public JobSubmittedDealer() {
        queue = JobSubmitDealer.getSubmittedQueue();
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobClient jobClient = queue.take();

                if (jobRestartDealer.checkAndRestartForSubmitResult(jobClient)) {
                    LOGGER.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());
                    continue;
                }

                LOGGER.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());

                //存储执行日志
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId())) {
                    JobResult jobResult = jobClient.getJobResult();
                    String appId = jobResult.getData(JobResult.EXT_ID_KEY);
                    String jobGraph = JobGraphUtil.formatJSON(jobClient.getEngineTaskId(), jobResult.getData(JobResult.JOB_GRAPH));
                    scheduleJobDao.updateJobSubmitSuccess(jobClient.getTaskId(), jobClient.getEngineTaskId(), appId, jobClient.getJobResult().getJsonStr(), jobGraph);
                    jobDealer.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
                    jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITTED.getStatus(), (jobId) -> {
                        LOGGER.warn("success submit job to Engine, jobId:{} jobResult:{} but shareManager is not found ...", jobId, jobClient.getJobResult());
                        jobClient.doStatusCallBack(RdosTaskStatus.CANCELED.getStatus());
                    });
                } else {
                    scheduleJobDao.jobFail(jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus(), jobClient.getJobResult().getJsonStr());
                    LOGGER.info("jobId:{} update job status:{}, job is finished.", jobClient.getTaskId(), RdosTaskStatus.FAILED.getStatus());
                    engineJobCacheDao.delete(jobClient.getTaskId());
                }
            } catch (Throwable e) {
                LOGGER.error("TaskListener run error", e);
            }
        }
    }

}
