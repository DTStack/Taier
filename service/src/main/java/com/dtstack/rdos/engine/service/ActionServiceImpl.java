package com.dtstack.rdos.engine.service;

import com.dtstack.rdos.commom.exception.ErrorCode;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.annotation.Param;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.JobSubmitExecutor;
import com.dtstack.rdos.engine.service.db.dao.*;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJobRetry;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobStopRecord;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJobRetry;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineUniqueSign;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.node.WorkNode;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 接收http请求
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 */
public class ActionServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ActionServiceImpl.class);

    private RdosEngineStreamJobDAO streamJobDAO = new RdosEngineStreamJobDAO();

    private RdosEngineBatchJobDAO batchJobDAO = new RdosEngineBatchJobDAO();

    private RdosEngineJobCacheDAO jobCacheDAO = new RdosEngineJobCacheDAO();

    private RdosEngineUniqueSignDAO uniqueSignDAO = new RdosEngineUniqueSignDAO();

    private RdosEngineStreamJobRetryDAO streamJobRetryDAO = new RdosEngineStreamJobRetryDAO();

    private RdosEngineBatchJobRetryDAO batchJobRetryDAO = new RdosEngineBatchJobRetryDAO();

    private RdosEngineJobStopRecordDAO jobStopRecordDAO = new RdosEngineJobStopRecordDAO();

    private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private WorkNode workNode = WorkNode.getInstance();

    private static int length = 8;

    private static int TASK_STOP_LIMIT = 1000;

    private Random random = new Random();

    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    public Boolean start(Map<String, Object> params){
        try{
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            checkParam(paramAction);
            //taskId唯一去重，并发请求时以数据库taskId主键去重返回false
            boolean canAccepted = receiveStartJob(paramAction);
            //会对重复数据做校验
            if(canAccepted){
                //选择节点间队列负载最小的node，做任务分发
                JobClient jobClient = new JobClient(paramAction);
                workNode.addStartJob(jobClient);
                return true;
            }
            logger.warn("Job taskId：" + paramAction.getTaskId() + " duplicate submissions are not allowed");
        }catch (Exception e){
            logger.error("", e);
        }
        return false;
    }

    /**
     * 节点间 http 交互方法
     * 执行从 work node 上下发的任务
     */
    public Map<String, Object> submit(Map<String, Object> params){
        Map<String, Object> result = Maps.newHashMap();
        result.put("send", true);
        try{
            ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
            checkParam(paramAction);
            if(!checkSubmitted(paramAction)){
                return result;
            }
            JobClient jobClient = new JobClient(paramAction);
            workNode.addSubmitJob(jobClient, true);
        }catch (Exception e){
            logger.error("", e);
            result.put("send", false);
        }
        return result;
    }

    /**
     * master 节点分发的容灾任务
     */
    public void masterSendJobs(Map<String, Object> params) throws Exception {
        try {
            if(!params.containsKey("jobIds")){
                logger.info("invalid param:" + params);
                return;
            }
            Object paramsObj = params.get("jobIds");
            if(!(paramsObj instanceof List)){
                logger.info("invalid param:" + params);
                return;
            }
            List<String> jobIds = (List<String>) paramsObj;
            workNode.masterSendSubmitJob(jobIds);
        }catch (Exception e){
            logger.error("", e);
        }
    }

    /**
     * 只允许发到master节点上
     * 1: 在master等待队列中查找
     * 2: 在worker-exe等待队列里面查找
     * 3：在worker-status监听队列里面查找（可以直接在master节点上直接发送消息到对应的引擎）
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

        if (paramList.size() > TASK_STOP_LIMIT){
            throw new RdosException("please don't stop too many tasks at once, limit:" + TASK_STOP_LIMIT);
        }

        for(Map<String, Object> param : paramList){
            /**
             * 在性能要求较高的接口上尽可能使用java原生方法，性能对比 {@link com.dtstack.rdos.engine.entrance.test.RdosEngineJobStopRecordCompare}
             */
            RdosEngineJobStopRecord jobStopRecord = RdosEngineJobStopRecord.toEntity(param);
            jobStopRecordDAO.insert(jobStopRecord);
        }

    }

    /**
     * 节点间 http 交互方法
     */
    public boolean workSendStop(Map<String, Object> params) throws Exception {
        ParamAction paramAction = PublicUtil.mapToObject(params, ParamAction.class);
        return workNode.workSendStop(paramAction);
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
        String jobId = paramAction.getTaskId();
        Integer computerType = paramAction.getComputeType();
        if (ComputeType.STREAM.getType().equals(computerType)) {
            RdosEngineStreamJob rdosEngineStreamJob = streamJobDAO.getRdosTaskByTaskId(jobId);
            if(rdosEngineStreamJob != null){
                return true;
            }
            logger.error("can't find job from engineStreamJob:" + paramAction);
        }else{
            RdosEngineBatchJob rdosEngineBatchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
            if(rdosEngineBatchJob != null) {
                return true;
            }
            logger.error("can't find job from engineBatchJob:" + paramAction);
        }
        return false;
    }

    /**
     * 处理从客户的发送过来的任务，会插入到engine_batch/stream_job 表
     * 修改任务状态为 ENGINEACCEPTED, 没有更新的逻辑
     *
     * @param paramAction
     * @return
     */
    private boolean receiveStartJob(ParamAction paramAction){
        boolean result = false;
        String jobId = paramAction.getTaskId();
        Integer computerType = paramAction.getComputeType();

        //当前任务已经存在在engine里面了
        //不允许相同任务同时在engine上运行---考虑将cache的清理放在任务结束的时候(停止，取消，完成)
        if(jobCacheDAO.getJobById(jobId) != null){
            return result;
        }
        try {
            if (ComputeType.STREAM.getType().equals(computerType)) {
                RdosEngineStreamJob rdosEngineStreamJob = streamJobDAO.getRdosTaskByTaskId(jobId);
                if(rdosEngineStreamJob == null){
                    rdosEngineStreamJob = new RdosEngineStreamJob();
                    rdosEngineStreamJob.setTaskId(jobId);
                    rdosEngineStreamJob.setTaskName(paramAction.getName());
                    rdosEngineStreamJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus().byteValue());
                    streamJobDAO.insert(rdosEngineStreamJob);
                    result =  true;
                }else{

                    result = RdosTaskStatus.canStartAgain(rdosEngineStreamJob.getStatus());
                    if(result && rdosEngineStreamJob.getStatus().intValue() != RdosTaskStatus.ENGINEACCEPTED.getStatus()){
                        int oldStatus = rdosEngineStreamJob.getStatus().intValue();
                        Integer update = streamJobDAO.updateTaskStatusCompareOld(rdosEngineStreamJob.getTaskId(), RdosTaskStatus.ENGINEACCEPTED.getStatus(), oldStatus, paramAction.getName());
                        if (update==null||update!=1){
                            result = false;
                        }
                    }
                }
            }else{
                RdosEngineBatchJob rdosEngineBatchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
                if(rdosEngineBatchJob == null){
                    rdosEngineBatchJob = new RdosEngineBatchJob();
                    rdosEngineBatchJob.setJobId(jobId);
                    rdosEngineBatchJob.setJobName(paramAction.getName());
                    rdosEngineBatchJob.setSourceType(paramAction.getSourceType());
                    rdosEngineBatchJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus().byteValue());
                    batchJobDAO.insert(rdosEngineBatchJob);
                    result =  true;
                }else{

                    result = RdosTaskStatus.canStartAgain(rdosEngineBatchJob.getStatus());
                    if(result && rdosEngineBatchJob.getStatus().intValue() != RdosTaskStatus.ENGINEACCEPTED.getStatus() ){
                        int oldStatus = rdosEngineBatchJob.getStatus().intValue();
                        Integer update = batchJobDAO.updateTaskStatusCompareOld(rdosEngineBatchJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus(),oldStatus, paramAction.getName());
                        if (update==null||update!=1){
                            result = false;
                        }
                    }
                }
                if (result){
                    rdosStreamTaskCheckpointDAO.deleteByTaskId(jobId);
                }
            }
        } catch (Exception e){
            logger.error("{}",e);
        }
        return result;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Integer status(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId)||computeType==null){
            throw new RdosException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Integer status = null;
        if (ComputeType.STREAM.getType().equals(computeType)) {
            RdosEngineStreamJob streamJob = streamJobDAO.getRdosTaskByTaskId(jobId);
            if (streamJob != null) {
                status = streamJob.getStatus().intValue();
            }
        } else if (ComputeType.BATCH.getType().equals(computeType)) {
            RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
            if (batchJob != null) {
                status = batchJob.getStatus().intValue();
            }
        }
        return status;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Map<String, Integer> statusByJobIds(@Param("jobIds") List<String> jobIds,@Param("computeType") Integer computeType) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)||computeType==null){
            throw new RdosException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Map<String,Integer> result = null;
        if (ComputeType.STREAM.getType().equals(computeType)) {
            List<RdosEngineStreamJob> streamJobs = streamJobDAO.getRdosTaskByTaskIds(jobIds);
            if (CollectionUtils.isNotEmpty(streamJobs)) {
                result = new HashMap<>(streamJobs.size());
                for (RdosEngineStreamJob streamJob:streamJobs){
                    result.put(streamJob.getTaskId(),streamJob.getStatus().intValue());
                }
            }
        } else if (ComputeType.BATCH.getType().equals(computeType)) {
            List<RdosEngineBatchJob> batchJobs = batchJobDAO.getRdosTaskByTaskIds(jobIds);
            if (CollectionUtils.isNotEmpty(batchJobs)) {
                result = new HashMap<>(batchJobs.size());
                for (RdosEngineBatchJob batchJob:batchJobs){
                    result.put(batchJob.getJobId(),batchJob.getStatus().intValue());
                }
            }
        }
        return result;
    }

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    public Long startTime(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId)||computeType==null){
            throw new RdosException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Date startTime = null;
        if (ComputeType.STREAM.getType().equals(computeType)) {
            RdosEngineStreamJob streamJob = streamJobDAO.getRdosTaskByTaskId(jobId);
            if (streamJob != null) {
                startTime = streamJob.getExecStartTime();
            }
        } else if (ComputeType.BATCH.getType().equals(computeType)) {
            RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
            if (batchJob != null) {
                startTime = batchJob.getExecStartTime();
            }
        }
        if (startTime!=null){
            return startTime.getTime();
        }
        return null;
    }

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    public String log(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId)||computeType==null){
            throw new RdosException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Map<String,String> log = new HashMap<>(2);
        if (ComputeType.STREAM.getType().equals(computeType)) {
            RdosEngineStreamJob streamJob = streamJobDAO.getRdosTaskByTaskId(jobId);
            if (streamJob != null) {
                log.put("logInfo",streamJob.getLogInfo());
                log.put("engineLog",streamJob.getEngineLog());
            }
        } else if (ComputeType.BATCH.getType().equals(computeType)) {
            RdosEngineBatchJob batchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
            if (batchJob != null) {
                log.put("logInfo",batchJob.getLogInfo());
                log.put("engineLog",batchJob.getEngineLog());
            }
        }
        return PublicUtil.objToString(log);
    }

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLog(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId) || computeType==null){
            throw new RdosException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<Map<String,String>> logs = new ArrayList<>(5);
        if (ComputeType.STREAM.getType().equals(computeType)) {
            List<RdosEngineStreamJobRetry> streamJobRetrys = streamJobRetryDAO.getJobRetryByTaskId(jobId);
            if (CollectionUtils.isNotEmpty(streamJobRetrys)) {
                streamJobRetrys.forEach(jobRetry->{
                    Map<String,String> log = new HashMap<String,String>(3);
                    log.put("retryNum",jobRetry.getRetryNum().toString());
                    log.put("logInfo",jobRetry.getLogInfo());
                    log.put("engineLog",jobRetry.getEngineLog());
                    logs.add(log);
                });

            }
        } else if (ComputeType.BATCH.getType().equals(computeType)) {
            List<RdosEngineBatchJobRetry> batchJobRetrys = batchJobRetryDAO.getJobRetryByJobId(jobId);
            if (CollectionUtils.isNotEmpty(batchJobRetrys)) {
                batchJobRetrys.forEach(jobRetry->{
                    Map<String,String> log = new HashMap<String,String>(4);
                    log.put("retryNum",jobRetry.getRetryNum().toString());
                    log.put("logInfo",jobRetry.getLogInfo());
                    log.put("engineLog",jobRetry.getEngineLog());
                    log.put("retryTaskParams",jobRetry.getRetryTaskParams());
                    logs.add(log);
                });
            }
        }
        return PublicUtil.objToString(logs);
    }

    /**
     * 根据jobids 和 计算类型，查询job
     */
    public List<Map<String,Object>> entitys(@Param("jobIds") List<String> jobIds,@Param("computeType") Integer computeType) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)||computeType==null){
            throw new RdosException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<Map<String,Object>> result = null;
        if (ComputeType.STREAM.getType().equals(computeType)) {
            List<RdosEngineStreamJob> streamJobs = streamJobDAO.getRdosTaskByTaskIds(jobIds);
            if (CollectionUtils.isNotEmpty(streamJobs)) {
                result = new ArrayList<>(streamJobs.size());
                for (RdosEngineStreamJob streamJob:streamJobs){
                    Map<String,Object> data = new HashMap<>(4);
                    data.put("jobId", streamJob.getTaskId());
                    data.put("status", streamJob.getStatus());
                    data.put("execStartTime", streamJob.getExecStartTime());
                    data.put("logInfo", streamJob.getLogInfo());
                    data.put("engineLog", streamJob.getEngineLog());
                    result.add(data);
                }
            }
        } else if (ComputeType.BATCH.getType().equals(computeType)) {
            List<RdosEngineBatchJob> batchJobs = batchJobDAO.getRdosTaskByTaskIds(jobIds);
            if (CollectionUtils.isNotEmpty(batchJobs)) {
                result = new ArrayList<>(batchJobs.size());
                for (RdosEngineBatchJob batchJob:batchJobs){
                    Map<String,Object> data = new HashMap<>(4);
                    data.put("jobId", batchJob.getJobId());
                    data.put("status", batchJob.getStatus());
                    data.put("execStartTime", batchJob.getExecStartTime());
                    data.put("logInfo", batchJob.getLogInfo());
                    data.put("engineLog", batchJob.getEngineLog());
                    result.add(data);
                }
            }
        }
        return result;
    }


    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    public List<String> containerInfos(Map<String, Object> param) throws Exception {
        ParamAction paramAction = PublicUtil.mapToObject(param, ParamAction.class);
        checkParam(paramAction);
        workNode.fillJobClientEngineId(paramAction);
        JobClient jobClient = new JobClient(paramAction);
        List<String> infos = JobSubmitExecutor.getInstance().containerInfos(jobClient);
        return infos;
    }

    public String generateUniqueSign(){

        String uniqueSign;
        int index = 100;
        while(true){
            try{
                if(index > 100){
                    Thread.sleep(100);
                }
                index = index+1;
                uniqueSign = UUID.randomUUID().toString().replace("-","");
                int len = uniqueSign.length();
                StringBuffer sb =new StringBuffer();
                for(int i=0;i<length;i++){
                    int a = random.nextInt(len) + 1;
                    sb.append(uniqueSign.substring(a-1, a));
                }
                uniqueSign =  sb.toString();
                RdosEngineUniqueSign generateUniqueSign = new RdosEngineUniqueSign();
                generateUniqueSign.setUniqueSign(sb.toString());
                //新增操作
                uniqueSignDAO.generate(generateUniqueSign);
                break;
            }catch(Exception e){
            }
        }
        return uniqueSign;
    }

    /**
     * 重置任务状态为未提交
     * @return
     */
    public String resetTaskStatus(@Param("jobId") String jobId, @Param("computeType") Integer computeType){
        //check jobstatus can reset
        Byte currStatus;

        if(ComputeType.STREAM.getType().equals(computeType)){
            RdosEngineStreamJob rdosEngineStreamJob = streamJobDAO.getRdosTaskByTaskId(jobId);
            Preconditions.checkNotNull(rdosEngineStreamJob, "not exists job with id " + jobId);
            currStatus = rdosEngineStreamJob.getStatus();
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            RdosEngineBatchJob rdosEngineBatchJob = batchJobDAO.getRdosTaskByTaskId(jobId);
            Preconditions.checkNotNull(rdosEngineBatchJob, "not exists job with id " + jobId);
            currStatus = rdosEngineBatchJob.getStatus();
        }else{
            throw new RdosException("not support computeType:" + computeType);
        }

        if(!RdosTaskStatus.canReset(currStatus)){
            throw new RdosException(String.format("computeType(%d) taskId(%s) can't reset status, current status(%d)", computeType, jobId, currStatus.intValue()));
        }

        //do reset status

        if(ComputeType.STREAM.getType().equals(computeType)){
            streamJobDAO.updateTaskEngineIdAndStatus(jobId, null, null, RdosTaskStatus.UNSUBMIT.getStatus());
            streamJobDAO.updateSubmitLog(jobId, "");
            streamJobDAO.updateEngineLog(jobId, "");
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            // TODO
            batchJobDAO.updateJobEngineIdAndStatus(jobId, null, RdosTaskStatus.UNSUBMIT.getStatus(),null);
            batchJobDAO.updateSubmitLog(jobId, "");
            batchJobDAO.updateEngineLog(jobId, "");
            batchJobDAO.resetExecTime(jobId);
        }

        return jobId;
    }
}
