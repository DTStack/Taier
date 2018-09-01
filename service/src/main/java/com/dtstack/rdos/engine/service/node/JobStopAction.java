package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDAO engineJobCacheDao = new RdosEngineJobCacheDAO();

    private WorkNode workNode;

    public JobStopAction(WorkNode workNode){
        this.workNode = workNode;
    }

    public void stopJob(ParamAction paramAction) throws Exception {

        //在work节点等待队列中查找，状态流转时engineaccept和enginedistribute无法停止
        if(workNode.stopTaskIfExists(paramAction.getEngineType(), paramAction.getGroupName(), paramAction.getTaskId(), paramAction.getComputeType())){
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

        JobClient jobClient = new JobClient(paramAction);
        jobClient.setJobClientCallBack(new JobClientCallBack(){

            @Override
            public void execute(Map<String, ? extends Object> exeParams) {

                if(!exeParams.containsKey(JOB_STATUS)){
                    return;
                }

                int jobStatus = MathUtil.getIntegerVal(exeParams.get(JOB_STATUS));

                zkDistributed.updateJobZKStatus(zkTaskId, jobStatus);
                updateJobStatus(jobId, computeType, jobStatus);
                deleteJobCache(jobId);
            }

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
