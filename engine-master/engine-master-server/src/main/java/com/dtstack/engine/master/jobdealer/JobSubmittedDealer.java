package com.dtstack.engine.master.jobdealer;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.constrant.JobResultConstant;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.pojo.JobResult;
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
            JobClient jobClient = null;
            try {
                jobClient = queue.take();

                if (jobRestartDealer.checkAndRestartForSubmitResult(jobClient)) {
                    LOGGER.warn("failed submit job restarting, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());
                    continue;
                }

                LOGGER.info("success submit job to Engine, jobId:{} jobResult:{} ...", jobClient.getTaskId(), jobClient.getJobResult());

                //存储执行日志
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId())) {
                    JobResult jobResult = jobClient.getJobResult();
                    String appId = jobResult.getData(JobResult.EXT_ID_KEY);
                    JSONObject jobExtraInfo = jobResult.getExtraInfoJson();
                    jobExtraInfo.put(JobResultConstant.JOB_GRAPH,JobGraphUtil.formatJSON(jobClient.getEngineTaskId(), jobExtraInfo.getString(JobResultConstant.JOB_GRAPH), jobClient.getComputeType()));
                    scheduleJobDao.updateJobSubmitSuccess(jobClient.getTaskId(), jobClient.getEngineTaskId(), appId, jobClient.getJobResult().getJsonStr(), jobExtraInfo.toJSONString());
                    jobDealer.updateCache(jobClient, EJobCacheStage.SUBMITTED.getStage());
                    jobClient.doStatusCallBack(RdosTaskStatus.SUBMITTED.getStatus());
                    JobClient finalJobClient = jobClient;
                    shardCache.updateLocalMemTaskStatus(jobClient.getTaskId(), RdosTaskStatus.SUBMITTED.getStatus(), (jobId) -> {
                        LOGGER.warn("success submit job to Engine, jobId:{} jobResult:{} but shareManager is not found ...", jobId, finalJobClient.getJobResult());
                        finalJobClient.doStatusCallBack(RdosTaskStatus.CANCELED.getStatus());
                    });
                } else {
                    jobClientFail(jobClient.getTaskId(), jobClient.getJobResult().getJsonStr());
                }
            } catch (Throwable e) {
                LOGGER.error("jobId submitted {} jobStatus dealer run error", null == jobClient ? "" : jobClient.getTaskId(), e);
                if (null != jobClient) {
                    jobClientFail(jobClient.getTaskId(), JobResult.createErrorResult(e).getJsonStr());
                }
            }
        }
    }

    private void jobClientFail(String taskId, String info) {
        try {
            scheduleJobDao.jobFail(taskId, RdosTaskStatus.FAILED.getStatus(), info);
            LOGGER.info("jobId:{} update job status:{}, job is finished.", taskId, RdosTaskStatus.FAILED.getStatus());
            engineJobCacheDao.delete(taskId);
        } catch (Exception e) {
            LOGGER.error("jobId:{} update job fail {}  error", taskId, info, e);
        }
    }

}
