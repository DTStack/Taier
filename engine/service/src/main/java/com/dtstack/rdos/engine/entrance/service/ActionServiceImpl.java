package com.dtstack.rdos.engine.entrance.service;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.annotation.Forbidden;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.entrance.node.MasterNode;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.entrance.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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

    private MasterNode masterNode = MasterNode.getInstance();

    /**
     * 接受来自客户端的请求, 目的是在master节点上组织成一个优先级队列
     * TODO 1：处理 重复发送的问题 2：rdos-web端的发送需要修改为向master节点发送--避免转发
     * @param params
     */
    public void start(Map<String, Object> params){

        try{
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);

            checkParam(paramAction);

            //判断当前节点是不是master
            if(zkDistributed.localIsMaster()){

                if(!receiveJob(paramAction)){
                    return;
                }

                //1: 直接提交到本地master的优先级队列
                JobClient jobClient = new JobClient(paramAction);
                masterNode.addTask(jobClient);
                return;
            }

            //2: 发送到master节点
            // ----获取master地址
            String masterAddr = zkDistributed.isHaveMaster();

            if(masterAddr == null){
                //TODO 如果遇到master 地址为null 应该如果处理
                logger.error("---------serious error can't get master address-------");
                return;
            }

            //---提交任务
            paramAction.setRequestStart(RequestStart.NODE.getStart());
            HttpSendClient.actionStart(masterAddr, paramAction);
        }catch (Exception e){
            logger.info("", e);
        }

    }

    /**
     * 执行从master上下发的任务
     * 不需要判断等待队列是否满了，由master节点主动判断
     * TODO 处理重复发送的问题
     * @param params
     * @return
     */
    public Map<String, Object> submit(Map<String, Object> params){
        Map<String, Object> result = Maps.newHashMap();
        String jobId = null;
        Integer computeType = null;

        try{

            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            checkParam(paramAction);
            if(checkSubmitted(paramAction)){
                result.put("send", true);
                return result;
            }

            jobId = paramAction.getTaskId();
            computeType = paramAction.getComputeType();

            String zkTaskId = TaskIdUtil.getZkTaskId(paramAction.getComputeType(), paramAction.getEngineType(), paramAction.getTaskId());
            updateJobZKStatus(zkTaskId, RdosTaskStatus.ENGINEDISTRIBUTE.getStatus());
            updateJobStatus(jobId, computeType, RdosTaskStatus.ENGINEDISTRIBUTE.getStatus());

            JobClient jobClient = new JobClient(paramAction);
            String finalJobId = jobId;
            Integer finalComputeType = computeType;
            jobClient.setJobClientCallBack(new JobClientCallBack() {

                @Override
                public void execute(Map<String, ? extends Object> params) {

                    if(!params.containsKey(JOB_STATUS)){
                        return;
                    }

                    int jobStatus = MathUtil.getIntegerVal(params.get(JOB_STATUS));
                    updateJobZKStatus(zkTaskId, jobStatus);
                    updateJobStatus(finalJobId, finalComputeType, jobStatus);
                }
            });

            addJobCache(jobId, paramAction.getEngineType(), computeType, EJobCacheStage.IN_SUBMIT_QUEUE.getStage(), paramAction.toString());
            updateJobZKStatus(zkTaskId,RdosTaskStatus.WAITENGINE.getStatus());
            updateJobStatus(jobId, computeType, RdosTaskStatus.WAITENGINE.getStatus());
            jobClient.submitJob();

            result.put("send", true);
            return result;

        }catch (Exception e){
            //提交失败,修改对应的提交jobid为提交失败
            logger.error("", e);
            if (jobId != null) {
                updateJobStatus(jobId, computeType, RdosTaskStatus.FAILED.getStatus());
            }

            //也是处理成功的一种
            result.put("send", true);
            return result;
        }
    }

    /**
     * 检查是否可以下发任务
     * @param params
     */
    public Map<String, Object> checkCanDistribute(Map<String, Object> params){
        Map<String, Object> resultMap = Maps.newHashMap();
        String groupName = MathUtil.getString(params.get("groupName"));
        String engineType = MathUtil.getString(params.get("engineType"));

        Boolean canAdd = ExeQueueMgr.getInstance().checkCanAddToWaitQueue(engineType, groupName);
        resultMap.put("result", canAdd);
        return resultMap;
    }

    @Forbidden
    public void updateJobZKStatus(String zkTaskId, Integer status){
        BrokerDataNode brokerDataNode = BrokerDataNode.initBrokerDataNode();
        brokerDataNode.getMetas().put(zkTaskId, status.byteValue());
        zkDistributed.updateSynchronizedBrokerData(zkDistributed.getLocalAddress(), brokerDataNode, false);
        zkDistributed.updateLocalMemTaskStatus(brokerDataNode);

    }

    /**
     * 只允许发到master节点上
     * 1: 在master等待队列中查找
     * 2: 在worker-exe等待队列里面查找
     * 3：在worker-status监听队列里面查找（可以直接在master节点上直接发送消息到对应的引擎）
     *
     * @param params
     * @throws Exception
     */
    public void stop(Map<String, Object> params) throws Exception {

        if(!zkDistributed.localIsMaster()){
            String masterAddr = zkDistributed.isHaveMaster();

            if(masterAddr == null){
                //如果遇到master 地址为null 应该如果处理
                logger.error("---------serious error can't get master address-------");
                return;
            }
        }

        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        checkParam(paramAction);
        String jobId = paramAction.getTaskId();

        //在master等待队列中查找
        if(masterNode.stopTaskIfExists(paramAction.getEngineType(), paramAction.getGroupName(), jobId)){
            logger.info("stop job:{} success." + paramAction.getTaskId());
            return;
        }

        //cache记录被删除说明已经在引擎上执行了,往对应的引擎发送停止任务指令
        if(engineJobCacheDao.getJobById(jobId) == null){
            stopJob(paramAction);
            logger.info("stop job:{} success." + paramAction.getTaskId());
            return;
        }

        //在zk上查找任务所在的worker-address
        Integer computeType  = paramAction.getComputeType();
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, paramAction.getEngineType(), jobId);
        String addr = zkDistributed.getJobLocationAddr(zkTaskId);
        if(addr == null){
            logger.info("can't get info from engine zk for jobId:" + jobId);
            return;
        }

        paramAction.setRequestStart(RequestStart.NODE.getStart());
        HttpSendClient.actionStopJobToWorker(addr, params);
    }

    public void masterSendStop(Map<String, Object> params) throws Exception {
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        stopJob(paramAction);
        logger.info("stop job:{} success." + paramAction.getTaskId());
    }

    private void stopJob(ParamAction paramAction) throws Exception {

        String jobId = paramAction.getTaskId();
        Integer computeType  = paramAction.getComputeType();
        String zkTaskId = TaskIdUtil.getZkTaskId(computeType, paramAction.getEngineType(), jobId);

        JobClient jobClient = new JobClient(paramAction);
        fillJobClientEngineId(jobClient);

        jobClient.setJobClientCallBack(new JobClientCallBack(){

            @Override
            public void execute(Map<String, ? extends Object> exeParams) {

                if(!exeParams.containsKey(JOB_STATUS)){
                    return;
                }

                int jobStatus = MathUtil.getIntegerVal(exeParams.get(JOB_STATUS));

                updateJobZKStatus(zkTaskId, jobStatus);
                updateJobStatus(jobId, computeType, jobStatus);
                deleteJobCache(jobId);
            }

        });

        jobClient.stopJob();
    }


    private void checkParam(ParamAction paramAction) throws Exception{

        if(StringUtils.isBlank(paramAction.getTaskId())){
           throw new RdosException("param taskId is not allow null");
        }

        if(paramAction.getComputeType()==null){
            throw new RdosException("param computeType is not allow null");
        }

        if(paramAction.getEngineType() == null){
            throw new RdosException("param engineType is not allow null");
        }
    }

    private boolean checkSubmitted(ParamAction paramAction){
        boolean result;
        String jobId = paramAction.getTaskId();
        Integer computerType = paramAction.getComputeType();

        if (ComputeType.STREAM.getType().equals(computerType)) {
            RdosEngineStreamJob rdosEngineStreamJob = streamTaskDAO.getRdosTaskByTaskId(jobId);
            if(rdosEngineStreamJob == null){
                logger.error("can't find job from engineStreamJob:" + paramAction);
                return false;
            }

            result = RdosTaskStatus.canSubmitAgain(rdosEngineStreamJob.getStatus());

        }else{
            RdosEngineBatchJob rdosEngineBatchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
            if(rdosEngineBatchJob == null){
                logger.error("can't find job from engineBatchJob:" + paramAction);
                return false;
            }

            result = RdosTaskStatus.canSubmitAgain(rdosEngineBatchJob.getStatus());

        }

        return result;
    }

    /**
     * master节点接收到任务，修改任务状态
     * 同时处理重复提交的问题
     * @param paramAction
     * @return
     */
    private boolean receiveJob(ParamAction paramAction){
    	boolean result;
    	String jobId = paramAction.getTaskId();
    	Integer computerType = paramAction.getComputeType();

        if (ComputeType.STREAM.getType().equals(computerType)) {
        	RdosEngineStreamJob rdosEngineStreamJob = streamTaskDAO.getRdosTaskByTaskId(jobId);
        	if(rdosEngineStreamJob == null){
        		rdosEngineStreamJob = new RdosEngineStreamJob();
        		rdosEngineStreamJob.setTaskId(jobId);
        		rdosEngineStreamJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus().byteValue());
        		streamTaskDAO.insert(rdosEngineStreamJob);
        		result =  true;
        	}else{
        		result = RdosTaskStatus.canStartAgain(rdosEngineStreamJob.getStatus());
        		if(result){
        			streamTaskDAO.updateTaskStatus(rdosEngineStreamJob.getTaskId(), RdosTaskStatus.ENGINEACCEPTED.getStatus().byteValue());
        		}
        	}
        }else{
        	RdosEngineBatchJob rdosEngineBatchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
        	if(rdosEngineBatchJob == null){
        		rdosEngineBatchJob = new RdosEngineBatchJob();
        		rdosEngineBatchJob.setJobId(jobId);
        		rdosEngineBatchJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus().byteValue());
        		batchJobDAO.insert(rdosEngineBatchJob);
        		result =  true;
        	}else{
        		result = RdosTaskStatus.canStartAgain(rdosEngineBatchJob.getStatus());
        		if(result){
        			batchJobDAO.updateJobStatus(rdosEngineBatchJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus().byteValue());
        		}
        	}
        }
        return result;
    }
    
    @Forbidden
    public void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getType().equals(computeType)) {
            streamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            batchJobDAO.updateJobStatus(jobId, status);
        }
    }

    /**
     * master接受到任务的时候也需要将数据缓存
     * @param jobId
     * @param engineType
     * @param computeType
     * @param stage
     * @param jobInfo
     */
    @Forbidden
    public void addJobCache(String jobId, String engineType, Integer computeType, int stage, String jobInfo){
        if(engineJobCacheDao.getJobById(jobId) != null){
            engineJobCacheDao.updateJobStage(jobId, stage);
        }else{
            engineJobCacheDao.insertJob(jobId, engineType, computeType, stage, jobInfo);
        }
    }

    public void deleteJobCache(String jobId){
        engineJobCacheDao.deleteJob(jobId);
    }

    /**
     * 根据taskId 补齐engineTaskId
     * @param jobClient
     */
    public void fillJobClientEngineId(JobClient jobClient){
        ComputeType computeType = jobClient.getComputeType();
        String jobId = jobClient.getTaskId();

        if(jobClient.getEngineTaskId() == null){
            //从数据库补齐数据
            if(ComputeType.STREAM.getType().equals(computeType)){
                RdosEngineStreamJob streamJob = streamTaskDAO.getRdosTaskByTaskId(jobId);
                if(streamJob != null){
                    jobClient.setEngineTaskId(streamJob.getEngineTaskId());
                }
            }

            if(ComputeType.BATCH.getType().equals(computeType)){
                RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
                if(batchJob != null){
                    jobClient.setEngineTaskId(batchJob.getEngineJobId());
                }
            }
        }

    }
}
