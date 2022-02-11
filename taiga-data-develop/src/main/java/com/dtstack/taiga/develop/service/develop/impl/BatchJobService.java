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

package com.dtstack.taiga.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taiga.common.constant.TaskStatusConstant;
import com.dtstack.taiga.common.enums.EScheduleJobType;
import com.dtstack.taiga.common.enums.TaskStatus;
import com.dtstack.taiga.common.enums.TempJobType;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.util.JsonUtils;
import com.dtstack.taiga.common.util.MathUtil;
import com.dtstack.taiga.dao.domain.BatchSelectSql;
import com.dtstack.taiga.dao.domain.BatchTask;
import com.dtstack.taiga.dao.domain.BatchTaskParam;
import com.dtstack.taiga.dao.domain.BatchTaskParamShade;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleTaskShade;
import com.dtstack.taiga.dao.domain.Tenant;
import com.dtstack.taiga.dao.domain.User;
import com.dtstack.taiga.develop.dto.devlop.BatchParamDTO;
import com.dtstack.taiga.develop.service.console.TenantService;
import com.dtstack.taiga.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taiga.develop.service.develop.IBatchJobExeService;
import com.dtstack.taiga.develop.service.schedule.JobService;
import com.dtstack.taiga.develop.service.user.UserService;
import com.dtstack.taiga.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taiga.develop.web.develop.result.BatchGetSyncTaskStatusInnerResultVO;
import com.dtstack.taiga.develop.web.develop.result.BatchStartSyncResultVO;
import com.dtstack.taiga.pluginapi.enums.ComputeType;
import com.dtstack.taiga.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taiga.scheduler.impl.pojo.ParamTaskAction;
import com.dtstack.taiga.scheduler.service.ScheduleActionService;
import com.dtstack.taiga.scheduler.vo.action.ActionJobEntityVO;
import com.dtstack.taiga.scheduler.vo.action.ActionLogVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/3
 */
