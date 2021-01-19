package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.schedule.common.enums.AppType;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

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
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EngineUniqueSignDao engineUniqueSignDao;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private ElasticsearchService elasticsearchService;


    private static int length = 8;

    private Random random = new Random();

    private ThreadPoolExecutor logTimeOutPool =  new ThreadPoolExecutor(5, 5,
                                          60L,TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
                new CustomThreadFactory("logTimeOutPool"),
                new CustomThreadRunsPolicy("logTimeOutPool", "log"));

    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    public Boolean start(ParamActionExt paramActionExt){

        logger.info("start  actionParam: {}", JSONObject.toJSONString(paramActionExt));

        try{
            checkParam(paramActionExt);
            //taskId唯一去重，并发请求时以数据库taskId主键去重返回false
            boolean canAccepted = receiveStartJob(paramActionExt);
            //会对重复数据做校验
            if(canAccepted){
                JobClient jobClient = new JobClient(paramActionExt);
                jobDealer.addSubmitJob(jobClient);
                return true;
            }
            logger.warn("Job taskId：" + paramActionExt.getTaskId() + " duplicate submissions are not allowed");
        }catch (Exception e){
            logger.error("", e);
            //任务提交出错 需要将状态从提交中 更新为失败 否则一直在提交中
            String taskId = paramActionExt.getTaskId();
            try {
                if (StringUtils.isNotBlank(taskId)) {
                    logger.error("Job taskId：" + taskId + " submit error ", e);
                    ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
                    if (scheduleJob == null && Objects.nonNull(paramActionExt)) {
                        //新job 任务
                        scheduleJob = buildScheduleJob(paramActionExt);
                        scheduleJob.setStatus(RdosTaskStatus.SUBMITFAILD.getStatus());
                        scheduleJob.setLogInfo(GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()));
                        scheduleJobDao.insert(scheduleJob);
                    } else {
                        //直接失败
                        scheduleJobDao.jobFail(taskId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()));
                    }
                }
            } catch (Exception ex) {
                logger.error("", ex);
            }
        }
        return false;
    }

    /**
     * 停止的请求接口
     * @throws Exception
     */
    public Boolean stop(List<String> jobIds) {

        if(CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobIds不能为空");
        }
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.getRdosJobByJobIds(jobIds));
        jobStopDealer.addStopJobs(jobs);
        return true;
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

    /**
     * 处理从客户的发送过来的任务，会插入到engine_batch/stream_job 表
     * 修改任务状态为 ENGINEACCEPTED, 没有更新的逻辑
     *
     * @param paramActionExt
     * @return
     */
    private boolean receiveStartJob(ParamActionExt paramActionExt){
        boolean result = false;
        String jobId = paramActionExt.getTaskId();
        Integer computerType = paramActionExt.getComputeType();

        //当前任务已经存在在engine里面了
        //不允许相同任务同时在engine上运行---考虑将cache的清理放在任务结束的时候(停止，取消，完成)
        if(engineJobCacheDao.getOne(jobId) != null){
            return result;
        }
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
            if(scheduleJob == null){
                scheduleJob = buildScheduleJob(paramActionExt);
                scheduleJobDao.insert(scheduleJob);
                result = true;
            }else{
                result = RdosTaskStatus.canStart(scheduleJob.getStatus());
                if (result && ComputeType.BATCH.getType().equals(computerType)) {
                    engineJobRetryDao.removeByJobId(jobId);
                }
                if(result && !RdosTaskStatus.ENGINEACCEPTED.getStatus().equals(scheduleJob.getStatus()) ){
                    scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
                    scheduleJob.setAppType(paramActionExt.getAppType());
                    scheduleJob.setDtuicTenantId(paramActionExt.getDtuicTenantId());
                    if (AppType.STREAM.getType() == paramActionExt.getAppType()) {
                        scheduleJob.setRetryNum(0);
                    }
                    scheduleJobDao.update(scheduleJob);
                    logger.info("jobId:{} update job status:{}.", scheduleJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus());
                }
            }
            if (result && ComputeType.BATCH.getType().equals(computerType)){
                engineJobCheckpointDao.deleteByTaskId(jobId);
            }
        } catch (Exception e){
            logger.error("{}", e);
        }
        return result;
    }

    private ScheduleJob buildScheduleJob(ParamActionExt paramActionExt) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(paramActionExt.getTaskId());
        scheduleJob.setJobName(getOrDefault(paramActionExt.getName(),""));
        scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJob.setComputeType(paramActionExt.getComputeType());

        scheduleJob.setTenantId(paramActionExt.getTenantId());
        scheduleJob.setProjectId(getOrDefault(paramActionExt.getProjectId(), -1L));
        //dtuicTenantId() 取 tenantId字段
        scheduleJob.setDtuicTenantId(getOrDefault(paramActionExt.getTenantId(), -1L));
        scheduleJob.setAppType(getOrDefault(paramActionExt.getAppType(), 0));
        scheduleJob.setJobKey(getOrDefault(paramActionExt.getJobKey(), String.format("%s%s%s", "tempJob", paramActionExt.getTaskId(), new DateTime().toString("yyyyMMdd") )));
        scheduleJob.setTaskId(getOrDefault(paramActionExt.getTaskSourceId(), -1L));
        scheduleJob.setCreateUserId(getOrDefault(paramActionExt.getCreateUserId(), -1L));

        scheduleJob.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
        scheduleJob.setIsRestart(getOrDefault(paramActionExt.getIsRestart(), 0));
        scheduleJob.setBusinessDate(getOrDefault(paramActionExt.getBusinessDate(), ""));
        scheduleJob.setCycTime(getOrDefault(paramActionExt.getCycTime(), ""));
        scheduleJob.setDependencyType(getOrDefault(paramActionExt.getDependencyType(), 0));
        scheduleJob.setFlowJobId(getOrDefault(paramActionExt.getFlowJobId(), "0"));
        scheduleJob.setTaskType(getOrDefault(paramActionExt.getTaskType(), -2));
        scheduleJob.setMaxRetryNum(getOrDefault(paramActionExt.getMaxRetryNum(), 0));
        scheduleJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleJob.setVersionId(getOrDefault(paramActionExt.getVersionId(), 0));
        scheduleJob.setComputeType(getOrDefault(paramActionExt.getComputeType(), 1));
        scheduleJob.setPeriodType(paramActionExt.getPeriodType());
        return scheduleJob;
    }

    private <T> T getOrDefault(T value, T defaultValue){
        return value != null? value : defaultValue;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Integer status( String jobId) throws Exception {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
        	return scheduleJob.getStatus();
        }
        return null;
    }

    /**
     * 根据jobid 和 计算类型，查询job的状态
     */
    public Map<String, Integer> statusByJobIds( List<String> jobIds) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobIds  is not allow empty", ErrorCode.INVALID_PARAMETERS);
        }
        Map<String,Integer> result = null;
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getRdosJobByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
        	result = new HashMap<>(scheduleJobs.size());
        	for (ScheduleJob scheduleJob : scheduleJobs){
        		result.put(scheduleJob.getJobId(), scheduleJob.getStatus());
        	}
        }
        return result;
    }

    /**
     * 根据jobid 和 计算类型，查询job开始运行的时间
     * return 毫秒级时间戳
     */
    public Long startTime( String jobId ) throws Exception {

        if (StringUtils.isBlank(jobId) ){
            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        Date startTime = null;
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
        	startTime = scheduleJob.getExecStartTime();
        }
        if (startTime!=null){
            return startTime.getTime();
        }
        return null;
    }

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    public ActionLogVO log( String jobId, Integer computeType) throws Exception {

        if (StringUtils.isBlank(jobId)||computeType==null){
            throw new RdosDefineException("jobId or computeType is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ActionLogVO vo = new ActionLogVO();
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
            vo.setLogInfo(scheduleJob.getLogInfo());
        	String engineLog = scheduleJob.getEngineLog();
            if (StringUtils.isBlank(engineLog)) {
                engineLog = CompletableFuture.supplyAsync(
                        () ->
                        jobDealer.getAndUpdateEngineLog(jobId, scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getDtuicTenantId()),
                        logTimeOutPool
                ).get(environmentContext.getLogTimeout(), TimeUnit.SECONDS);
                if (engineLog == null) {
                    engineLog = "";
                }
            }
            vo.setEngineLog(engineLog);
        }
        return vo;
    }

    /**
     * 根据jobid 从es中获取日志
     */
    public String logFromEs(String jobId) throws Exception {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        String engineLog = "";
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
            engineLog = elasticsearchService.searchWithJobId("taskId", jobId);
        }
        return engineLog;
    }



    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public List<ActionRetryLogVO> retryLog( String jobId) throws Exception {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        List<ActionRetryLogVO> logs = new ArrayList<>(5);
        List<EngineJobRetry> batchJobRetrys = engineJobRetryDao.listJobRetryByJobId(jobId);
        if (CollectionUtils.isNotEmpty(batchJobRetrys)) {
            batchJobRetrys.forEach(jobRetry->{
                ActionRetryLogVO vo = new ActionRetryLogVO();
                vo.setRetryNum(jobRetry.getRetryNum());
                vo.setLogInfo(jobRetry.getLogInfo());
                vo.setRetryTaskParams(jobRetry.getRetryTaskParams());
                logs.add(vo);
            });
        }
        return logs;
    }

    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public ActionRetryLogVO retryLogDetail( String jobId, Integer retryNum) throws Exception {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        if (retryNum == null || retryNum <= 0) {
            retryNum = 1;
        }
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        //数组库中存储的retryNum为0开始的索引位置
        EngineJobRetry jobRetry = engineJobRetryDao.getJobRetryByJobId(jobId, retryNum - 1);
        ActionRetryLogVO vo = new ActionRetryLogVO();
        if (jobRetry != null) {
            vo.setRetryNum(jobRetry.getRetryNum());
            vo.setLogInfo(jobRetry.getLogInfo());
            String engineLog = jobRetry.getEngineLog();
            if (StringUtils.isBlank(jobRetry.getEngineLog())){
                engineLog = jobDealer.getAndUpdateEngineLog(jobId, jobRetry.getEngineJobId(), jobRetry.getApplicationId(), scheduleJob.getDtuicTenantId());
                if (engineLog != null){
                    logger.info("engineJobRetryDao.updateEngineLog id:{}, jobId:{}, engineLog:{}", jobRetry.getId(), jobRetry.getJobId(), engineLog);
                    engineJobRetryDao.updateEngineLog(jobRetry.getId(), engineLog);
                } else {
                    engineLog = "";
                }
            }
            vo.setEngineLog(engineLog);
            vo.setRetryTaskParams(jobRetry.getRetryTaskParams());

        }
        return vo;
    }

    /**
     * 根据jobids 和 计算类型，查询job
     */
    public List<ActionJobEntityVO> entitys( List<String> jobIds) throws Exception {

        if (CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<ActionJobEntityVO> result = null;
        List<ScheduleJob> scheduleJobs = scheduleJobDao.getRdosJobByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
        	result = new ArrayList<>(scheduleJobs.size());
        	for (ScheduleJob scheduleJob:scheduleJobs){
                ActionJobEntityVO vo = new ActionJobEntityVO();
                vo.setJobId(scheduleJob.getJobId());
                vo.setStatus(scheduleJob.getStatus());
                vo.setExecStartTime(scheduleJob.getExecStartTime());
                vo.setLogInfo(scheduleJob.getLogInfo());
                vo.setEngineLog(scheduleJob.getEngineLog());
                vo.setEngineJobId(scheduleJob.getEngineJobId());
                vo.setApplicationId(scheduleJob.getApplicationId());
        		result.add(vo);
        	}
        }
        return result;
    }


    /**
     * 根据jobid 和 计算类型，查询container 信息
     */
    public List<String> containerInfos(ParamAction paramAction) throws Exception {
        checkParam(paramAction);
        //从数据库补齐数据
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(paramAction.getTaskId());
        if(scheduleJob != null){
            paramAction.setEngineTaskId(scheduleJob.getEngineJobId());
            paramAction.setApplicationId(scheduleJob.getApplicationId());
            JobClient jobClient = new JobClient(paramAction);
            return workerOperator.containerInfos(jobClient);
        }
        return null;
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
                StringBuilder sb =new StringBuilder();
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
    public String resetTaskStatus( String jobId){

        //check jobstatus can reset
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        Preconditions.checkNotNull(scheduleJob, "not exists job with id " + jobId);
        Integer currStatus = scheduleJob.getStatus();

        if(!RdosTaskStatus.canReset(currStatus)){
            logger.error("jobId:{} can not update status current status is :{} ", jobId, currStatus);
            throw new RdosDefineException(String.format(" taskId(%s) can't reset status, current status(%d)", jobId, currStatus));
        }

        //do reset status
        scheduleJobDao.updateJobStatusAndPhaseStatus(jobId, RdosTaskStatus.UNSUBMIT.getStatus(), JobPhaseStatus.CREATE.getCode());
        logger.info("jobId:{} update job status:{}.", jobId, RdosTaskStatus.UNSUBMIT.getStatus());
        return jobId;
    }

    /**
     * task 工程使用
     */
    public List<ActionJobStatusVO> listJobStatus( Long time) {
        if (time == null || time == 0L) {
            throw new RuntimeException("time is null");
        }

        List<ScheduleJob> scheduleJobs = scheduleJobDao.listJobStatus(new Timestamp(time), ComputeType.BATCH.getType());
        return toVOS(scheduleJobs);
    }

    private List<ActionJobStatusVO> toVOS(List<ScheduleJob> scheduleJobs){
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            List<ActionJobStatusVO> result = new ArrayList<>(scheduleJobs.size());
            for (ScheduleJob scheduleJob : scheduleJobs) {
                ActionJobStatusVO data = batJobConvertMap(scheduleJob);
                result.add(data);
            }
            return result;
        }
        return Collections.EMPTY_LIST;
    }

    public List<ActionJobStatusVO> listJobStatusByJobIds( List<String> jobIds) throws Exception {
        if (CollectionUtils.isNotEmpty(jobIds)) {
            List<ScheduleJob> scheduleJobs = scheduleJobDao.getRdosJobByJobIds(jobIds);
            return toVOS(scheduleJobs);
        }
        return Collections.EMPTY_LIST;
    }

    private ActionJobStatusVO batJobConvertMap(ScheduleJob scheduleJob){
        ActionJobStatusVO vo =new ActionJobStatusVO();
        vo.setJobId(scheduleJob.getJobId());
        vo.setStatus(scheduleJob.getStatus());
        vo.setExecStartTime(scheduleJob.getExecStartTime() == null ? new Timestamp(0) : scheduleJob.getExecStartTime());
        vo.setExecEndTime(scheduleJob.getExecEndTime() == null ? new Timestamp(0) : scheduleJob.getExecEndTime());
        vo.setExecTime(scheduleJob.getExecTime());
        vo.setRetryNum(scheduleJob.getRetryNum());
        return vo;
    }
}
