package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.annotation.Param;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobRetry;
import com.dtstack.engine.domain.EngineJobStopRecord;
import com.dtstack.engine.domain.EngineUniqueSign;
import com.dtstack.engine.domain.EngineJob;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.dao.EngineJobDao;
import com.dtstack.engine.dao.EngineJobRetryDao;
import com.dtstack.engine.dao.EngineJobStopRecordDao;
import com.dtstack.engine.dao.EngineUniqueSignDao;
import com.dtstack.engine.dao.StreamTaskCheckpointDao;
import com.dtstack.engine.master.WorkNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
@Service
public class ActionService {

    private static final Logger logger = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private EngineJobDao engineJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineUniqueSignDao engineUniqueSignDao;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

    @Autowired
    private EngineJobStopRecordDao engineJobStopRecordDao;

    @Autowired
    private StreamTaskCheckpointDao streamTaskCheckpointDao;

    @Autowired
    private WorkNode workNode;

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
                workNode.addSubmitJob(jobClient, true);
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
            throw new RdosDefineException("please don't stop too many tasks at once, limit:" + TASK_STOP_LIMIT);
        }

        List<EngineJobStopRecord> jobStopRecords = new ArrayList<>(paramList.size());
        List<String> jobIds = new ArrayList<>(paramList.size());
        for(Map<String, Object> param : paramList){
            /**
             * 在性能要求较高的接口上尽可能使用java原生方法，性能对比 {@link com.dtstack.engine.dtscript.entrance.test.RdosEngineJobStopRecordCompare}
             */
            EngineJobStopRecord jobStopRecord = EngineJobStopRecord.toEntity(param);
            jobStopRecords.add(jobStopRecord);
            jobIds.add(jobStopRecord.getTaskId());
        }

        List<String> alreadyExistJobIds = engineJobStopRecordDao.listByJobIds(jobIds);
        for (EngineJobStopRecord jobStopRecord : jobStopRecords) {
            if (alreadyExistJobIds.contains(jobStopRecord.getTaskId())) {
                logger.info("jobId:{} ignore insert stop record, because is already exist in table.", jobStopRecord.getTaskId());
                continue;
            }
            engineJobStopRecordDao.insert(jobStopRecord);
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
           throw new RdosDefineException("param taskId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if(paramAction.getComputeType() == null){
            throw new RdosDefineException("param computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        if(paramAction.getEngineType() == null){
            throw new RdosDefineException("param engineType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
    }

    private boolean checkSubmitted(ParamAction paramAction){
        EngineJob rdosEngineBatchJob = engineJobDao.getRdosJobByJobId(paramAction.getTaskId());
        if(rdosEngineBatchJob != null) {
        	return true;
        }
        logger.error("can't find job from engineBatchJob:" + paramAction);
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
        if(engineJobCacheDao.getOne(jobId) != null){
            return result;
        }
        try {
            EngineJob engineJob = engineJobDao.getRdosJobByJobId(jobId);
            if(engineJob == null){
                engineJob = new EngineJob();
                engineJob.setJobId(jobId);
                engineJob.setJobName(paramAction.getName());
                engineJob.setSourceType(paramAction.getSourceType());
                engineJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
                engineJob.setComputeType(computerType);
                engineJobDao.insert(engineJob);
                result =  true;
            }else{
                result = RdosTaskStatus.canStartAgain(engineJob.getStatus());
                if (result && ComputeType.BATCH.getType().equals(computerType)) {
                    engineJobRetryDao.removeByJobId(jobId);
                }

                if(result && !RdosTaskStatus.ENGINEACCEPTED.getStatus().equals(engineJob.getStatus()) ){
                    int oldStatus = engineJob.getStatus();
                    Integer update = engineJobDao.updateTaskStatusCompareOld(engineJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus(),oldStatus, paramAction.getName());
                    if (update==null||update!=1){
                        result = false;
                    }
                }
            }
            if (result && ComputeType.BATCH.getType().equals(computerType)){
                streamTaskCheckpointDao.deleteByTaskId(jobId);
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
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobId);
        if (batchJob != null) {
        	return batchJob.getStatus();
        }
        return null;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Map<String, Integer> statusByJobIds(@Param("jobIds") List<String> jobIds,@Param("computeType") Integer computeType) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)||computeType==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Map<String,Integer> result = null;
        List<EngineJob> batchJobs = engineJobDao.getRdosJobByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(batchJobs)) {
        	result = new HashMap<>(batchJobs.size());
        	for (EngineJob batchJob:batchJobs){
        		result.put(batchJob.getJobId(),batchJob.getStatus());
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
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Date startTime = null;
        EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobId);
        if (batchJob != null) {
        	startTime = batchJob.getExecStartTime();
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
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Map<String,String> log = new HashMap<>(2);
        EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobId);
        if (batchJob != null) {
        	log.put("logInfo",batchJob.getLogInfo());
        	String engineLog = null;
            if (StringUtils.isBlank(batchJob.getEngineLog())) {
                engineLog = workNode.getAndUpdateEngineLog(jobId, batchJob.getEngineJobId(), batchJob.getApplicationId(), batchJob.getPluginInfoId());
                if (engineLog == null) {
                    engineLog = "";
                }
            }
        	log.put("engineLog", engineLog);
        }
        return PublicUtil.objToString(log);
    }

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLog(@Param("jobId") String jobId,@Param("computeType") Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId) || computeType==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<Map<String,String>> logs = new ArrayList<>(5);
        List<EngineJobRetry> batchJobRetrys = engineJobRetryDao.listJobRetryByJobId(jobId);
        if (CollectionUtils.isNotEmpty(batchJobRetrys)) {
            batchJobRetrys.forEach(jobRetry->{
                Map<String,String> log = new HashMap<String,String>(4);
                log.put("retryNum",jobRetry.getRetryNum().toString());
                log.put("logInfo",jobRetry.getLogInfo());
                log.put("retryTaskParams",jobRetry.getRetryTaskParams());
                logs.add(log);
            });
        }
        return PublicUtil.objToString(logs);
    }

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public String retryLogDetail(@Param("jobId") String jobId,@Param("computeType") Integer computeType, @Param("retryNum") Integer retryNum) throws Exception {

        if (StringUtils.isBlank(jobId) || computeType==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        if (retryNum == null || retryNum <= 0) {
            retryNum = 1;
        }

        EngineJob batchJob = engineJobDao.getRdosJobByJobId(jobId);
        //数组库中存储的retryNum为0开始的索引位置
        EngineJobRetry jobRetry = engineJobRetryDao.getJobRetryByJobId(jobId, retryNum - 1);
        Map<String,String> log = new HashMap<String,String>(4);
        if (jobRetry != null) {
            log.put("retryNum",jobRetry.getRetryNum().toString());
            log.put("logInfo",jobRetry.getLogInfo());
            String engineLog = jobRetry.getEngineLog();
            if (StringUtils.isBlank(jobRetry.getEngineLog())){
                engineLog = workNode.getAndUpdateEngineLog(jobId, jobRetry.getEngineJobId(), jobRetry.getApplicationId(), batchJob.getPluginInfoId());
                if (engineLog != null){
                    logger.info("engineJobRetryDao.updateEngineLog id:{}, jobId:{}, engineLog:{}", jobRetry.getId(), jobRetry.getJobId(), engineLog);
                    engineJobRetryDao.updateEngineLog(jobRetry.getId(), engineLog);
                } else {
                    engineLog = "";
                }
            }
            log.put("engineLog", engineLog);
            log.put("retryTaskParams",jobRetry.getRetryTaskParams());
        }
        return PublicUtil.objToString(log);
    }

    /**
     * 根据jobids 和 计算类型，查询job
     */
    public List<Map<String,Object>> entitys(@Param("jobIds") List<String> jobIds,@Param("computeType") Integer computeType) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)||computeType==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<Map<String,Object>> result = null;
        List<EngineJob> batchJobs = engineJobDao.getRdosJobByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(batchJobs)) {
        	result = new ArrayList<>(batchJobs.size());
        	for (EngineJob batchJob:batchJobs){
        		Map<String,Object> data = new HashMap<>();
        		data.put("jobId", batchJob.getJobId());
        		data.put("status", batchJob.getStatus());
        		data.put("execStartTime", batchJob.getExecStartTime());
        		data.put("logInfo", batchJob.getLogInfo());
        		data.put("engineLog", batchJob.getEngineLog());
                data.put("engineJobId", batchJob.getEngineJobId());
                data.put("applicationId", batchJob.getApplicationId());
        		result.add(data);
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
        List<String> infos = jobClient.getContainerInfos();
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
                EngineUniqueSign generateUniqueSign = new EngineUniqueSign();
                generateUniqueSign.setUniqueSign(sb.toString());
                //新增操作
                engineUniqueSignDao.insert(generateUniqueSign);
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
        EngineJob rdosEngineBatchJob = engineJobDao.getRdosJobByJobId(jobId);
        Preconditions.checkNotNull(rdosEngineBatchJob, "not exists job with id " + jobId);
        Integer currStatus = rdosEngineBatchJob.getStatus();

        if(!RdosTaskStatus.canReset(currStatus)){
            throw new RdosDefineException(String.format("computeType(%d) taskId(%s) can't reset status, current status(%d)", computeType, jobId, currStatus.intValue()));
        }

        //do reset status
        engineJobDao.updateJobUnSubmitOrRestart(jobId, RdosTaskStatus.UNSUBMIT.getStatus());
        return jobId;
    }

    /**
     * task 工程使用
     */
    public List<Map<String, Object>> listJobStatus(@Param("time") Long time) {
        if (time == null || time == 0L) {
            throw new RuntimeException("time is null");
        }

        List<EngineJob> batchJobs = engineJobDao.listJobStatus(new Timestamp(time), ComputeType.BATCH.getType());
        if (CollectionUtils.isNotEmpty(batchJobs)) {
            List<Map<String, Object>> result = new ArrayList<>(batchJobs.size());
            for (EngineJob batchJob : batchJobs) {
                Map<String, Object> data = batJobConvertMap(batchJob);
                result.add(data);
            }
            return result;
        }
        return Collections.EMPTY_LIST;
    }


    public List<Map<String, Object>> listJobStatusByJobIds(@Param("jobIds") List<String> jobIds) throws Exception {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            List<EngineJob> batchJobs = engineJobDao.getRdosJobByJobIds(jobIds);
            if (CollectionUtils.isNotEmpty(batchJobs)) {
                List<Map<String, Object>> result = new ArrayList<>(batchJobs.size());
                for (EngineJob batchJob : batchJobs) {
                    Map<String, Object> data = batJobConvertMap(batchJob);
                    result.add(data);
                }
                return result;
            }
        }
        return Collections.EMPTY_LIST;
    }

    private Map<String, Object> batJobConvertMap(EngineJob batchJob){
        Map<String, Object> data = new HashMap<>(6);
        data.put("jobId", batchJob.getJobId());
        data.put("status", batchJob.getStatus());
        data.put("execStartTime", batchJob.getExecStartTime() == null ? 0 : batchJob.getExecStartTime());
        data.put("execEndTime", batchJob.getExecEndTime() == null ? 0 : batchJob.getExecEndTime());
        data.put("execTime", batchJob.getExecTime());
        data.put("retryNum", batchJob.getRetryNum());
        return data;
    }
}
