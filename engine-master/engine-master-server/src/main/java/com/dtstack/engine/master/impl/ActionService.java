/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.dtstack.engine.common.CustomThreadRunsPolicy;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.DtJobIdWorker;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.domain.EngineJobRetry;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskParamShade;
import com.dtstack.engine.mapper.*;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.action.restart.RestartJobRunnable;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.jobdealer.JobDealer;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import com.dtstack.engine.master.server.multiengine.JobStartTriggerBase;
import com.dtstack.engine.master.server.multiengine.factory.MultiEngineFactory;
import com.dtstack.engine.master.server.pipeline.IPipeline;
import com.dtstack.engine.master.server.pipeline.PipelineBuilder;
import com.dtstack.engine.master.server.pipeline.params.UploadParamPipeline;
import com.dtstack.engine.master.server.scheduler.JobRichOperator;
import com.dtstack.engine.master.server.scheduler.parser.ScheduleCron;
import com.dtstack.engine.master.server.scheduler.parser.ScheduleFactory;
import com.dtstack.engine.master.vo.JobLogVO;
import com.dtstack.engine.master.vo.action.ActionJobEntityVO;
import com.dtstack.engine.master.vo.action.ActionLogVO;
import com.dtstack.engine.master.vo.action.ActionRetryLogVO;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.pluginapi.enums.*;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.pojo.ParamAction;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 接收http请求
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 */
@Service
public class ActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;


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
    private JobRichOperator jobRichOperator;

    @Autowired
    private MultiEngineFactory multiEngineFactory;

    @Autowired
    private TaskParamsService taskParamsService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    private final ObjectMapper objMapper = new ObjectMapper();

    private static final String RUN_JOB_NAME = "runJob";
    private static final String RUN_DELIMITER = "_";

    private ThreadPoolExecutor logTimeOutPool =  new ThreadPoolExecutor(5, 5,
                                          60L,TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
                new CustomThreadFactory("logTimeOutPool"),
                new CustomThreadRunsPolicy("logTimeOutPool", "log"));

    private static final PropertyFilter propertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("taskParams") || name.equalsIgnoreCase("sqlText"));

    private DtJobIdWorker jobIdWorker;

    /**
     * 接受来自客户端的请求, 并判断节点队列长度。
     * 如在当前节点,则直接处理任务
     */
    public Boolean start(ParamActionExt paramActionExt){
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("start  actionParam: {}", JSONObject.toJSONString(paramActionExt,propertyFilter));
        }

        try{
            checkParam(paramActionExt);
            //taskId唯一去重，并发请求时以数据库taskId主键去重返回false
            boolean canAccepted = receiveStartJob(paramActionExt);
            //会对重复数据做校验
            if(canAccepted){
                JobClient jobClient = new JobClient(paramActionExt);
                jobClient.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
                jobDealer.addSubmitJob(jobClient);
                return true;
            }
            LOGGER.warn("Job taskId：" + paramActionExt.getTaskId() + " duplicate submissions are not allowed");
        }catch (Exception e){
            LOGGER.error("", e);
            //任务提交出错 需要将状态从提交中 更新为失败 否则一直在提交中
            String taskId = paramActionExt.getTaskId();
            try {
                if (StringUtils.isNotBlank(taskId)) {
                    LOGGER.error("Job taskId：" + taskId + " submit error ", e);
                    ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(taskId);
                    if (scheduleJob == null) {
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
                LOGGER.error("", ex);
            }
        }
        return false;
    }

    public ParamActionExt paramActionExt(ScheduleTaskShade batchTask, String jobId, String flowJobId) throws Exception {
        if (StringUtils.isBlank(jobId)) {
            jobId = this.generateUniqueSign();
        }
        LOGGER.info("startJob ScheduleTaskShade: {} jobId:{} flowJobId:{} ", JSONObject.toJSONString(batchTask), jobId, flowJobId);
        ScheduleJob scheduleJob = buildScheduleJob(batchTask, jobId, flowJobId);
        ParamActionExt paramActionExt = paramActionExt(batchTask, scheduleJob, JSONObject.parseObject(batchTask.getExtraInfo()));
        if (paramActionExt == null) {
            throw new RdosDefineException("extraInfo can't null or empty string");
        }

        paramActionExt.setCycTime(scheduleJob.getCycTime());
        paramActionExt.setTaskSourceId(batchTask.getTaskId());
        paramActionExt.setComponentVersion(batchTask.getComponentVersion());
        paramActionExt.setBusinessType(batchTask.getBusinessType());
        paramActionExt.setBusinessDate(scheduleJob.getBusinessDate());
        paramActionExt.setFlowJobId(flowJobId);
        return paramActionExt;
    }


    public ParamActionExt paramActionExt(ScheduleTaskShade batchTask, ScheduleJob scheduleJob, JSONObject extraInfo) throws Exception {
        return this.parseParamActionExt(scheduleJob, batchTask, extraInfo);
    }

    private ScheduleJob buildScheduleJob(ScheduleTaskShade batchTask, String jobId, String flowJobId) throws IOException, ParseException {
        String cycTime = jobRichOperator.getCycTime(0);
        String scheduleConf = batchTask.getScheduleConf();
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setJobName(RUN_JOB_NAME+RUN_DELIMITER+batchTask.getName()+RUN_DELIMITER+cycTime);
        scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJob.setComputeType(batchTask.getComputeType());

        scheduleJob.setTenantId(batchTask.getTenantId());
        scheduleJob.setJobKey(String.format("%s%s", "tempJob", batchTask.getTaskId(), new DateTime().toString("yyyyMMdd")));
        scheduleJob.setTaskId(-1L);
        scheduleJob.setCreateUserId(getOrDefault(batchTask.getCreateUserId(), -1L));

        scheduleJob.setType(EScheduleType.TEMP_JOB.getType());
        scheduleJob.setBusinessDate(getOrDefault(jobRichOperator.getCycTime(-1), ""));
        scheduleJob.setCycTime(getOrDefault(cycTime, DateTime.now().toString("yyyyMMddHHmmss")));

        if (StringUtils.isNotBlank(scheduleConf)) {
            Map jsonMap = objMapper.readValue(scheduleConf, Map.class);
            jsonMap.put("isFailRetry",false);
            scheduleConf = JSON.toJSONString(jsonMap);
            batchTask.setScheduleConf(scheduleConf);
            ScheduleCron scheduleCron = ScheduleFactory.parseFromJson(scheduleConf);
            scheduleJob.setDependencyType(getOrDefault(scheduleCron.getSelfReliance(), 0));
            scheduleJob.setPeriodType(scheduleCron.getPeriodType());
            scheduleJob.setMaxRetryNum(getOrDefault(scheduleCron.getMaxRetryNum(), 0));
        }

        scheduleJob.setFlowJobId(getOrDefault(flowJobId, "0"));
        scheduleJob.setTaskType(getOrDefault(batchTask.getTaskType(), -2));
        scheduleJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleJob.setVersionId(getOrDefault(batchTask.getVersionId(), 0));
        scheduleJob.setComputeType(getOrDefault(batchTask.getComputeType(), 1));

        return scheduleJob;
    }

    public ParamActionExt parseParamActionExt(ScheduleJob scheduleJob, ScheduleTaskShade batchTask, JSONObject info) throws Exception {
        if (info == null) {
            throw new RdosDefineException("extraInfo can't null or empty string");
        }

        Integer multiEngineType = info.getInteger("multiEngineType");
        String ldapUserName = info.getString("ldapUserName");
        if (org.apache.commons.lang.StringUtils.isNotBlank(ldapUserName)) {
            info.remove("ldapUserName");
            info.remove("ldapPassword");
            info.remove("dbName");
        }
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        dealActionParam(actionParam,multiEngineType,batchTask,scheduleJob);
        actionParam.put("name", scheduleJob.getJobName());
        actionParam.put("taskId", scheduleJob.getJobId());
        actionParam.put("taskType", batchTask.getTaskType());
        actionParam.put("componentVersion",batchTask.getComponentVersion());
        actionParam.put("type",scheduleJob.getType());
        Object tenantId = actionParam.get("tenantId");
        if (Objects.isNull(tenantId)) {
            actionParam.put("tenantId", batchTask.getTenantId());
        }
        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        JSONObject scheduleConf = JSONObject.parseObject(batchTask.getScheduleConf());
        if (scheduleConf != null && scheduleConf.containsKey("isFailRetry")) {
            actionParam.put("isFailRetry", scheduleConf.getBooleanValue("isFailRetry"));
            if (scheduleConf.getBooleanValue("isFailRetry")) {
                int maxRetryNum = scheduleConf.getIntValue("maxRetryNum") == 0 ? 3 : scheduleConf.getIntValue("maxRetryNum");
                actionParam.put("maxRetryNum", maxRetryNum);
                //离线 单位 分钟
                Integer retryIntervalTime = scheduleConf.getInteger("retryIntervalTime");
                if (null != retryIntervalTime) {
                    actionParam.put("retryIntervalTime", retryIntervalTime * 60 * 1000);
                }
            } else {
                actionParam.put("maxRetryNum", 0);
            }
        }
        if (EJobType.SYNC.getType() == scheduleJob.getTaskType()) {
            //数据同步需要解析是perjob 还是session
            EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams(batchTask.getTaskParams(),batchTask.getComputeType(), EngineType.Flink.name(),batchTask.getTenantId());
            actionParam.put("deployMode", eDeployMode.getType());
        }
        return PublicUtil.mapToObject(actionParam, ParamActionExt.class);
    }

    private void dealActionParam(Map<String, Object> actionParam, Integer multiEngineType, ScheduleTaskShade batchTask, ScheduleJob scheduleJob) throws Exception {
        IPipeline pipeline = null;
        String pipelineConfig = null;
        if (actionParam.containsKey(PipelineBuilder.pipelineKey)) {
            pipelineConfig = (String) actionParam.get(PipelineBuilder.pipelineKey);
            pipeline = PipelineBuilder.buildPipeline(pipelineConfig);
        }
        if (pipeline == null) {
            //走旧逻辑
            JobStartTriggerBase jobTriggerService = multiEngineFactory.getJobTriggerService(multiEngineType);
            jobTriggerService.readyForTaskStartTrigger(actionParam, batchTask, scheduleJob);
            return;
        }
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        Map<String, Object> pipelineInitMap = PipelineBuilder.getPipelineInitMap(pipelineConfig, scheduleJob, batchTask, taskParamsToReplace, (pipelineMap) -> {
            //fill 文件上传的信息
            JSONObject pluginInfo = clusterService.pluginInfoJSON(batchTask.getTenantId(), batchTask.getTaskType(), null, null, null);
            Long clusterId = clusterTenantMapper.getClusterIdByTenantId(batchTask.getTenantId());
            String hadoopVersion = componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, EComponentType.HDFS.getTypeCode());
            pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, EComponentType.HDFS.name().toLowerCase() + componentService.formatHadoopVersion(hadoopVersion, EComponentType.HDFS));
            pipelineMap.put(UploadParamPipeline.pluginInfoKey, pluginInfo);
            pipelineMap.put(UploadParamPipeline.workOperatorKey, workerOperator);
            pipelineMap.put(UploadParamPipeline.fileUploadPathKey, environmentContext.getHdfsTaskPath());
        });
        pipeline.execute(actionParam, pipelineInitMap);
    }

    /**
     * 停止的请求接口
     * @throws Exception
     */
    public Boolean stop(List<String> jobIds) {

        if(CollectionUtils.isEmpty(jobIds)){
            throw new RdosDefineException("jobIds不能为空");
        }
        return stop(jobIds, ForceCancelFlag.NO.getFlag());
    }

    public Boolean stop(List<String> jobIds, Integer isForce) {
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.getRdosJobByJobIds(jobIds));
        jobStopDealer.addStopJobs(jobs, isForce);
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
        String jobId = paramActionExt.getTaskId();
        Integer computerType = paramActionExt.getComputeType();
        //当前任务已经存在在engine里面了
        //不允许相同任务同时在engine上运行---考虑将cache的清理放在任务结束的时候(停止，取消，完成)
        if(engineJobCacheDao.getOne(jobId) != null){
            return false;
        }
        boolean result = false;
        try {
            ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
            if(scheduleJob == null){
                scheduleJob = buildScheduleJob(paramActionExt);
                scheduleJobDao.insert(scheduleJob);
                result = true;
            }else{
                result = RdosTaskStatus.canStart(scheduleJob.getStatus());
                if (result) {
                    engineJobRetryDao.removeByJobId(jobId);
                }
                if(result && !RdosTaskStatus.ENGINEACCEPTED.getStatus().equals(scheduleJob.getStatus()) ){
                    scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
                    scheduleJob.setAppType(paramActionExt.getAppType());
                    if (AppType.STREAM.getType().equals(paramActionExt.getAppType())) {
                        scheduleJob.setRetryNum(0);
                    }
                    scheduleJobDao.update(scheduleJob);
                    LOGGER.info("jobId:{} update job status:{}.", scheduleJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus());
                }
            }
            if (result && ComputeType.BATCH.getType().equals(computerType)){
                engineJobCheckpointDao.deleteByTaskId(jobId);
            }
        } catch (Exception e){
            LOGGER.error("", e);
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
        scheduleJob.setBusinessType(paramActionExt.getBusinessType());
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
     * 根据jobid 和 计算类型，查询job的日志
     */
    public ActionLogVO log( String jobId, Integer computeType) {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ActionLogVO vo = new ActionLogVO();
        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);
        if (scheduleJob != null) {
            vo.setLogInfo(scheduleJob.getLogInfo());
            String engineLog = getEngineLog(jobId, scheduleJob);
            vo.setEngineLog(engineLog);
        }
        return vo;
    }

    private String getEngineLog(String jobId, ScheduleJob scheduleJob) {
        String engineLog = scheduleJob.getEngineLog();
        try {
            if (StringUtils.isBlank(engineLog)) {
                engineLog = CompletableFuture.supplyAsync(
                        () ->
                        jobDealer.getAndUpdateEngineLog(jobId, scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getTenantId()),
                        logTimeOutPool
                ).get(environmentContext.getLogTimeout(), TimeUnit.SECONDS);
                if (engineLog == null) {
                    engineLog = "";
                }
            }
        } catch (Exception e){
        }
        return engineLog;
    }

    public JobLogVO logUnite(String jobId, Integer pageInfo) {
        if (StringUtils.isBlank(jobId)) {
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ScheduleJob scheduleJob = scheduleJobDao.getRdosJobByJobId(jobId);

        if (scheduleJob == null) {
            throw new RdosDefineException("job is not exist");
        }

        ScheduleTaskShade taskShadeDao = scheduleTaskShadeDao.getOne(scheduleJob.getTaskId());

        if (taskShadeDao == null) {
            throw new RdosDefineException("task is not exist");
        }

        JobLogVO jobLogVO = new JobLogVO();
        jobLogVO.setName(taskShadeDao.getName());
        jobLogVO.setComputeType(taskShadeDao.getComputeType());
        jobLogVO.setTaskType(taskShadeDao.getTaskType());

        jobLogVO.setExecEndTime(scheduleJob.getExecEndTime());
        jobLogVO.setExecStartTime(scheduleJob.getExecStartTime());

        String engineLog = getEngineLog(jobId, scheduleJob);
        jobLogVO.setEngineLog(engineLog);

        // 封装日志信息
        JSONObject info = new JSONObject();
        try {
            info = JSON.parseObject(scheduleJob.getLogInfo());
        } catch (final Exception e) {
            LOGGER.error("parse jobId {} } logInfo error {}", jobId, scheduleJob.getLogInfo());
            info.put("msg_info", scheduleJob.getLogInfo());
        }

        if (info == null) {
            info = new JSONObject();
        }

        info.put("sql", taskShadeDao.getSqlText());
        info.put("engineLogErr", engineLog);
        jobLogVO.setLogInfo(info.toJSONString());
        try {
            if (scheduleJob.getRetryNum() > 0) {
                String retryLog = buildRetryLog(scheduleJob.getJobId(), pageInfo, jobLogVO);
                if (StringUtils.isNotBlank(retryLog)) {
                    jobLogVO.setLogInfo(retryLog);
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }

        return jobLogVO;
    }


    private String buildRetryLog(final String jobId, Integer pageInfo,JobLogVO batchServerLogVO) throws Exception {
        //先获取engine的日志总数信息
        List<ActionRetryLogVO> actionRetryLogVOs = retryLog(jobId);
        if (CollectionUtils.isEmpty(actionRetryLogVOs)) {
            return "";
        }
        batchServerLogVO.setPageSize(actionRetryLogVOs.size());
        if(Objects.isNull(pageInfo)){
            pageInfo = 0;
        }
        //engine 的 retryNum 从1 开始
        if (0 == pageInfo) {
            pageInfo = actionRetryLogVOs.size();
        }
        if (pageInfo > actionRetryLogVOs.size()) {
            throw new RdosDefineException(ErrorCode.INVALID_PARAMETERS);
        }
        //获取对应的日志
        ActionRetryLogVO retryLogContent = retryLogDetail(jobId, pageInfo);
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(retryLogContent)) {
            return "";
        }
        Integer retryNumVal = retryLogContent.getRetryNum();
        int retryNum = 0;
        if(Objects.nonNull(retryNumVal)){
            retryNum = retryNumVal + 1;
        }
        String logInfo = retryLogContent.getLogInfo();
        String engineInfo = retryLogContent.getEngineLog();
        String retryTaskParams = retryLogContent.getRetryTaskParams();
        builder.append("====================第 ").append(retryNum).append("次重试====================").append("\n");

        if (!Strings.isNullOrEmpty(logInfo)) {
            builder.append("====================LogInfo start====================").append("\n");
            builder.append(logInfo).append("\n");
            builder.append("=====================LogInfo end=====================").append("\n");
        }
        if (!Strings.isNullOrEmpty(engineInfo)) {
            builder.append("==================EngineInfo  start==================").append("\n");
            builder.append(engineInfo).append("\n");
            builder.append("===================EngineInfo  end===================").append("\n");
        }
        if (!Strings.isNullOrEmpty(retryTaskParams)) {
            builder.append("==================RetryTaskParams  start==================").append("\n");
            builder.append(retryTaskParams).append("\n");
            builder.append("===================RetryTaskParams  end===================").append("\n");
        }

        builder.append("==================第").append(retryNum).append("次重试结束==================").append("\n");
        for (int j = 0; j < 10; j++) {
            builder.append("==" + "\n");
        }

        return builder.toString();
    }




    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public List<ActionRetryLogVO> retryLog( String jobId) {

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
    public ActionRetryLogVO retryLogDetail( String jobId, Integer retryNum) {

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
                engineLog = jobDealer.getAndUpdateEngineLog(jobId, jobRetry.getEngineJobId(), jobRetry.getApplicationId(), scheduleJob.getTenantId());
                if (engineLog != null){
                    LOGGER.info("engineJobRetryDao.updateEngineLog id:{}, jobId:{}, engineLog:{}", jobRetry.getId(), jobRetry.getJobId(), engineLog);
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
    public List<ActionJobEntityVO> entitys( List<String> jobIds) {

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

    public String generateUniqueSign(){
        if (null == jobIdWorker) {
            String[] split = AddressUtil.getOneIp().split("\\.");
            jobIdWorker = DtJobIdWorker.getInstance(split.length >= 4 ? Integer.parseInt(split[3]) : 0, 0);
        }
        return jobIdWorker.nextJobId();
    }


    /**
     * 异步重跑任务
     *
     * @return
     */
    public boolean restartJob(RestartType restartType, List<String> jobIds) {
        CompletableFuture.runAsync(new RestartJobRunnable(jobIds,restartType, scheduleJobDao, scheduleTaskShadeDao,
                scheduleJobJobDao, environmentContext,scheduleJobService));
        return true;
    }
}
