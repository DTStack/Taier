
package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.common.enums.StoppedStatus;
import com.dtstack.engine.master.WorkNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Reason:
 * Date: 2018/1/22
 * Company: www.dtstack.com
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
    private WorkNode workNode;

    public StoppedStatus stopJob(ParamAction paramAction) throws Exception {

        JobClient jobClient = new JobClient(paramAction);
        //在work节点等待队列中查找，状态流转时engineaccept和enginedistribute无法停止
        if(workNode.stopTaskIfExists(jobClient)){
            LOG.info("jobId:{} stopped success, because of [stopTaskIfExists].", paramAction.getTaskId());
            return StoppedStatus.STOPPED;
        }

        //job数量小会全都缓存在内存，如果超过 GroupPriorityQueue.QUEUE_SIZE_LIMITED 大小则会存在数据库中
        //如果存储在数据库中的job，必须判断jobcache表不为空并stage=1 并且 jobstatus=WAITENGINE
        EngineJobCache jobCache = engineJobCacheDao.getOne(paramAction.getTaskId());
        if(jobCache == null){
            return jobStopStatus(jobClient);
        } else if (EJobCacheStage.unSubmitted().contains(jobCache.getStage())){
            Integer status = getJobStatus(jobClient);
            if (status !=null && RdosTaskStatus.WAITENGINE.getStatus() == status.intValue()){
                //删除
                removeJob(jobClient);
                LOG.info("jobId:{} stopped success, because of [DB & WAITENGINE].", paramAction.getTaskId());
                return StoppedStatus.STOPPED;
            }
        } else if (EJobCacheStage.SUBMITTED.getStage() == jobCache.getStage()) {
            /**
             * 停止过程中存在超时情况（就flink perjob而言，cancel job 是一个阻塞调用），
             * 一旦"取消任务超时并再次触发取消"与"任务停止后立即重启"并发，可能会出现取消上一次任务的情况，并出现异常（就flink perjob而言，会出现Job colud not be found 异常）
             */
            String engineTaskId = getEngineTaskId(jobClient);
            if (StringUtils.isNotBlank(jobClient.getEngineTaskId()) && !jobClient.getEngineTaskId().equals(engineTaskId)) {
                LOG.info("jobId:{} stopped success, because of [difference engineTaskId].", paramAction.getTaskId());
                return StoppedStatus.STOPPED;
            }
        }

        JobResult jobResult = jobClient.stopJob();
        if (jobResult.getCheckRetry()){
            LOG.info("jobId:{} is retry.", paramAction.getTaskId());
            return StoppedStatus.RETRY;
        } else {
            LOG.info("jobId:{} is stopping.", paramAction.getTaskId());
            return StoppedStatus.STOPPING;
        }
    }

    private StoppedStatus jobStopStatus(JobClient jobClient){
        Integer status = getJobStatus(jobClient);
        if (status != null && RdosTaskStatus.isStopped(status)){
            LOG.info("jobId:{} stopped success, task status is STOPPED.", jobClient.getTaskId());
            return StoppedStatus.STOPPED;
        }
        LOG.info("jobId:{} cache is missed, stop interrupt.", jobClient.getTaskId());
        return StoppedStatus.MISSED;
    }

    private Integer getJobStatus(JobClient jobClient) {
    	EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobClient.getTaskId());
    	if (batchJob != null) {
    		return batchJob.getStatus();
    	}
        return null;
    }

    private String getEngineTaskId(JobClient jobClient) {
    	EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobClient.getTaskId());
    	if (batchJob != null) {
    		return batchJob.getEngineJobId();
    	}
        return null;
    }

    private void removeJob(JobClient jobClient) {
        engineJobCacheDao.delete(jobClient.getTaskId());
        engineJobDao.updateJobStatusAndExecTime(jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
        LOG.info("jobId:{} update job status to {}", jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
    }

}
