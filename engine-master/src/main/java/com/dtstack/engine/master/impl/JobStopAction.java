
package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.common.enums.StoppedStatus;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.cache.ShardCache;
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
    private EngineJobDao engineJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ShardCache shardCache;

    @Autowired
    private WorkerOperator workerOperator;

    public StoppedStatus stopJob(JobStopQueue.JobElement jobElement) throws Exception {
        EngineJobCache jobCache = engineJobCacheDao.getOne(jobElement.jobId);
        EngineJob engineJob = engineJobDao.getRdosJobByJobId(jobElement.jobId);
        if (jobCache == null) {
            if (engineJob != null && RdosTaskStatus.isStopped(engineJob.getStatus())) {
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
            if (engineJob == null) {
                LOG.info("jobId:{} cache is missed, stop interrupt.", jobElement.jobId);
                return StoppedStatus.MISSED;
            }
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobCache.getJobInfo(), ParamAction.class);
            paramAction.setEngineTaskId(engineJob.getEngineJobId());
            paramAction.setApplicationId(engineJob.getApplicationId());
            JobClient jobClient = new JobClient(paramAction);
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
        engineJobDao.updateJobStatusAndExecTime(jobId, RdosTaskStatus.CANCELED.getStatus());
        LOG.info("jobId:{} update job status:{}, job is finished.", jobId, RdosTaskStatus.CANCELED.getStatus());
    }

}
