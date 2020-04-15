
package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.common.enums.StoppedStatus;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.cache.ShardCache;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reason:
 * Date: 2018/1/22
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Component
public class JobStopAction {

    private static final Logger LOG = LoggerFactory.getLogger(JobStopAction.class);

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private WorkerOperator workerOperator;

    public StoppedStatus stopJob(JobStopQueue.JobElement jobElement) throws Exception {
        EngineJobCache jobCache = engineJobCacheDao.getOne(jobElement.jobId);
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobElement.jobId);
        if (jobCache == null) {
            if (scheduleJob != null && RdosTaskStatus.isStopped(scheduleJob.getStatus())) {
                LOG.info("jobId:{} stopped success, task status is STOPPED.", jobElement.jobId);
                return StoppedStatus.STOPPED;
            }
            LOG.info("jobId:{} cache is missed, stop interrupt.", jobElement.jobId);
            return StoppedStatus.MISSED;
        } else if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())) {
            removeMemStatusAndJobCache(jobCache.getJobId());
            LOG.info("jobId:{} stopped success, task status is STOPPED.", jobElement.jobId);
            return StoppedStatus.STOPPED;
        } else {
            if (scheduleJob == null) {
                LOG.info("jobId:{} cache is missed, stop interrupt.", jobElement.jobId);
                return StoppedStatus.MISSED;
            }
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
            paramAction.setEngineTaskId(scheduleJob.getEngineJobId());
            paramAction.setApplicationId(scheduleJob.getApplicationId());
            JobClient jobClient = new JobClient(paramAction);

            if (StringUtils.isNotBlank(scheduleJob.getEngineJobId()) && !jobClient.getEngineTaskId().equals(scheduleJob.getEngineJobId())) {
                LOG.info("jobId:{} stopped success, because of [difference engineJobId].", paramAction.getTaskId());
                return StoppedStatus.STOPPED;
            }

            JobResult jobResult = workerOperator.stopJob(jobClient);
            if (jobResult.getCheckRetry()) {
                LOG.info("jobId:{} is retry.", paramAction.getTaskId());
                return StoppedStatus.RETRY;
            } else {
                LOG.info("jobId:{} is stopping.", paramAction.getTaskId());
                return StoppedStatus.STOPPING;
            }
        }

    }

    private void removeMemStatusAndJobCache(String jobId) {
        shardCache.removeIfPresent(jobId);
        engineJobCacheDao.delete(jobId);
        //修改任务状态
        scheduleJobDao.updateJobStatusAndExecTime(jobId, RdosTaskStatus.CANCELED.getStatus());
        LOG.info("jobId:{} update job status:{}, job is finished.", jobId, RdosTaskStatus.CANCELED.getStatus());
    }

}
