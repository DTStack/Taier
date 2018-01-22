package com.dtstack.rdos.engine.entrance.node;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.util.TaskIdUtil;

import java.util.Map;

/**
 * Reason:
 * Date: 2018/1/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobStopAction {

    private RdosEngineStreamJobDAO streamTaskDAO = new RdosEngineStreamJobDAO();

    private RdosEngineBatchJobDAO batchJobDAO = new RdosEngineBatchJobDAO();

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineJobCacheDao engineJobCacheDao = new RdosEngineJobCacheDao();

    public JobStopAction(){
    }

    public void stopJob(ParamAction paramAction) throws Exception {

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
