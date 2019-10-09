package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.engine.execution.base.enums.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.enums.StoppedStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2018/1/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobStopAction {

    private static final Logger LOG = LoggerFactory.getLogger(JobStopAction.class);


    private RdosEngineStreamJobDAO streamTaskDAO = new RdosEngineStreamJobDAO();

    private RdosEngineBatchJobDAO batchJobDAO = new RdosEngineBatchJobDAO();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private WorkNode workNode;

    public JobStopAction(WorkNode workNode){
        this.workNode = workNode;
    }

    public StoppedStatus stopJob(ParamAction paramAction) throws Exception {

        JobClient jobClient = new JobClient(paramAction);
        //在work节点等待队列中查找，状态流转时engineaccept和enginedistribute无法停止
        if(workNode.stopTaskIfExists(paramAction.getEngineType(), jobClient.getGroupName(), paramAction.getTaskId(), paramAction.getComputeType())){
            LOG.info("job:{} stopped success.", paramAction.getTaskId());
            return StoppedStatus.STOPPED;
        }

        //job如果少会缓存在内存，如果超过 GroupPriorityQueue.QUEUE_SIZE_LIMITED 大小则会存在数据库中
        //如果存在数据库中必须判断jobcache表不为空并stage=1 并且 jobstatus=WAITENGINE
        RdosEngineJobCache jobCache = engineJobCacheDao.getJobById(paramAction.getTaskId());
        if(jobCache == null){
            return jobStopStatus(jobClient);
        } else if (EJobCacheStage.IN_PRIORITY_QUEUE.getStage() == jobCache.getStage()){
            Byte status = getJobStatus(jobClient);
            if (status !=null && RdosTaskStatus.WAITENGINE.getStatus() == status.intValue()){
                //删除
                removeJob(jobClient);
            }
        }

        jobClient.stopJob();
        LOG.info("job:{} is stopping.", paramAction.getTaskId());
        return StoppedStatus.STOPPING;
    }

    private StoppedStatus jobStopStatus(JobClient jobClient){
        Byte status = getJobStatus(jobClient);
        if (status != null && RdosTaskStatus.isStopped(status)){
            LOG.info("job:{} stopped success.", jobClient.getTaskId());
            return StoppedStatus.STOPPED;
        }
        LOG.info("job:{} cache is missed, stop interrupt.", jobClient.getTaskId());
        return StoppedStatus.MISSED;
    }

    private Byte getJobStatus(JobClient jobClient) {
        if(ComputeType.BATCH == jobClient.getComputeType()){
            RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobClient.getTaskId());
            if (batchJob != null) {
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId()) && !jobClient.getEngineTaskId().equals(batchJob.getEngineJobId())) {
                    return RdosTaskStatus.CANCELED.getStatus().byteValue();
                } else {
                    return batchJob.getStatus();
                }
            }
        } else if (ComputeType.STREAM == jobClient.getComputeType()) {
            RdosEngineStreamJob streamJob = streamTaskDAO.getRdosTaskByTaskId(jobClient.getTaskId());
            if (streamJob != null) {
                /**
                 * 停止过程中存在超时情况（就flink perjob而言，cancel job 是一个阻塞调用），
                 * 一旦"取消任务超时"与"任务停止后立即重启"并发时会出现取消上一次任务的情况，并可能出现异常（就flink perjob而言，会出现Job colud not be found 异常）
                 */
                if (StringUtils.isNotBlank(jobClient.getEngineTaskId()) && !jobClient.getEngineTaskId().equals(streamJob.getEngineTaskId())) {
                    return RdosTaskStatus.CANCELED.getStatus().byteValue();
                } else {
                    return streamJob.getStatus();
                }
            }
        }
        return null;
    }

    private void removeJob(JobClient jobClient) {
        engineJobCacheDao.deleteJob(jobClient.getTaskId());
        //修改任务状态
        if(ComputeType.BATCH == jobClient.getComputeType()){
            batchJobDAO.updateJobStatus(jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
        }else if(ComputeType.STREAM == jobClient.getComputeType()){
            streamTaskDAO.updateTaskStatus(jobClient.getTaskId(), RdosTaskStatus.CANCELED.getStatus());
        }
    }

}
