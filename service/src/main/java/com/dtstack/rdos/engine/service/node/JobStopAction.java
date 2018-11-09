package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
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

    public void stopJob(ParamAction paramAction) throws Exception {

        JobClient jobClient = new JobClient(paramAction);
        //在work节点等待队列中查找，状态流转时engineaccept和enginedistribute无法停止
        if(workNode.stopTaskIfExists(paramAction.getEngineType(), jobClient.getGroupName(), paramAction.getTaskId(), paramAction.getComputeType())){
            LOG.info("stop job:{} success." + paramAction.getTaskId());
            return;
        }

        if(engineJobCacheDao.getJobById(paramAction.getTaskId()) == null){
            LOG.info("job cache is null, stop job:{} interrupt." + paramAction.getTaskId());
            return;
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
        LOG.info("stop job:{} success." + paramAction.getTaskId());
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
