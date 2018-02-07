package com.dtstack.rdos.engine.entrance.service;

import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.annotation.Forbidden;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosEngineJobCacheDao;
import com.dtstack.rdos.engine.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.db.dao.RdosPluginInfoDao;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.db.dataobject.RdosPluginInfo;
import com.dtstack.rdos.engine.entrance.enumeration.RequestStart;
import com.dtstack.rdos.engine.entrance.node.JobStopAction;
import com.dtstack.rdos.engine.entrance.node.MasterNode;
import com.dtstack.rdos.engine.entrance.zk.ZkDistributed;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobClientCallBack;
import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobCacheStage;
import com.dtstack.rdos.engine.execution.base.enumeration.EPluginType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.dtstack.rdos.engine.send.HttpSendClient;
import com.dtstack.rdos.engine.util.TaskIdUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 接收http请求
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 */
public class ActionServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    private RdosEngineStreamJobDAO streamTaskDAO = new RdosEngineStreamJobDAO();
    
    private RdosEngineBatchJobDAO batchJobDAO = new RdosEngineBatchJobDAO();
    
    private RdosEngineJobCacheDao engineJobCacheDao = new RdosEngineJobCacheDao();

    private RdosPluginInfoDao pluginInfoDao = new RdosPluginInfoDao();

    private JobStopAction stopAction = new JobStopAction();

    private MasterNode masterNode = MasterNode.getInstance();

    /**
     * 接受来自客户端的请求, 目的是在master节点上组织成一个优先级队列
     * 1：处理 重复发送的问题 2：rdos-web端的发送需要修改为向master节点发送--避免转发
     * @param params
     */
    public void start(Map<String, Object> params){

        try{
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            checkParam(paramAction);

            boolean canAccepted = receiveStartJob(paramAction);

            //判断当前节点是不是master
            if(zkDistributed.localIsMaster()){

                //直接提交到本地master的优先级队列,会对重复数据做校验
                JobClient jobClient = new JobClient(paramAction);
                masterNode.addStartJob(jobClient);
                return;
            }

            if(!canAccepted){
                return;
            }

            //转发送到master节点
            String masterAddr = zkDistributed.isHaveMaster();
            if(masterAddr == null){
                //如果遇到master 地址为null --- 直接将job 缓存到cache.
                addJobCache(paramAction.getTaskId(), paramAction.getEngineType(), paramAction.getComputeType(),
                        EJobCacheStage.IN_PRIORITY_QUEUE.getStage(), paramAction.toString());
                logger.error("---------serious error can't get master address-------");
                throw new RdosException(ErrorCode.NO_MASTER_NODE);
            }

            paramAction.setRequestStart(RequestStart.NODE.getStart());
            HttpSendClient.actionStart(masterAddr, paramAction);
        }catch (Exception e){
            logger.info("", e);
        }
    }

    /**
     * 执行从master上下发的任务
     * 不需要判断等待队列是否满了，由master节点主动判断
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
            if(!checkSubmitted(paramAction)){
                result.put("send", true);
                return result;
            }

            jobId = paramAction.getTaskId();
            computeType = paramAction.getComputeType();

            String zkTaskId = TaskIdUtil.getZkTaskId(paramAction.getComputeType(), paramAction.getEngineType(), paramAction.getTaskId());
            zkDistributed.updateJobZKStatus(zkTaskId, RdosTaskStatus.ENGINEDISTRIBUTE.getStatus());
            updateJobStatus(jobId, computeType, RdosTaskStatus.ENGINEDISTRIBUTE.getStatus());
            updateJobClientPluginInfo(jobId, computeType, paramAction.getPluginInfo());

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
                    zkDistributed.updateJobZKStatus(zkTaskId, jobStatus);
                    updateJobStatus(finalJobId, finalComputeType, jobStatus);
                }
            });

            addJobCache(jobId, paramAction.getEngineType(), computeType, EJobCacheStage.IN_SUBMIT_QUEUE.getStage(), paramAction.toString());
            zkDistributed.updateJobZKStatus(zkTaskId,RdosTaskStatus.WAITENGINE.getStatus());
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

        if(!params.containsKey("jobs")){
            logger.info("invalid param:" + params);
            return ;
        }

        Object paramsObj = params.get("jobs");
        if(!(paramsObj instanceof List)){
            logger.info("invalid param:" + params);
            return;
        }

        List<Map<String, Object>> paramList = (List<Map<String, Object>>) paramsObj;
        if(!zkDistributed.localIsMaster()){
            String masterAddr = zkDistributed.isHaveMaster();

            if(masterAddr == null){
                //如果遇到master 地址为null
                logger.error("---------serious error can't get master address-------");
                throw new RdosException(ErrorCode.NO_MASTER_NODE);
            }

            //转发给master
            HttpSendClient.actionStopJob(masterAddr, params);
            return;
        }

        for(Map<String, Object> param : paramList){
            ParamAction paramAction = PublicUtil.mapToObject(param, ParamAction.class);
            checkParam(paramAction);
            fillJobClientEngineId(paramAction);
            masterNode.addStopJob(paramAction);
        }
    }

    public void masterSendStop(Map<String, Object> params) throws Exception {
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        stopAction.stopJob(paramAction);
        logger.info("stop job:{} success." + paramAction.getTaskId());
    }

    private void checkParam(ParamAction paramAction) throws Exception{

        if(StringUtils.isBlank(paramAction.getTaskId())){
           throw new RdosException("param taskId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if(paramAction.getComputeType() == null){
            throw new RdosException("param computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if(paramAction.getEngineType() == null){
            throw new RdosException("param engineType is not allow null", ErrorCode.INVALID_PARAMETERS);
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
     * 处理从客户的发送过来的任务
     * 修改任务状态为已经接收
     * @param paramAction
     * @return
     */
    public boolean receiveStartJob(ParamAction paramAction){
        boolean result;
        String jobId = paramAction.getTaskId();
        Integer computerType = paramAction.getComputeType();

        //当前任务已经存在在engine里面了
        //TODO 基于不允许相同任务同时在engine上运行---考虑将cache的清理放在任务结束的时候(停止，取消，完成)
        if(engineJobCacheDao.getJobById(jobId) != null){
            return false;
        }

        if (ComputeType.STREAM.getType().equals(computerType)) {
            RdosEngineStreamJob rdosEngineStreamJob = streamTaskDAO.getRdosTaskByTaskId(jobId);
            if(rdosEngineStreamJob == null){
                rdosEngineStreamJob = new RdosEngineStreamJob();
                rdosEngineStreamJob.setTaskId(jobId);
                rdosEngineStreamJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus().byteValue());
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
                rdosEngineBatchJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus().byteValue());
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

    public void updateJobClientPluginInfo(String jobId, Integer computeType, String pluginInfoStr){
        Long refPluginInfoId = -1L;

        //请求不带插件的连接信息的话则默认为使用本地默认的集群配置---pluginInfoId = -1;
        if(!Strings.isNullOrEmpty(pluginInfoStr)){
            RdosPluginInfo pluginInfo = pluginInfoDao.getByPluginInfo(pluginInfoStr);
            if(pluginInfo == null){
                refPluginInfoId = pluginInfoDao.replaceInto(pluginInfoStr, EPluginType.DYNAMIC.getType());
            }else{
                refPluginInfoId = pluginInfo.getId();
            }
        }

        //更新任务ref的pluginInfo
        if(ComputeType.STREAM.getType().equals(computeType)){
            streamTaskDAO.updateTaskPluginId(jobId, refPluginInfoId);
        } else{
            batchJobDAO.updateJobPluginId(jobId, refPluginInfoId);
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
     * 根据taskId 补齐 engineTaskId
     * @param paramAction
     */
    public void fillJobClientEngineId(ParamAction paramAction){
        Integer computeType = paramAction.getComputeType();
        String jobId = paramAction.getTaskId();

        if(paramAction.getEngineTaskId() == null){
            //从数据库补齐数据
            if(ComputeType.STREAM.getType().equals(computeType)){
                RdosEngineStreamJob streamJob = streamTaskDAO.getRdosTaskByTaskId(jobId);
                if(streamJob != null){
                    paramAction.setEngineTaskId(streamJob.getEngineTaskId());
                }
            }

            if(ComputeType.BATCH.getType().equals(computeType)){
                RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
                if(batchJob != null){
                    paramAction.setEngineTaskId(batchJob.getEngineJobId());
                }
            }
        }

    }
}
