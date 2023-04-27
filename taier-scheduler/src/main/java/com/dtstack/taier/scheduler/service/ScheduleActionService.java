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

package com.dtstack.taier.scheduler.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.enums.ForceCancelFlag;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.util.AddressUtil;
import com.dtstack.taier.common.util.DtJobIdWorker;
import com.dtstack.taier.common.util.GenerateErrorMsgUtil;
import com.dtstack.taier.dao.domain.ScheduleJobRetry;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taier.dao.mapper.ScheduleJobRetryMapper;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.jobdealer.JobDealer;
import com.dtstack.taier.scheduler.jobdealer.JobStopDealer;
import com.dtstack.taier.scheduler.server.builder.ScheduleConf;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleConfManager;
import com.dtstack.taier.scheduler.server.builder.cron.ScheduleCorn;
import com.dtstack.taier.scheduler.server.pipeline.IPipeline;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import com.dtstack.taier.scheduler.server.pipeline.PipelineBuilder;
import com.dtstack.taier.scheduler.server.pipeline.operator.SyncOperatorPipeline;
import com.dtstack.taier.scheduler.server.pipeline.operator.UnnecessaryPreprocessJobPipeline;
import com.dtstack.taier.scheduler.server.pipeline.params.UploadParamPipeline;
import com.dtstack.taier.scheduler.utils.FileUtil;
import com.dtstack.taier.scheduler.vo.action.ActionJobEntityVO;
import com.dtstack.taier.scheduler.vo.action.ActionLogVO;
import com.dtstack.taier.scheduler.vo.action.ActionRetryLogVO;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class ScheduleActionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleActionService.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobCacheService scheduleJobCacheService;

    @Autowired
    private ScheduleJobRetryMapper engineJobRetryMapper;

    @Autowired
    private JobDealer jobDealer;

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private UnnecessaryPreprocessJobPipeline unnecessaryPreprocessJobPipeline;

    @Autowired
    private SyncOperatorPipeline syncOperatorPipeline;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private ScriptService scriptService;

    @Autowired
    private DataxService dataxService;

    private final ObjectMapper objMapper = new ObjectMapper();

    private static final PropertyFilter propertyFilter = (object, name, value) ->
            !(name.equalsIgnoreCase("taskParams") || name.equalsIgnoreCase("sqlText"));

    private DtJobIdWorker jobIdWorker;

    @Autowired
    private DatasourceOperator datasourceOperator;

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
                engineJobRetryMapper.delete(Wrappers.lambdaQuery(ScheduleJobRetry.class)
                        .eq(ScheduleJobRetry::getJobId, jobClient.getJobId()));
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
            scheduleJob.setStatus(TaskStatus.SUBMITFAILD.getStatus());
            scheduleJobService.insert(scheduleJob);
        } else {
            //直接失败
            scheduleJobService.jobFail(jobId, TaskStatus.SUBMITFAILD.getStatus(), GenerateErrorMsgUtil.generateErrorMsg(e.getMessage()));
        }
    }

    /**
     * 参数替换
     *
     * @param task
     * @param jobId
     * @param flowJobId
     * @return
     * @throws Exception
     */
    public ParamActionExt paramActionExt(ScheduleTaskShade task, String jobId, String flowJobId) throws Exception {
        if (StringUtils.isBlank(jobId)) {
            jobId = this.generateUniqueSign();
        }
        LOGGER.info("startJob ScheduleTaskShade: {} jobId:{} flowJobId:{} ", JSONObject.toJSONString(task), jobId, flowJobId);
        ScheduleJob scheduleJob = buildScheduleJob(task, jobId, flowJobId);
        ParamActionExt paramActionExt = paramActionExt(task, scheduleJob, JSONObject.parseObject(task.getExtraInfo()));
        if (Objects.isNull(paramActionExt)) {
            throw new TaierDefineException("extraInfo can't null or empty string");
        }
        paramActionExt.setCycTime(scheduleJob.getCycTime());
        paramActionExt.setTaskId(task.getTaskId());
        paramActionExt.setComponentVersion(task.getComponentVersion());
        paramActionExt.setFlowJobId(flowJobId);
        return paramActionExt;
    }


    public ParamActionExt paramActionExt(ScheduleTaskShade task, ScheduleJob scheduleJob, JSONObject extraInfo) throws Exception {
        return this.parseParamActionExt(scheduleJob, task, extraInfo);
    }

    private ScheduleJob buildScheduleJob(ScheduleTaskShade task, String jobId, String flowJobId) throws IOException, ParseException {
        String cycTime = getCycTime(0);
        String scheduleConf = task.getScheduleConf();
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(jobId);
        scheduleJob.setJobName(CommonConstant.RUN_JOB_NAME + CommonConstant.RUN_DELIMITER + task.getName() + CommonConstant.RUN_DELIMITER + cycTime);
        scheduleJob.setStatus(TaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJob.setComputeType(task.getComputeType());

        scheduleJob.setTenantId(task.getTenantId());
        scheduleJob.setJobKey(String.format("%s%s%s", "tempJob", task.getTaskId(), new DateTime().toString("yyyyMMdd")));
        scheduleJob.setTaskId(task.getTaskId());
        scheduleJob.setCreateUserId(getOrDefault(task.getCreateUserId(), -1L));

        scheduleJob.setType(EScheduleType.TEMP_JOB.getType());
        scheduleJob.setCycTime(getOrDefault(cycTime, DateTime.now().toString("yyyyMMddHHmmss")));

        if (StringUtils.isNotBlank(scheduleConf)) {
            Map jsonMap = objMapper.readValue(scheduleConf, Map.class);
            jsonMap.put("isFailRetry", false);
            scheduleConf = JSON.toJSONString(jsonMap);
            task.setScheduleConf(scheduleConf);
            ScheduleCorn scheduleCron = ScheduleConfManager.parseFromJson(scheduleConf);
            ScheduleConf scheduleConfBean = scheduleCron.getScheduleConf();
            scheduleJob.setDependencyType(getOrDefault(scheduleConfBean.getSelfReliance(), 0));
            scheduleJob.setPeriodType(scheduleConfBean.getPeriodType());
            scheduleJob.setMaxRetryNum(getOrDefault(scheduleConfBean.getMaxRetryNum(), 0));
        }

        scheduleJob.setFlowJobId(getOrDefault(flowJobId, "0"));
        scheduleJob.setTaskType(getOrDefault(task.getTaskType(), -2));
        scheduleJob.setNodeAddress(environmentContext.getLocalAddress());
        scheduleJob.setVersionId(getOrDefault(task.getVersionId(), 0));
        scheduleJob.setComputeType(getOrDefault(task.getComputeType(), ComputeType.BATCH.getType()));

        return scheduleJob;
    }

    public ParamActionExt parseParamActionExt(ScheduleJob scheduleJob, ScheduleTaskShade task, JSONObject info) throws Exception {
        if (info == null) {
            throw new TaierDefineException("extraInfo can't null or empty string");
        }
        Map<String, Object> actionParam = PublicUtil.strToMap(info.toJSONString());
        dealActionParam(actionParam, task, scheduleJob);
        actionParam.put("name", scheduleJob.getJobName());
        actionParam.put("jobId", scheduleJob.getJobId());
        actionParam.put("taskType", task.getTaskType());
        actionParam.put("componentVersion", task.getComponentVersion());
        actionParam.put("type", scheduleJob.getType());
        actionParam.put("tenantId", task.getTenantId());
        actionParam.put("queueName", task.getQueueName());
        actionParam.put("datasourceId", task.getDatasourceId());
        actionParam.putIfAbsent("sqlText", task.getSqlText());
        actionParam.putAll(parseRetryParam(task));
        return PublicUtil.mapToObject(actionParam, ParamActionExt.class);
    }

    private Map<String, Object> parseRetryParam(ScheduleTaskShade task) {
        Map<String, Object> retryParam = new HashMap<>();
        JSONObject scheduleConf = JSONObject.parseObject(task.getScheduleConf());
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

    private void dealActionParam(Map<String, Object> actionParam, ScheduleTaskShade task, ScheduleJob scheduleJob) throws Exception {
        IPipeline pipeline = null;
        String pipelineConfig = null;
        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);

        if (actionParam.containsKey(PipelineBuilder.pipelineKey)) {
            pipelineConfig = (String) actionParam.get(PipelineBuilder.pipelineKey);
            pipeline = PipelineBuilder.buildPipeline(pipelineConfig);
        } else if (EScheduleJobType.SYNC.getType().equals(task.getTaskType())) {
            pipeline = syncOperatorPipeline;
        } else if (EScheduleJobType.WORK_FLOW.getType().equals(task.getTaskType())
                || EScheduleJobType.VIRTUAL.getType().equals(task.getTaskType())) {
            pipeline = unnecessaryPreprocessJobPipeline;
        } else if (EScheduleJobType.PYTHON.getType().equals(task.getTaskType())
                || EScheduleJobType.SHELL.getType().equals(task.getTaskType())) {
            scriptService.handScriptParams(actionParam, task, taskParamsToReplace, scheduleJob);
            return;
        } else if (EScheduleJobType.DATAX.getType().equals(task.getTaskType())){
            dataxService.handDataxParams(actionParam, task, taskParamsToReplace, scheduleJob);
            return;
        }else {
            pipeline = PipelineBuilder.buildDefaultSqlPipeline();
        }

        Map<String, Object> pipelineInitMap = PipelineBuilder.getPipelineInitMap(pipelineConfig, scheduleJob, task, taskParamsToReplace, (uploadPipelineMap) -> {
            //fill 文件上传的信息
            JSONObject pluginInfo = clusterService.pluginInfoJSON(task.getTenantId(), task.getTaskType(), null, null, null);
            String hdfsTypeName = componentService.buildHdfsTypeName(task.getTenantId(), null);
            pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, hdfsTypeName);
            uploadPipelineMap.put(UploadParamPipeline.pluginInfoKey, pluginInfo);
            uploadPipelineMap.put(UploadParamPipeline.fileUploadPathKey, environmentContext.getHdfsTaskPath());
        });
        pipeline.execute(actionParam, pipelineInitMap);
    }

    /**
     * 停止的请求接口
     *
     * @throws Exception
     */
    public Boolean stop(List<String> jobIds) {

        if (CollectionUtils.isEmpty(jobIds)) {
            throw new TaierDefineException("jobIds不能为空");
        }
        return stop(jobIds, ForceCancelFlag.NO.getFlag());
    }

    public Boolean stop(List<String> jobIds, Integer isForce) {
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobService.getByJobIds(jobIds));
        if (CollectionUtils.isNotEmpty(jobs)) {
            jobStopDealer.addStopJobs(jobs, isForce);
        }
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
        boolean result = TaskStatus.canStart(scheduleJob.getStatus());
        if (result) {
            engineJobRetryMapper.delete(Wrappers.lambdaQuery(ScheduleJobRetry.class)
                    .eq(ScheduleJobRetry::getJobId, jobId));
            if (!TaskStatus.ENGINEACCEPTED.getStatus().equals(scheduleJob.getStatus())) {
                scheduleJob.setStatus(TaskStatus.ENGINEACCEPTED.getStatus());
                scheduleJobService.updateByJobId(scheduleJob);
                LOGGER.info("jobId:{} update job status:{}.", scheduleJob.getJobId(), TaskStatus.ENGINEACCEPTED.getStatus());
            }
        }
        return result;
    }

    private ScheduleJob buildScheduleJob(ParamActionExt paramActionExt) {
        ScheduleJob scheduleJob = new ScheduleJob();
        scheduleJob.setJobId(paramActionExt.getJobId());
        scheduleJob.setJobName(getOrDefault(paramActionExt.getName(), ""));
        scheduleJob.setStatus(TaskStatus.ENGINEACCEPTED.getStatus());
        scheduleJob.setComputeType(paramActionExt.getComputeType());

        scheduleJob.setTenantId(paramActionExt.getTenantId());
        scheduleJob.setJobKey(getOrDefault(paramActionExt.getJobKey(), String.format("%s%s%s", "tempJob", paramActionExt.getTaskId(), paramActionExt.getJobId())));
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

    private <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 根据jobid 和 计算类型，查询job的日志
     */
    public ActionLogVO log(String jobId) {

        if (StringUtils.isBlank(jobId)) {
            throw new TaierDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        ActionLogVO vo = new ActionLogVO();
        ScheduleJobExpand scheduleJobExpand = scheduleJobExpandService.getByJobId(jobId);
        if (scheduleJobExpand != null) {
            vo.setEngineLog(scheduleJobExpand.getEngineLog());
            vo.setLogInfo(scheduleJobExpand.getLogInfo());
            if (StringUtils.isBlank(scheduleJobExpand.getEngineLog())) {
                ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
                vo.setEngineLog(getEngineLog(jobId, scheduleJob));
            }
        }
        return vo;
    }

    private String getEngineLog(String jobId, ScheduleJob scheduleJob) {
        String engineLog = "";
        if (StringUtils.isBlank(engineLog)) {
            engineLog = jobDealer.getAndUpdateEngineLog(jobId, scheduleJob.getEngineJobId(), scheduleJob.getApplicationId(), scheduleJob.getTenantId());
        }
        return engineLog;
    }


    /**
     * 根据jobid 和 计算类型，查询job的重试retry日志
     */
    public List<ActionRetryLogVO> retryLog(String jobId) {
        if (StringUtils.isBlank(jobId)) {
            throw new TaierDefineException("jobId is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        List<ActionRetryLogVO> logs = new ArrayList<>(5);
        List<ScheduleJobRetry> jobRetries = engineJobRetryMapper.selectList(Wrappers.lambdaQuery(ScheduleJobRetry.class)
                .eq(ScheduleJobRetry::getJobId, jobId).last(" limit 5"));
        if (CollectionUtils.isNotEmpty(jobRetries)) {
            jobRetries.forEach(jobRetry -> {
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
    public ActionRetryLogVO retryLogDetail(String jobId, Integer retryNum) {

        if (StringUtils.isBlank(jobId)) {
            throw new TaierDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }
        if (retryNum == null || retryNum <= 0) {
            retryNum = 1;
        }
        ScheduleJob scheduleJob = scheduleJobService.getByJobId(jobId);
        //数组库中存储的retryNum为0开始的索引位置
        ScheduleJobRetry jobRetry = engineJobRetryMapper
                .selectOne(Wrappers.lambdaQuery(ScheduleJobRetry.class)
                        .eq(ScheduleJobRetry::getJobId, jobId)
                        .eq(ScheduleJobRetry::getRetryNum, retryNum - 1));
        ActionRetryLogVO vo = new ActionRetryLogVO();
        if (jobRetry != null) {
            vo.setRetryNum(jobRetry.getRetryNum());
            vo.setLogInfo(jobRetry.getLogInfo());
            String engineLog = jobRetry.getEngineLog();
            if (StringUtils.isBlank(jobRetry.getEngineLog())) {
                engineLog = jobDealer.getAndUpdateEngineLog(jobId, jobRetry.getEngineJobId(), jobRetry.getApplicationId(), scheduleJob.getTenantId());
                if (engineLog != null) {
                    LOGGER.info("engineJobRetryDao.updateEngineLog id:{}, jobId:{}, engineLog:{}", jobRetry.getId(), jobRetry.getJobId(), engineLog);
                    jobRetry.setEngineLog(engineLog);
                    engineJobRetryMapper.updateById(jobRetry);
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
    public List<ActionJobEntityVO> entitys(List<String> jobIds) {

        if (CollectionUtils.isEmpty(jobIds)) {
            throw new TaierDefineException("jobId  is not allow null", ErrorCode.INVALID_PARAMETERS);
        }

        List<ActionJobEntityVO> result = null;
        List<ScheduleJob> scheduleJobs = scheduleJobService.getByJobIds(jobIds);
        if (CollectionUtils.isNotEmpty(scheduleJobs)) {
            result = new ArrayList<>(scheduleJobs.size());
            for (ScheduleJob scheduleJob : scheduleJobs) {
                ActionJobEntityVO vo = new ActionJobEntityVO();
                vo.setJobId(scheduleJob.getJobId());
                vo.setStatus(scheduleJob.getStatus());
                vo.setEngineJobId(scheduleJob.getEngineJobId());
                vo.setApplicationId(scheduleJob.getApplicationId());
                result.add(vo);
            }
        }
        return result;
    }

    public String generateUniqueSign() {
        if (null == jobIdWorker) {
            String[] split = AddressUtil.getOneIp().split("\\.");
            jobIdWorker = DtJobIdWorker.getInstance(split.length >= 4 ? Integer.parseInt(split[3]) : 0, 0);
        }
        return jobIdWorker.nextJobId();
    }

    public String getCycTime(Integer beforeDay) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        if (beforeDay == null || beforeDay == 0) {
            return sdf.format(calendar.getTime());
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + beforeDay);
        return sdf.format(calendar.getTime());
    }


}