package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.enums.StoppedStatus;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
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

    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

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

        if(engineJobCacheDao.getJobById(paramAction.getTaskId()) == null){
            return jobStopStatus(jobClient);
        }

        String jobId = paramAction.getTaskId();
        Integer computeType  = paramAction.getComputeType();
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, paramAction.getEngineType(), jobId);

        jobClient.setCallBack((jobStatus)->{
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, jobStatus);
            updateJobStatus(jobId, computeType, jobStatus);
            deleteJobCache(jobId);
        });

        jobClient.stopJob();
        LOG.info("job:{} is stopping.", paramAction.getTaskId());
        return StoppedStatus.STOPPING;
    }

    private StoppedStatus jobStopStatus(JobClient jobClient){
        if(ComputeType.STREAM == jobClient.getComputeType()){
            RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobClient.getTaskId());
            if (batchJob != null && RdosTaskStatus.isStopped(batchJob.getStatus())){
                LOG.info("job:{} stopped success.", jobClient.getTaskId());
                return StoppedStatus.STOPPED;
            }
        } else if (ComputeType.BATCH == jobClient.getComputeType()) {
            RdosEngineStreamJob streamJob = streamTaskDAO.getRdosTaskByTaskId(jobClient.getTaskId());
            if (streamJob != null && RdosTaskStatus.isStopped(streamJob.getStatus())){
                LOG.info("job:{} stopped success.", jobClient.getTaskId());
                return StoppedStatus.STOPPED;
            }
        }
        LOG.info("job:{} cache is missed, stop interrupt.", jobClient.getTaskId());
        return StoppedStatus.MISSED;
    }

    private void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getType().equals(computeType)) {
            streamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            batchJobDAO.updateJobStatus(jobId, status);
        }
    }

    private void deleteJobCache(String jobId){
        engineJobCacheDao.deleteJob(jobId);
    }
}
