package com.dtstack.rdos.engine.entrance.service;

import java.util.Map;
import com.dtstack.rdos.common.annotation.Forbidden;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;

/**
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 *
 * @author sishu.yss
 */
public class ActionServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineStreamJobDAO streamTaskDAO = new RdosEngineStreamJobDAO();
    
    private RdosEngineBatchJobDAO batchJobDAO = new RdosEngineBatchJobDAO();
    
    private RdosEngineJobCacheDao engineJobCacheDao = new RdosEngineJobCacheDao();

    public void start(Map<String, Object> params) throws Exception {
        String ajobId = null;
        Integer acomputeType  = null;
        try {
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            if(recevieJob(paramAction)){
                String jobId = paramAction.getTaskId();
                ajobId = jobId;
                Integer computeType  = paramAction.getComputeType();
                acomputeType = computeType;
                String zkTaskId = TaskIdUtil.getZkTaskId(paramAction.getComputeType(), paramAction.getEngineType(), paramAction.getTaskId());
                boolean isAlreadyInThisNode = zkDistributed.checkIsAlreadyInThisNode(zkTaskId);
                String address = zkDistributed.getExcutionNode();
                if (isAlreadyInThisNode || paramAction.getRequestStart() == RequestStart.NODE.getStart() || zkDistributed.getLocalAddress().equals(address)) {
                    updateJobZookStatus(zkTaskId,RdosTaskStatus.WAITENGINE.getStatus());
                    updateJobStatus(jobId, computeType, RdosTaskStatus.WAITENGINE.getStatus());                	JobClient jobClient = new JobClient(paramAction);
                    jobClient.setJobClientCallBack(new JobClientCallBack() {

                        @Override
                        public void execute(Map<String, ? extends Object> params) {

                            if(!params.containsKey(JOB_STATUS)){
                                return;
                            }

                            int jobStatus = MathUtil.getIntegerVal(params.get(JOB_STATUS));
                            updateJobZookStatus(zkTaskId, jobStatus);
                            updateJobStatus(jobId, computeType, jobStatus);
                        }
                    });
                    addJobCache(jobId, paramAction.toString());
                    jobClient.submitJob();
                } else {
                    paramAction.setRequestStart(RequestStart.NODE.getStart());
                    HttpSendClient.actionStart(address, paramAction);
                }
            }
        } catch (Throwable e) {
            //提交失败,修改对应的提交jobid为提交失败
            logger.error("", e);
            if (ajobId != null) {
                updateJobStatus(ajobId, acomputeType, RdosTaskStatus.FAILED.getStatus());
            }
        }
    }

    @Forbidden
    public void updateJobZookStatus(String taskId,Integer status){
        BrokerDataNode brokerDataNode = BrokerDataNode.initBrokerDataNode();
        brokerDataNode.getMetas().put(taskId, status.byteValue());
        zkDistributed.updateSynchronizedBrokerData(zkDistributed.getLocalAddress(), brokerDataNode, false);
        zkDistributed.updateLocalMemTaskStatus(brokerDataNode);

    }

    public void stop(Map<String, Object> params) throws Exception {
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        String zkTaskId = TaskIdUtil.getZkTaskId(paramAction.getComputeType(), paramAction.getEngineType(), paramAction.getTaskId());
        String jobId = paramAction.getTaskId();
        Integer computeType  = paramAction.getComputeType();
        JobClient jobClient = new JobClient(paramAction);
        jobClient.setJobClientCallBack(new JobClientCallBack(){

			@Override
			public void execute(Map<String, ? extends Object> exeParams) {

                if(!exeParams.containsKey(JOB_STATUS)){
                    return;
                }

                int jobStatus = MathUtil.getIntegerVal(exeParams.get(JOB_STATUS));

                updateJobZookStatus(zkTaskId, jobStatus);
                updateJobStatus(jobId, computeType, jobStatus);
                deleteJobCache(jobId);
			}
        	
        });
        jobClient.stopJob();
    }

    private boolean recevieJob(ParamAction paramAction){
    	boolean result = false;
    	String jobId = paramAction.getTaskId();
    	Integer computerType = paramAction.getComputeType();
        if (ComputeType.STREAM.getComputeType().equals(computerType)) {
        	RdosEngineStreamJob rdosEngineStreamJob = streamTaskDAO.getRdosTaskByTaskId(jobId);
        	if(rdosEngineStreamJob == null){
        		rdosEngineStreamJob = new RdosEngineStreamJob();
        		rdosEngineStreamJob.setTaskId(jobId);
        		rdosEngineStreamJob.setStatus(RdosTaskStatus.SUBMITTING.getStatus().byteValue());
        		streamTaskDAO.insert(rdosEngineStreamJob);
        		result =  true;
        	}else{
        		result = RdosTaskStatus.canSubmitAgain(rdosEngineStreamJob.getStatus());
        	}
        }else{
        	RdosEngineBatchJob rdosEngineBatchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
        	if(rdosEngineBatchJob == null){
        		rdosEngineBatchJob = new RdosEngineBatchJob();
        		rdosEngineBatchJob.setJobId(jobId);
        		rdosEngineBatchJob.setStatus(RdosTaskStatus.SUBMITTING.getStatus().byteValue());
        		batchJobDAO.insert(rdosEngineBatchJob);
        		result =  true;
        	}else{
        		result = RdosTaskStatus.canSubmitAgain(rdosEngineBatchJob.getStatus());
        	}
        }
        return result;
    }
    
    @Forbidden
    public void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getComputeType().equals(computeType)) {
            streamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            batchJobDAO.updateJobStatus(jobId, status);
        }
    }

    @Forbidden
    public void addJobCache(String jobId, String jobInfo){
        engineJobCacheDao.insertJob(jobId, jobInfo);
    }

    public void deleteJobCache(String jobId){
        engineJobCacheDao.deleteJob(jobId);
    }
}
