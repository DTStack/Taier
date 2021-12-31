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
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.common.enums.ForceCancelFlag;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.DtJobIdWorker;
import com.dtstack.engine.common.util.GenerateErrorMsgUtil;
import com.dtstack.engine.domain.EngineJobRetry;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.dto.ScheduleTaskParamShade;
import com.dtstack.engine.mapper.ClusterTenantMapper;
import com.dtstack.engine.mapper.ComponentMapper;
import com.dtstack.engine.mapper.EngineJobRetryDao;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.action.restart.RestartJobRunnable;
import com.dtstack.engine.master.dto.schedule.ActionJobKillDTO;
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
import com.dtstack.engine.master.service.ScheduleJobExpandService;
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
import com.dtstack.engine.pluginapi.util.PublicUtil;
import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
@Service("OldActionService")
public class ActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionService.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobCacheService scheduleJobCacheService;

    @Autowired
    private EngineJobRetryDao engineJobRetryDao;

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
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

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
    public Boolean start(ParamActionExt paramActionExt) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("start  actionParam: {}", JSONObject.toJSONString(paramActionExt, propertyFilter));
        }
        try {
            boolean canAccepted = receiveStartJob(paramActionExt);
            //会对重复数据做校验
            if (canAccepted) {
                JobClient jobClient = new JobClient(paramActionExt);
                jobClient.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
                jobDealer.addSubmitJob(jobClient);
                return true;
            }
            LOGGER.warn("jobId：" + paramActionExt.getJobId() + " duplicate submissions are not allowed");
        } catch (Exception e) {
            runJobFail(paramActionExt, e, paramActionExt.getJobId());
        }
        return false;
    }

    private void runJobFail(ParamActionExt paramActionExt, Exception e, String jobId) {
        LOGGER.error("Job ：" + jobId + " submit error ", e);
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        if (scheduleJob == null) {
            //新job 任务
            scheduleJob = buildScheduleJob(paramActionExt);
            scheduleJob.setStatus(RdosTaskStatus.SUBMITFAILD.getStatus());
            scheduleJobService.insert(scheduleJob);
        } else {
            //直接失败
            scheduleJobService.jobFail(jobId, RdosTaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()));
        }
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
        paramActionExt.setTaskId(batchTask.getTaskId());
        paramActionExt.setComponentVersion(batchTask.getComponentVersion());
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
        scheduleJob.setJobKey(String.format("%s%s%s", "tempJob", batchTask.getTaskId(), new DateTime().toString("yyyyMMdd")));
        scheduleJob.setTaskId(batchTask.getTaskId());
        scheduleJob.setCreateUserId(getOrDefault(batchTask.getCreateUserId(), -1L));

        scheduleJob.setType(EScheduleType.TEMP_JOB.getType());
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
        scheduleJob.setComputeType(getOrDefault(batchTask.getComputeType(), ComputeType.BATCH.getType()));

        return scheduleJob;
    }

    public ParamActionExt parseParamActionExt(ScheduleJob scheduleJob, ScheduleTaskShade batchTask, JSONObject info) throws Exception {
        if (info == null) {
            throw new RdosDefineException("extraInfo can't null or empty string");
        }
        Integer multiEngineType = info.getInteger("multiEngineType");
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        dealActionParam(actionParam,multiEngineType,batchTask,scheduleJob);
        actionParam.put("name", scheduleJob.getJobName());
        actionParam.put("jobId", scheduleJob.getJobId());
        actionParam.put("taskType", batchTask.getTaskType());
        actionParam.put("componentVersion",batchTask.getComponentVersion());
        actionParam.put("type",scheduleJob.getType());
        actionParam.put("tenantId", batchTask.getTenantId());
        actionParam.putAll(parseRetryParam(batchTask));
        if (EJobType.SYNC.getType() == scheduleJob.getTaskType()) {
            //数据同步需要解析是perJob 还是session
            EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams((String)actionParam.get("taskParam"),batchTask.getComputeType());
            actionParam.put("deployMode", eDeployMode.getType());
        }
        return PublicUtil.mapToObject(actionParam, ParamActionExt.class);
    }

    private Map<String,Object> parseRetryParam(ScheduleTaskShade batchTask) {
        Map<String, Object> retryParam = new HashMap<>();
        JSONObject scheduleConf = JSONObject.parseObject(batchTask.getScheduleConf());
        if (scheduleConf != null && scheduleConf.containsKey("isFailRetry")) {
            retryParam.put("isFailRetry", scheduleConf.getBooleanValue("isFailRetry"));
            if (scheduleConf.getBooleanValue("isFailRetry")) {
                int maxRetryNum = scheduleConf.getIntValue("maxRetryNum") == 0 ? 3 : scheduleConf.getIntValue("maxRetryNum");
                retryParam.put("maxRetryNum", maxRetryNum);
                //离线 单位 分钟
                Integer retryIntervalTime = scheduleConf.getInteger("retryIntervalTime");
                if (null != retryIntervalTime) {
                    retryParam.put("retryIntervalTime", retryIntervalTime * 60 * 1000);
                }
            } else {
                retryParam.put("maxRetryNum", 0);
            }
        }
        return retryParam;
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
            JSONObject pluginInfo = clusterService.pluginInfoJSON(batchTask.getTenantId(), batchTask.getTaskType(), null, null);
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
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobService.getByJobIds(jobIds));
        jobStopDealer.addStopJobs(jobs, isForce);
        return true;
    }


    private boolean receiveStartJob(ParamActionExt paramActionExt) {
        String jobId = paramActionExt.getJobId();
        //不允许相同任务同时在engine上运行---考虑将cache的清理放在任务结束的时候(停止，取消，完成)
        if (scheduleJobCacheService.getJobCacheByJobId(jobId) != null) {
            return false;
        }
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        if (scheduleJob == null) {
            scheduleJob = buildScheduleJob(paramActionExt);
            scheduleJobService.insert(scheduleJob);

            return true;
        }
        boolean result = RdosTaskStatus.canStart(scheduleJob.getStatus());
        if (result) {
            engineJobRetryDao.removeByJobId(jobId);
            if (!RdosTaskStatus.ENGINEACCEPTED.getStatus().equals(scheduleJob.getStatus())) {
                scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
                scheduleJobService.updateByJobId(scheduleJob);
                LOGGER.info("jobId:{} update job status:{}.", scheduleJob.getJobId(), RdosTaskStatus.ENGINEACCEPTED.getStatus());
            }
        }
        return result;
    }

    private ScheduleJob buildScheduleJob(ParamActionExt paramActionExt) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(paramActionExt.getJobId());
        scheduleJob.setJobName(getOrDefault(paramActionExt.getName(),""));
        scheduleJob.setStatus(RdosTaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJob.setComputeType(paramActionExt.getComputeType());

        scheduleJob.setTenantId(paramActionExt.getTenantId());
        scheduleJob.setJobKey(getOrDefault(paramActionExt.getJobKey(), String.format("%s%s%s", "tempJob", paramActionExt.getTaskId(), new DateTime().toString("yyyyMMdd") )));
        scheduleJob.setTaskId(getOrDefault(paramActionExt.getTaskId(), -1L));
        scheduleJob.setCreateUserId(getOrDefault(paramActionExt.getCreateUserId(), -1L));

        scheduleJob.setType(getOrDefault(paramActionExt.getType(), EScheduleType.TEMP_JOB.getType()));
        scheduleJob.setIsRestart(getOrDefault(paramActionExt.getIsRestart(), 0));
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
     * 根据jobid 和 计算类型，查询job的日志
     */
    public ActionLogVO log( String jobId, Integer computeType) {

        if (StringUtils.isBlank(jobId)){
            throw new RdosDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ActionLogVO vo = new ActionLogVO();
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        if (scheduleJob != null) {
//            vo.setLogInfo(scheduleJob.getLogInfo());
            String engineLog = getEngineLog(jobId, scheduleJob);
            vo.setEngineLog(engineLog);
        }
        return vo;
    }

    private String getEngineLog(String jobId, ScheduleJob scheduleJob) {
        String engineLog = "";
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
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
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
        List<ScheduleJob> scheduleJobs = scheduleJobService.getByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
        	result = new ArrayList<>(scheduleJobs.size());
        	for (ScheduleJob scheduleJob:scheduleJobs){
                ActionJobEntityVO vo = new ActionJobEntityVO();
                vo.setJobId(scheduleJob.getJobId());
                vo.setStatus(scheduleJob.getStatus());
//                vo.setExecStartTime(scheduleJob.getExecStartTime());
//                vo.setLogInfo(scheduleJob.getLogInfo());
//                vo.setEngineLog(scheduleJob.getEngineLog());
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


}