@Service
public class BatchJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String DOWNLOAD_URL = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s&tenantId=%s";

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private BatchServerLogService batchServerLogService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private BatchTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private JobService jobService;

    /**
     * 构建运行任务的完整命令(包含真正执行的SQL内容)
     * @param batchTask
     * @param userId
     * @param taskParamsToReplace SQL中需要匹配和替换的 系统参数与自定义参数
     * @return info信息
     * @throws Exception
     */
    public String getExtraInfo(BatchTask batchTask, Long userId, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        //任务参数若为null，则表示是提交任务，否则就是临时运行任务
        if(taskParamsToReplace == null){
            taskParamsToReplace = this.batchTaskParamShadeService.getTaskParam(batchTask.getId());
        }
        IBatchJobExeService jobExecuteService = this.multiEngineServiceFactory.getBatchJobExeService(batchTask.getTaskType());

        //构建任务运行完整信息
        Map<String, Object> actionParam = Maps.newHashMap();
        //构建 sqlText、taskParams，如果是数据同步任务，则根据id替换数据源
        jobExecuteService.readyForTaskStartTrigger(actionParam, batchTask.getTenantId(), batchTask, taskParamsToReplace);

        actionParam.put("taskId", batchTask.getId());
        actionParam.put("taskType", EScheduleJobType.getByTaskType(batchTask.getTaskType()).getEngineJobType());
        actionParam.put("name", batchTask.getName());
        actionParam.put("computeType", batchTask.getComputeType());
        actionParam.put("tenantId", batchTask.getTenantId());
        actionParam.put("isFailRetry", false);
        actionParam.put("maxRetryNum", 0);
        actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));

        User user;
        if (Objects.isNull(userId)) {
            user = userService.getById(batchTask.getOwnerUserId());
        } else {
            user = userService.getById(userId);
        }
        if (Objects.isNull(user)) {
            throw new RdosDefineException(String.format("当前用户已被移除，userId：%d", userId == null ? batchTask.getOwnerUserId() : userId));
        }
        actionParam.put("userId", user.getId());
        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        JSONObject scheduleConf = JSON.parseObject(batchTask.getScheduleConf());
        if (scheduleConf.containsKey("isFailRetry")) {
            actionParam.put("isFailRetry", scheduleConf.getBooleanValue("isFailRetry"));
            if (scheduleConf.getBooleanValue("isFailRetry")) {
                int maxRetryNum = scheduleConf.getIntValue("maxRetryNum") == 0 ? 3 : scheduleConf.getIntValue("maxRetryNum");
                actionParam.put("maxRetryNum", maxRetryNum);
            } else {
                actionParam.put("maxRetryNum", 0);
            }
        }
        String extraInfo = objMapper.writeValueAsString(actionParam);
        extraInfo = extraInfo.replaceAll("\r\n", System.getProperty("line.separator"));
        return extraInfo;
    }

    /**
     * 运行同步任务
     *
     * @return
     */
    public BatchStartSyncResultVO startSyncImmediately(Long taskId, Long userId, Boolean isRoot, Long tenantId) {
        BatchStartSyncResultVO batchStartSyncResultVO = new BatchStartSyncResultVO();
        batchStartSyncResultVO.setMsg(null);
        batchStartSyncResultVO.setJobId(null);
        batchStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());

        BatchTask batchTask = batchTaskService.getOneWithError(taskId);

        if (!batchTask.getTaskType().equals(EScheduleJobType.SYNC.getVal())) {
            throw new RdosDefineException("只支持同步任务直接运行");
        }

        try {
            IBatchJobExeService batchJobExeService = this.multiEngineServiceFactory.getBatchJobExeService(EScheduleJobType.SYNC.getType());
            Map<String, Object> actionParam = batchJobExeService.readyForSyncImmediatelyJob(batchTask, tenantId, isRoot);
            String extraInfo = JSON.toJSONString(actionParam);
            ParamTaskAction paramTaskAction = new ParamTaskAction();
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(extraInfo, ScheduleTaskShade.class);
            scheduleTaskShade.setExtraInfo(extraInfo);
            scheduleTaskShade.setTaskId(batchTask.getId());
            scheduleTaskShade.setScheduleConf(batchTask.getScheduleConf());
            scheduleTaskShade.setComponentVersion(batchTask.getComponentVersion());
            paramTaskAction.setBatchTask(scheduleTaskShade);
            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getBatchTask(),paramTaskAction.getJobId(),paramTaskAction.getFlowJobId());
            String jobId = paramActionExt.getJobId();
            actionService.start(paramActionExt);
            String name = MathUtil.getString(actionParam.get("name"));
            String job = MathUtil.getString(actionParam.get("job"));
            batchSelectSqlService.addSelectSql(jobId, name, TempJobType.SYNC_TASK.getType(), batchTask.getTenantId(),
                    job, userId, EScheduleJobType.SPARK_SQL.getType());

            batchStartSyncResultVO.setMsg(String.format("任务提交成功,名称为: %s", name));
            batchStartSyncResultVO.setJobId(jobId);
            batchStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());
        } catch (Exception e) {
            LOGGER.warn("startSyncImmediately-->", e);
            batchStartSyncResultVO.setMsg(e.getMessage());
            batchStartSyncResultVO.setStatus(TaskStatus.SUBMITFAILD.getStatus());
        }
        return batchStartSyncResultVO;
    }

    /**
     * 获取同步任务运行状态
     */
    public BatchGetSyncTaskStatusInnerResultVO getSyncTaskStatus(Long tenantId, String jobId) {
        return this.getSyncTaskStatusInner(tenantId, jobId, 0);
    }

    private BatchGetSyncTaskStatusInnerResultVO getSyncTaskStatusInner(final Long tenantId, final String jobId, int retryTimes) {
        final BatchGetSyncTaskStatusInnerResultVO resultVO = new BatchGetSyncTaskStatusInnerResultVO();
        resultVO.setMsg(null);
        resultVO.setStatus(TaskStatus.RUNNING.getStatus());

        try {
            ScheduleJob job = jobService.getScheduleJob(jobId);
            if (Objects.isNull(job)) {
                resultVO.setMsg("无法获取engine数据");
                return resultVO;
            }

            Integer status = TaskStatusConstant.getShowStatus(job.getStatus());
            resultVO.setStatus(status);
            if (TaskStatus.RUNNING.getStatus().equals(status)) {
                resultVO.setMsg("运行中");
            }

            final JSONObject logsBody = new JSONObject(2);
            logsBody.put("jobId", jobId);
            logsBody.put("jobIds", Lists.newArrayList(jobId));
            logsBody.put("computeType", ComputeType.BATCH.getType());
            ActionLogVO actionLogVO = actionService.log(jobId);
            String engineLogStr = actionLogVO.getEngineLog();
            String logInfoStr = actionLogVO.getLogInfo();
            if(StringUtils.isNotBlank(engineLogStr)){
                //移除increConf 信息
                try {
                    JSONObject engineLogJson = JSON.parseObject(engineLogStr);
                    engineLogJson.remove("increConf");
                    engineLogStr = engineLogJson.toJSONString();
                } catch (Exception e) {
                    LOGGER.error("", e);
                    if (TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.CANCELED.getStatus().equals(status)
                            || TaskStatus.FAILED.getStatus().equals(status)) {
                        resultVO.setMsg(engineLogStr);
                        resultVO.setDownload(String.format(BatchJobService.DOWNLOAD_URL, jobId, EScheduleJobType.SYNC.getVal(), tenantId));
                    }
                    return resultVO;
                }
            }

            if (StringUtils.isEmpty(engineLogStr) && StringUtils.isEmpty(logInfoStr)) {
                return resultVO;
            }

            try {
                final JSONObject engineLog = JSON.parseObject(engineLogStr);
                final JSONObject logIngo = JSON.parseObject(logInfoStr);
                final StringBuilder logBuild = new StringBuilder();

                // 读取prometheus的相关信息
                Tenant tenantById = this.tenantService.getTenantById(tenantId);
                if (Objects.isNull(tenantById)) {
                    LOGGER.info("can not find job tenent{}.", tenantId);
                    throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION);
                }
                List<ActionJobEntityVO> engineEntities = actionService.entitys(Collections.singletonList(jobId));

                String applicationId = "";
                if (CollectionUtils.isNotEmpty(engineEntities)) {
                    applicationId = engineEntities.get(0).getEngineJobId();
                }
                final long startTime = Objects.isNull(job.getExecStartTime()) ? System.currentTimeMillis(): job.getExecStartTime().getTime();
                final String perf = StringUtils.isBlank(applicationId) ? null : this.batchServerLogService.formatPerfLogInfo(applicationId,jobId, startTime, System.currentTimeMillis(), tenantById.getId());
                if (StringUtils.isNotBlank(perf)) {
                    logBuild.append(perf.replace("\n", "  "));
                }

                if (TaskStatus.FAILED.getStatus().equals(status)) {
                    // 失败的话打印失败日志
                    logBuild.append("\n");
                    logBuild.append("====================Flink日志====================\n");

                    if (engineLog != null) {
                        if (StringUtils.isEmpty(engineLog.getString("root-exception")) && retryTimes < 3) {
                            retryTimes++;
                            Thread.sleep(500);
                            return this.getSyncTaskStatusInner(tenantId, jobId, retryTimes);
                        } else {
                            if (engineLog.containsKey("engineLogErr")) {
                                // 有这个字段表示日志没有获取到，目前engine端只对flink任务做了这种处理，这里先提前加上
                                logBuild.append(engineLog.getString("engineLogErr"));
                            } else {
                                logBuild.append(engineLog.getString("root-exception"));
                            }
                            logBuild.append("\n");
                        }
                    }

                    if (logIngo != null) {
                        logBuild.append(logIngo.getString("msg_info"));
                        logBuild.append("\n");
                    }

                    final BatchSelectSql batchHiveSelectSql = this.batchSelectSqlService.getByJobId(jobId, tenantId, 0);
                    if (batchHiveSelectSql != null) {
                        logBuild.append("====================任务信息====================\n");
                        final String sqlLog=batchHiveSelectSql.getCorrectSqlText().replaceAll("(\"password\"[^\"]+\")([^\"]+)(\")","$1**$3");
                        logBuild.append(JsonUtils.formatJSON(sqlLog));
                        logBuild.append("\n");
                    }
                } else if (TaskStatus.FINISHED.getStatus().equals(status) && retryTimes < 3) {
                    // FIXME perjob模式运行任务，任务完成后统计信息可能还没收集到，这里等待1秒后再请求一次结果
                    Thread.sleep(1000);
                    return this.getSyncTaskStatusInner(tenantId, jobId, 3);
                }
                if (TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.CANCELED.getStatus().equals(status)
                        || TaskStatus.FAILED.getStatus().equals(status)) {
                    resultVO.setDownload(String.format(BatchJobService.DOWNLOAD_URL, jobId, EScheduleJobType.SYNC.getVal(), tenantId));
                }
                resultVO.setMsg(logBuild.toString());
            } catch (Exception e) {
                // 日志解析失败，可能是任务失败，日志信息为非json格式
                LOGGER.error("", e);
                resultVO.setMsg(StringUtils.isEmpty(engineLogStr) ? "engine调度失败" : engineLogStr);
            }
        } catch (Exception e) {
            LOGGER.error("获取同步任务状态失败", e);
        }

        return resultVO;
    }

    /**
     * 停止同步任务
     */
    public void stopSyncJob(String jobId) {
        actionService.stop(Collections.singletonList(jobId));
    }


    /**
     * 运行SQL任务
     * @param userId
     * @param tenantId
     * @param taskId
     * @param uniqueKey
     * @param sql
     * @param taskVariables
     * @param dtToken
     * @param isCheckDDL
     * @param isRoot
     * @param isEnd         是否是当前session最后一条sql
     * @return
     */
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String uniqueKey, String sql, List<Map> taskVariables, String dtToken, Integer isCheckDDL, Boolean isRoot, Boolean isEnd) {
        final User user = userService.getById(userId);
        dtToken = String.format("%s;dt_user_id=%s;dt_username=%s;", dtToken, user.getId(), user.getUserName());
        ExecuteResultVO result = new ExecuteResultVO();
        try {
            final BatchTask task = batchTaskService.getOneWithError(taskId);

            result.setTaskType(task.getTaskType());
            //真正运行的SQL是页面传入的SQL
            task.setSqlText(sql);

            //将SQL中的 系统参数和自定义参数 转换为DTO对象
            List<BatchParamDTO> batchParamDTOS = this.batchTaskParamService.paramResolver(taskVariables);
            final List<BatchTaskParam> params = this.batchTaskParamService.convertParam(batchParamDTOS);
            List<BatchTaskParamShade> taskParamsToReplace = this.batchTaskParamService.convertShade(params);
            ParamTaskAction paramTaskAction = getParamTaskAction(task, userId, taskParamsToReplace);

            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getBatchTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
            sql = paramActionExt.getSqlText();
            String jobId = paramActionExt.getJobId();
            task.setTaskParams(paramActionExt.getTaskParams());

            IBatchJobExeService batchJobService = this.multiEngineServiceFactory.getBatchJobExeService(task.getTaskType());
            result = batchJobService.startSqlImmediately(userId, tenantId, uniqueKey, taskId, sql, isRoot, task, dtToken, isEnd, jobId);
        } catch (Exception e) {
            LOGGER.warn("startSqlImmediately-->", e);
            result.setMsg(e.getMessage());
            result.setStatus(TaskStatus.FAILED.getStatus());
            result.setSqlText(sql);
            return result;
        }
        return result;
    }


    /**
     * 停止通过sql任务执行的sql查询语句
     */
    public void stopSqlImmediately(String jobId, Long tenantId) {
        if (StringUtils.isNotBlank(jobId)) {
            this.batchSelectSqlService.stopSelectJob(jobId, tenantId);
        }
    }

    public String getEngineJobId(String jobId) {
        List<ActionJobEntityVO> engineEntities = actionService.entitys(Lists.newArrayList(jobId));
        if (CollectionUtils.isNotEmpty(engineEntities)) {
            return engineEntities.get(0).getEngineJobId();
        }
        return "";
    }
    /**
     * 初始化engine paramActionExt 入参
     * @param batchTask
     * @param userId
     * @param taskParamsToReplace  需要替换的 系统参数和自定义参数
     * @return
     * @throws Exception
     */
    private ParamTaskAction getParamTaskAction(BatchTask batchTask, Long userId, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        ParamTaskAction paramTaskAction = new ParamTaskAction();

        //将 BatchTask 对象转换为调度的 ScheduleTaskShade 对象
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        BeanUtils.copyProperties(batchTask, scheduleTaskShade);
        scheduleTaskShade.setTaskId(batchTask.getId());
        scheduleTaskShade.setTaskType(batchTask.getTaskType());

        //构建运行任务的完整命令(包含真正执行的SQL内容)
        String extraInfo = getExtraInfo(batchTask, userId, taskParamsToReplace);

        JSONObject jsonObject = JSON.parseObject(extraInfo);
        if (jsonObject.containsKey("sqlText")) {
            jsonObject.put("sqlText", batchTask.getSqlText());
        }
        extraInfo = jsonObject.toJSONString();
        scheduleTaskShade.setExtraInfo(extraInfo);
        paramTaskAction.setBatchTask(scheduleTaskShade);
        return paramTaskAction;
    }

}

