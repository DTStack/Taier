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

package com.dtstack.taier.develop.service.develop.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.JsonUtils;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.Tenant;
import com.dtstack.taier.develop.dto.devlop.DevelopParamDTO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.console.TenantService;
import com.dtstack.taier.develop.service.develop.IDevelopJobExeService;
import com.dtstack.taier.develop.service.develop.ITaskService;
import com.dtstack.taier.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taier.develop.service.develop.TaskContext;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.vo.develop.result.DevelopGetSyncTaskStatusInnerResultVO;
import com.dtstack.taier.develop.vo.develop.result.DevelopStartSyncResultVO;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.impl.pojo.ParamTaskAction;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.dtstack.taier.scheduler.vo.action.ActionJobEntityVO;
import com.dtstack.taier.scheduler.vo.action.ActionLogVO;
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


@Service
public class DevelopJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String DOWNLOAD_URL = "/taier/developDownload/downloadJobLog?jobId=%s&taskType=%s&tenantId=%s";

    @Autowired
    private DevelopTaskService developTaskService;

    @Autowired
    private DevelopServerLogService developServerLogService;

    @Autowired
    private DevelopTaskParamService developTaskParamService;

    @Autowired
    private DevelopSelectSqlService developSelectSqlService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private DevelopTaskParamShadeService developTaskParamShadeService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private JobService jobService;

    @Autowired
    private TaskContext taskContext;

    /**
     * 构建运行任务的完整命令(包含真正执行的SQL内容)
     *
     * @param task
     * @param userId
     * @param taskParamsToReplace SQL中需要匹配和替换的 系统参数与自定义参数
     * @return info信息
     * @throws Exception
     */
    public String getExtraInfo(Task task, Long userId, List<DevelopTaskParamShade> taskParamsToReplace) throws Exception {
        //任务参数若为null，则表示是提交任务，否则就是临时运行任务
        if (taskParamsToReplace == null) {
            taskParamsToReplace = this.developTaskParamShadeService.getTaskParam(task.getId());
        }
        IDevelopJobExeService jobExecuteService = this.multiEngineServiceFactory.getDevelopJobExeService(task.getTaskType());
        //构建任务运行完整信息
        Map<String, Object> actionParam = Maps.newHashMap();
        //构建 sqlText、taskParams，如果是数据同步任务，则根据id替换数据源
        jobExecuteService.readyForTaskStartTrigger(actionParam, task.getTenantId(), task, taskParamsToReplace);
        actionParam.put("taskId", task.getId());
        actionParam.put("taskType", EScheduleJobType.getByTaskType(task.getTaskType()).getEngineJobType());
        actionParam.put("name", task.getName());
        actionParam.put("computeType", task.getComputeType());
        actionParam.put("tenantId", task.getTenantId());
        actionParam.put("isFailRetry", false);
        actionParam.put("maxRetryNum", 0);
        actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));
        actionParam.put("userId", userId);
        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        JSONObject scheduleConf = JSON.parseObject(task.getScheduleConf());
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
    public DevelopStartSyncResultVO startSyncImmediately(Long taskId, Long userId, Boolean isRoot, Long tenantId) {
        DevelopStartSyncResultVO developStartSyncResultVO = new DevelopStartSyncResultVO();
        developStartSyncResultVO.setMsg(null);
        developStartSyncResultVO.setJobId(null);
        developStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());
        Task task = developTaskService.getOneWithError(taskId);
        if (!EScheduleJobType.SYNC.getVal().equals(task.getTaskType())) {
            throw new RdosDefineException("只支持同步任务直接运行");
        }
        try {
            IDevelopJobExeService developJobExeService = this.multiEngineServiceFactory.getDevelopJobExeService(EScheduleJobType.SYNC.getType());
            Map<String, Object> actionParam = developJobExeService.readyForSyncImmediatelyJob(task, tenantId, isRoot);
            String extraInfo = JSON.toJSONString(actionParam);
            ParamTaskAction paramTaskAction = new ParamTaskAction();
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(extraInfo, ScheduleTaskShade.class);
            scheduleTaskShade.setExtraInfo(extraInfo);
            scheduleTaskShade.setTaskId(task.getId());
            scheduleTaskShade.setScheduleConf(task.getScheduleConf());
            scheduleTaskShade.setComponentVersion(task.getComponentVersion());
            paramTaskAction.setTask(scheduleTaskShade);
            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
            String jobId = paramActionExt.getJobId();
            actionService.start(paramActionExt);
            String name = MathUtil.getString(actionParam.get("name"));
            String job = MathUtil.getString(actionParam.get("job"));
            developSelectSqlService.addSelectSql(jobId, name, TempJobType.SYNC_TASK.getType(), task.getTenantId(),
                    job, userId, EScheduleJobType.SYNC.getType());
            developStartSyncResultVO.setMsg(String.format("任务提交成功,名称为: %s", name));
            developStartSyncResultVO.setJobId(jobId);
            developStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());
        } catch (Exception e) {
            LOGGER.warn("startSyncImmediately-->", e);
            developStartSyncResultVO.setMsg(e.getMessage());
            developStartSyncResultVO.setStatus(TaskStatus.SUBMITFAILD.getStatus());
        }
        return developStartSyncResultVO;
    }

    /**
     * 获取同步任务运行状态
     */
    public DevelopGetSyncTaskStatusInnerResultVO getSyncTaskStatus(Long tenantId, String jobId) {
        return this.getSyncTaskStatusInner(tenantId, jobId, 0);
    }

    private DevelopGetSyncTaskStatusInnerResultVO getSyncTaskStatusInner(final Long tenantId, final String jobId, int retryTimes) {
        final DevelopGetSyncTaskStatusInnerResultVO resultVO = new DevelopGetSyncTaskStatusInnerResultVO();
        resultVO.setMsg(null);
        resultVO.setStatus(TaskStatus.RUNNING.getStatus());

        try {
            ScheduleJob job = jobService.getScheduleJob(jobId);
            if (Objects.isNull(job)) {
                resultVO.setMsg("无法获取engine数据");
                return resultVO;
            }

            Integer status = TaskStatus.getShowStatus(job.getStatus());
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
            if (StringUtils.isNotBlank(engineLogStr)) {
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
                        resultVO.setDownload(String.format(DevelopJobService.DOWNLOAD_URL, jobId, EScheduleJobType.SYNC.getVal(), tenantId));
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
                    LOGGER.info("can not find job tenant{}.", tenantId);
                    throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION);
                }
                List<ActionJobEntityVO> engineEntities = actionService.entitys(Collections.singletonList(jobId));

                String engineJobId = "";
                if (CollectionUtils.isNotEmpty(engineEntities)) {
                    engineJobId = engineEntities.get(0).getEngineJobId();
                }
                final long startTime = Objects.isNull(job.getExecStartTime()) ? System.currentTimeMillis() : job.getExecStartTime().getTime();
                final String perf = StringUtils.isBlank(engineJobId) ? null : this.developServerLogService.formatPerfLogInfo(engineJobId, jobId, startTime, System.currentTimeMillis(), tenantById.getId());
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

                    final DevelopSelectSql developHiveSelectSql = this.developSelectSqlService.getByJobId(jobId, tenantId, 0);
                    if (developHiveSelectSql != null) {
                        logBuild.append("====================任务信息====================\n");
                        final String sqlLog = developHiveSelectSql.getCorrectSqlText().replaceAll("(\"password\"[^\"]+\")([^\"]+)(\")", "$1**$3");
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
                    resultVO.setDownload(String.format(DevelopJobService.DOWNLOAD_URL, jobId, EScheduleJobType.SYNC.getVal(), tenantId));
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
     *
     * @param userId
     * @param tenantId
     * @param taskId
     * @param sql
     * @param taskVariables
     * @return
     */
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String sql, List<Map> taskVariables) {
        ExecuteResultVO result = new ExecuteResultVO();
        try {
            Task task = developTaskService.getOneWithError(taskId);
            result.setTaskType(task.getTaskType());
            task.setSqlText(sql);

            //将SQL中的 系统参数和自定义参数 转换为DTO对象
            List<DevelopParamDTO> developParamDTOS = this.developTaskParamService.paramResolver(taskVariables);
            List<DevelopTaskParam> params = this.developTaskParamService.convertParam(developParamDTOS);
            List<DevelopTaskParamShade> taskParamsToReplace = this.developTaskParamService.convertShade(params);
            /*ParamTaskAction paramTaskAction = getParamTaskAction(task, userId, taskParamsToReplace);

            // 转换参数
            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
            sql = paramActionExt.getSqlText();
            String jobId = paramActionExt.getJobId();
            task.setTaskParams(paramActionExt.getTaskParams());*/
            String jobId = actionService.generateUniqueSign();
            ITaskService taskService = taskContext.get(task.getTaskType());
            result = taskService.startSqlImmediately(userId, tenantId, taskId, sql, task, jobId);
        } catch (Exception e) {
            LOGGER.warn("startSqlImmediately-->", e);
            result.setMsg(ExceptionUtil.getErrorMessage(e));
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
            this.developSelectSqlService.stopSelectJob(jobId, tenantId);
        }
    }

    public String getApplicationId(String jobId) {
        List<ActionJobEntityVO> engineEntities = actionService.entitys(Lists.newArrayList(jobId));
        if (CollectionUtils.isNotEmpty(engineEntities)) {
            return engineEntities.get(0).getApplicationId();
        }
        return "";
    }

    /**
     * 初始化engine paramActionExt 入参
     *
     * @param task
     * @param userId
     * @param taskParamsToReplace 需要替换的 系统参数和自定义参数
     * @return
     * @throws Exception
     */
    private ParamTaskAction getParamTaskAction(Task task, Long userId, List<DevelopTaskParamShade> taskParamsToReplace) throws Exception {
        ParamTaskAction paramTaskAction = new ParamTaskAction();

        //将 Task 对象转换为调度的 ScheduleTaskShade 对象
        ScheduleTaskShade scheduleTaskShade = new ScheduleTaskShade();
        BeanUtils.copyProperties(task, scheduleTaskShade);
        scheduleTaskShade.setTaskId(task.getId());
        scheduleTaskShade.setTaskType(task.getTaskType());

        //构建运行任务的完整命令(包含真正执行的SQL内容)
        String extraInfo = getExtraInfo(task, userId, taskParamsToReplace);

        JSONObject jsonObject = JSON.parseObject(extraInfo);
        if (jsonObject.containsKey("sqlText")) {
            jsonObject.put("sqlText", task.getSqlText());
        }
        extraInfo = jsonObject.toJSONString();
        scheduleTaskShade.setExtraInfo(extraInfo);
        paramTaskAction.setTask(scheduleTaskShade);
        return paramTaskAction;
    }

}

