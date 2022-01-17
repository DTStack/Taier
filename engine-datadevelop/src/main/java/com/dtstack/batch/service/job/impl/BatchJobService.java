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

package com.dtstack.batch.service.job.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.dao.BatchTaskDao;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.BatchTaskParam;
import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.dto.BatchParamDTO;
import com.dtstack.batch.enums.EScheduleType;
import com.dtstack.batch.mapping.TaskTypeEngineTypeMapping;
import com.dtstack.batch.schedule.JobParamReplace;
import com.dtstack.batch.service.console.TenantService;
import com.dtstack.batch.service.impl.BatchServerLogService;
import com.dtstack.batch.service.impl.MultiEngineServiceFactory;
import com.dtstack.batch.service.job.IBatchJobExeService;
import com.dtstack.batch.service.schedule.JobService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.service.task.impl.BatchTaskParamService;
import com.dtstack.batch.service.task.impl.BatchTaskParamShadeService;
import com.dtstack.batch.service.task.impl.BatchTaskResourceShadeService;
import com.dtstack.batch.service.user.UserService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.SyncStatusLogInfoVO;
import com.dtstack.batch.web.job.vo.result.BatchGetSyncTaskStatusInnerResultVO;
import com.dtstack.batch.web.job.vo.result.BatchStartSyncResultVO;
import com.dtstack.engine.common.constrant.TaskStatusConstrant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.enums.AppType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.enums.TaskStatus;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.JsonUtils;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.SessionUtil;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.impl.pojo.ParamTaskAction;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleTaskShade;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.master.service.ScheduleActionService;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.impl.pojo.ParamTaskAction;
import com.dtstack.engine.master.service.ScheduleJobService;
import com.dtstack.engine.master.service.ScheduleTaskShadeService;
import com.dtstack.engine.master.vo.ScheduleJobExeStaticsVO;
import com.dtstack.engine.master.vo.action.ActionJobEntityVO;
import com.dtstack.engine.master.vo.action.ActionLogVO;
import com.dtstack.engine.pluginapi.enums.ComputeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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

    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String ADD_JAR_WITH = "ADD JAR WITH %s AS ;";

    private static final String DOWNLOAD_URL = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s&tenantId=%s";

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private BatchTaskResourceShadeService batchTaskResourceShadeService;

    @Autowired
    private BatchServerLogService batchServerLogService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Resource(name = "batchJobParamReplace")
    private JobParamReplace jobParamReplace;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private BatchTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private JobService jobService;

    private static final String IS_CHECK_DDL_KEY = "isCheckDDL";


    public String updateStatusById(String jobId, Integer status) {
        BatchJobService.logger.info("jobId:{} status:{}", jobId, status);
        this.scheduleJobService.updateJobStatusAndLogInfo(jobId, status, "");
        return jobId;
    }


    public String updateStatus(String jobId, Integer status, String msg) {
        BatchJobService.logger.info("jobId:{} status:{} msg:{}", jobId, status, msg);
        this.scheduleJobService.updateJobStatusAndLogInfo(jobId, status, "");
        return jobId;
    }

    /**
     * 构建运行任务的完整命令(包含真正执行的SQL内容)
     * @param batchTask
     * @param userId
     * @param taskParamsToReplace SQL中需要匹配和替换的 系统参数与自定义参数
     * @return info信息
     * @throws Exception
     */
    public String getExtraInfo(BatchTask batchTask, Long userId, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        //根据任务类型获取引擎类型
        MultiEngineType multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(batchTask.getTaskType());
        if (Objects.isNull(multiEngineType)) {
            throw new RdosDefineException(ErrorCode.COMPONENT_TYPE_UNDEFINED);
        }

        //任务参数若为null，则表示是提交任务，否则就是临时运行任务
        if(taskParamsToReplace == null){
            taskParamsToReplace = this.batchTaskParamShadeService.getTaskParam(batchTask.getId());
        }
        IBatchJobExeService jobExecuteService = this.multiEngineServiceFactory.getBatchJobExeService(multiEngineType.getType());

        //构建任务运行完整信息
        final Map<String, Object> actionParam = Maps.newHashMap();
        //构建 sqlText、taskParams，如果是数据同步任务，则根据id替换数据源
        jobExecuteService.readyForTaskStartTrigger(actionParam, batchTask.getTenantId(), batchTask, taskParamsToReplace);

        actionParam.put("taskId", batchTask.getId());
        actionParam.put("taskType", EJobType.getEngineJobType(batchTask.getTaskType()));
        actionParam.put("name", batchTask.getName());
        actionParam.put("computeType", batchTask.getComputeType());
        actionParam.put("tenantId", batchTask.getTenantId());
        actionParam.put("isFailRetry", false);
        actionParam.put("maxRetryNum", 0);
        actionParam.put("multiEngineType", multiEngineType.getType());
        actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));

        User user;
        if (userId == null) {
            user = userService.getById(batchTask.getOwnerUserId());
        } else {
            user = userService.getById(userId);
        }
        if (user != null) {
            actionParam.put("userId", user.getId());
        } else {
            throw new RdosDefineException(String.format("当前用户已被移除，userId：%d", userId == null ? batchTask.getOwnerUserId() : userId));
        }
        // 出错重试配置,兼容之前的任务，没有这个参数则默认重试
        final JSONObject scheduleConf = JSON.parseObject(batchTask.getScheduleConf());
        if (scheduleConf.containsKey("isFailRetry")) {
            actionParam.put("isFailRetry", scheduleConf.getBooleanValue("isFailRetry"));
            if (scheduleConf.getBooleanValue("isFailRetry")) {
                final int maxRetryNum = scheduleConf.getIntValue("maxRetryNum") == 0 ? 3 : scheduleConf.getIntValue("maxRetryNum");
                actionParam.put("maxRetryNum", maxRetryNum);
            } else {
                actionParam.put("maxRetryNum", 0);
            }
        }
        String extraInfo = objMapper.writeValueAsString(actionParam);
        extraInfo = extraInfo.replaceAll("\r\n", System.getProperty("line.separator"));
        return extraInfo;
    }


    public String stopJob(long jobId, Long userId, Boolean isRoot) {

        final ScheduleJob ScheduleJob = this.scheduleJobService.getById(jobId);
        if (ScheduleJob == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }

        this.checkJobOperateValid(ScheduleJob, userId, ScheduleJob.getTenantId(), isRoot);
        this.scheduleJobService.stopJob(jobId, AppType.RDOS.getType());
        return "success";
    }

    /**
     * 校验实例的操作权限
     * （主要针对跨项目实例）
     *
     * @param ScheduleJob
     * @param userId
     * @param tenantId
     * @param isRoot
     */
    private void checkJobOperateValid(final ScheduleJob ScheduleJob, final Long userId, final Long tenantId, final Boolean isRoot) {
        final ScheduleTaskShade task = scheduleTaskShadeService.getByTaskId(ScheduleJob.getTaskId());
        if (task != null) {
//            this.roleUserService.checkUserRole(userId, RoleValue.OPERATION.getRoleValue(), ErrorCode.PERMISSION_LIMIT.getDescription(), projectId, tenantId, isRoot);
        }
    }

    /**
     * 运行同步任务
     * TODO 内容也需要迁移出去
     *
     * @return
     */
    public BatchStartSyncResultVO startSyncImmediately(Long taskId, Long userId, Boolean isRoot, Long tenantId, String taskParams) {
        BatchStartSyncResultVO batchStartSyncResultVO = new BatchStartSyncResultVO();
        batchStartSyncResultVO.setMsg(null);
        batchStartSyncResultVO.setJobId(null);
        batchStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());

        final BatchTask batchTask = this.batchTaskDao.getOne(taskId);
        if (batchTask == null) {
            throw new RdosDefineException("can not find task by id:" + taskId);
        }

        if (!batchTask.getTaskType().equals(EJobType.SYNC.getVal())) {
            throw new RdosDefineException("只支持同步任务直接运行");
        }

        try {

            final IBatchJobExeService batchJobExeService = this.multiEngineServiceFactory.getBatchJobExeService(MultiEngineType.HADOOP.getType());
            final Map<String, Object> actionParam = batchJobExeService.readyForSyncImmediatelyJob(batchTask, tenantId, isRoot);
            String extroInfo = JSON.toJSONString(actionParam);
            ParamTaskAction paramTaskAction = new ParamTaskAction();
            ScheduleTaskShade scheduleTaskShade = JSON.parseObject(extroInfo, ScheduleTaskShade.class);
            JSONObject extroInfoObj = JSON.parseObject(extroInfo);
            extroInfoObj.put("engineType", EngineType.Flink.getEngineName());
            scheduleTaskShade.setExtraInfo(JSON.toJSONString(extroInfoObj));
            scheduleTaskShade.setTaskId(batchTask.getId());
            scheduleTaskShade.setScheduleConf(batchTask.getScheduleConf());
            scheduleTaskShade.setComponentVersion(batchTask.getComponentVersion());
            paramTaskAction.setBatchTask(scheduleTaskShade);
            ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getBatchTask(),paramTaskAction.getJobId(),paramTaskAction.getFlowJobId());
            String jobId = paramActionExt.getJobId();
            actionService.start(paramActionExt);
            String name = MathUtil.getString(actionParam.get("name"));
            String job = MathUtil.getString(actionParam.get("job"));
            this.batchSelectSqlService.addSelectSql(jobId, name, TempJobType.SYNC_TASK.getType(), batchTask.getTenantId(),
                    job, userId, MultiEngineType.HADOOP.getType());

            batchStartSyncResultVO.setMsg("任务提交成功,名称为:" + name);
            batchStartSyncResultVO.setJobId(jobId);
            batchStartSyncResultVO.setStatus(TaskStatus.SUBMITTING.getStatus());

        } catch (final Exception e) {
            BatchJobService.logger.warn("startSyncImmediately-->", e);

            batchStartSyncResultVO.setMsg(e.getMessage());
            batchStartSyncResultVO.setStatus(TaskStatus.SUBMITFAILD.getStatus());
        }

        return batchStartSyncResultVO;
    }

    /**
     * 获取同步任务运行状态
     */
    public BatchGetSyncTaskStatusInnerResultVO getSyncTaskStatus(Long tenantId, String jobId, Long userId) {
        return this.getSyncTaskStatusInner(tenantId, jobId, userId, 0);
    }

    private BatchGetSyncTaskStatusInnerResultVO getSyncTaskStatusInner(final Long tenantId, final String jobId, final Long userId, int retryTimes) {
        final BatchGetSyncTaskStatusInnerResultVO resultVO = new BatchGetSyncTaskStatusInnerResultVO();
        resultVO.setMsg(null);
        resultVO.setStatus(TaskStatus.RUNNING.getStatus());

        try {
            ScheduleJob job = jobService.getScheduleJob(jobId);
            if (job == null) {
                resultVO.setMsg("无法获取engine数据");
                return resultVO;
            }

            final Integer status = TaskStatusConstrant.getShowStatus(job.getStatus());
            resultVO.setStatus(status);
            if (TaskStatus.RUNNING.getStatus().equals(status)) {
                resultVO.setMsg("运行中");
            }

            final JSONObject logsBody = new JSONObject(2);
            logsBody.put("jobId", jobId);
            logsBody.put("jobIds", Lists.newArrayList(jobId));
            logsBody.put("computeType", ComputeType.BATCH.getType());
            ActionLogVO actionLogVO = actionService.log(jobId, ComputeType.BATCH.getType());
            String engineLogStr = actionLogVO.getEngineLog();
            final String logInfoStr = actionLogVO.getLogInfo();
            if(StringUtils.isNotBlank(engineLogStr)){
                //移除increConf 信息
                try {
                    final JSONObject engineLogJson = JSON.parseObject(engineLogStr);
                    engineLogJson.remove("increConf");
                    engineLogStr = engineLogJson.toJSONString();
                } catch (final Exception e) {
                    logger.error("", e);
                    if (TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.CANCELED.getStatus().equals(status)
                            || TaskStatus.FAILED.getStatus().equals(status)) {
                        resultVO.setMsg(engineLogStr);
                        resultVO.setDownload(String.format(BatchJobService.DOWNLOAD_URL, jobId, EJobType.SYNC.getVal(), tenantId));
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
                final Tenant tenantById = this.tenantService.getTenantById(tenantId);
                if (tenantById == null) {
                    BatchJobService.logger.info("can not find job tenent{}.", tenantId);
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
                            return this.getSyncTaskStatusInner(tenantId, jobId, userId, retryTimes);
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

                    final BatchHiveSelectSql batchHiveSelectSql = this.batchSelectSqlService.getByJobId(jobId, tenantId, 0);
                    if (batchHiveSelectSql != null) {
                        logBuild.append("====================任务信息====================\n");
                        final String sqlLog=batchHiveSelectSql.getCorrectSqlText().replaceAll("(\"password\"[^\"]+\")([^\"]+)(\")","$1**$3");
                        logBuild.append(JsonUtils.formatJSON(sqlLog));
                        logBuild.append("\n");
                    }
                } else if (TaskStatus.FINISHED.getStatus().equals(status) && retryTimes < 3) {
                    // FIXME perjob模式运行任务，任务完成后统计信息可能还没收集到，这里等待1秒后再请求一次结果
                    Thread.sleep(1000);
                    return this.getSyncTaskStatusInner(tenantId, jobId, userId, 3);
                }
                if (TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.CANCELED.getStatus().equals(status)
                        || TaskStatus.FAILED.getStatus().equals(status)) {
                    resultVO.setDownload(String.format(BatchJobService.DOWNLOAD_URL, jobId, EJobType.SYNC.getVal(), tenantId));
                }
                resultVO.setMsg(logBuild.toString());
            } catch (final Exception e) {
                // 日志解析失败，可能是任务失败，日志信息为非json格式
                BatchJobService.logger.error("", e);
                resultVO.setMsg(StringUtils.isEmpty(engineLogStr) ? "engine调度失败" : engineLogStr);
            }
        } catch (final Exception e) {
            BatchJobService.logger.error("获取同步任务状态失败", e);
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
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String uniqueKey, String sql, List<Map> taskVariables, String dtToken, Integer isCheckDDL, Boolean isRoot,  Boolean isEnd) {

        // 更新ddl检查设置
        if (!Objects.isNull(isCheckDDL)) {
            SessionUtil.setValue(dtToken, BatchJobService.IS_CHECK_DDL_KEY, isCheckDDL);
        }
        final User user = userService.getById(userId);
        dtToken = String.format("%s;dt_user_id=%s;dt_username=%s;", dtToken, user.getId(), user.getUserName());
        ExecuteResultVO result = new ExecuteResultVO();
        MultiEngineType multiEngineType = null;
        try {
            final BatchTask task = this.batchTaskDao.getOne(taskId);
            if (task == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
            }
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

            multiEngineType = TaskTypeEngineTypeMapping.getEngineTypeByTaskType(task.getTaskType());
            final IBatchJobExeService batchJobService = this.multiEngineServiceFactory.getBatchJobExeService(multiEngineType.getType());
            result = batchJobService.startSqlImmediately(userId, tenantId, uniqueKey, taskId, sql, isRoot, task, dtToken, isEnd, jobId);
        } catch (final Exception e) {
            BatchJobService.logger.warn("startSqlImmediately-->", e);
            result.setMsg(e.getMessage());
            result.setStatus(TaskStatus.FAILED.getStatus());
            result.setSqlText(sql);
            return result;
        } finally {
            if (null != multiEngineType && Objects.nonNull(result)) {
                result.setEngineType(multiEngineType.getType());
            }
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


    /**
     * 运行报告
     *
     * @param taskId
     * @param count
     * @param tenantId
     * @return
     */
    //FIXME 任务类型只统计了完成和失败的。。然后其他状态呢？运行中。。提交中。。。
    public ScheduleJobExeStaticsVO statisticsTaskRecentInfo(Long taskId, int count, Long tenantId)  {
        final BatchTask task = this.batchTaskDao.getOne(taskId);
        if (task == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }

        final ScheduleJobExeStaticsVO result = new ScheduleJobExeStaticsVO();
        result.setTaskType(task.getTaskType());
        Integer fillDataNum = 0;
        Integer cronNum = 0;
        Integer failNum = 0;

        final List<Map<String, Object>> resultMap = this.scheduleJobService.statisticsTaskRecentInfo(taskId, AppType.RDOS.getType(), tenantId, count);
        for ( Map<String, Object> map : resultMap) {
            Object execStartTime = map.getOrDefault("execStartTime","0");
            Object execEndTime = map.getOrDefault("execEndTime","0");
            String jobId = MapUtils.getString(map, "jobId");
            Object application = jobId;
            //需要转化出ApplicationId
            JSONObject logsBody = new JSONObject(2);
            logsBody.put("jobId", jobId);
            logsBody.put("computeType", ComputeType.BATCH.getType());

            ActionLogVO log = actionService.log(jobId, ComputeType.BATCH.getType());
            if (null != log){
                JSONObject logInfo =  log.getLogInfo() != null && log.getLogInfo().contains("jobid") ? JSONObject.parseObject(log.getLogInfo()) : null;
                if (logInfo != null){
                    application = logInfo.getOrDefault("jobid",jobId);
                }
            }

            final Object status = map.get("status");
            final Object execTime = map.get("execTime");
            final Integer type = MathUtil.getIntegerVal(map.get("type"));

            if (EScheduleType.NORMAL_SCHEDULE.getType() == type) {
                cronNum++;
            } else {
                fillDataNum++;
            }
            if (status != null) {
                final Integer statusVal = MathUtil.getIntegerVal(status);
                if (statusVal.equals(TaskStatus.FINISHED.getStatus())) {
                    final ScheduleJobExeStaticsVO.BatchJobInfo vo = new ScheduleJobExeStaticsVO.BatchJobInfo();
                    vo.setJobId(MathUtil.getString(jobId));
                    vo.setExeTime(MathUtil.getIntegerVal(execTime));
                    vo.setExeStartTime(MathUtil.getLongVal(execStartTime));

                    if (EJobType.SYNC.getVal().equals(task.getTaskType())) {
                        //需要添加读取数据条数和脏数据信息
                        try {
                            /**
                             * engine_job表中得log_info字段获取jobid从prometheus中取任务执行信息
                             */
                            final SyncStatusLogInfoVO syncJobLogInfo = this.batchServerLogService.getSyncJobLogInfo(application.toString(),taskId,  Long.parseLong(execStartTime.toString()), Long.parseLong( execEndTime.toString()), this.tenantService.getDtuicTenantId(task.getTenantId()));
                            vo.setTotalCount(Math.toIntExact(syncJobLogInfo.getNumRead()));
                            vo.setDirtyNum(Math.toIntExact(syncJobLogInfo.getNErrors()));
                        } catch (final Exception e) {
                            BatchJobService.logger.error("", e);
                        }
                    }
                    result.addBatchJob(vo);
                } else if (statusVal.equals(TaskStatus.FAILED.getStatus())) {
                    failNum++;
                }
            }
        }

        result.setFillDataExeNum(fillDataNum);
        result.setCronExeNum(cronNum);
        result.setFailNum(failNum);

        return result;
    }

    public List<String> listJobIdByTaskNameAndStatusList(String taskName, List<Integer> statusList, Long tenantId) {
        return this.scheduleJobService.listJobIdByTaskNameAndStatusList(taskName, statusList, tenantId, AppType.RDOS.getType());
    }


    /**
     * 返回这些jobId对应的父节点的jobMap
     *
     * @param jobIdList
     * @param tenantId
     * @return
     */
    public Map<String, ScheduleJob> getLabTaskRelationMap(List<String> jobIdList, Long tenantId) {
        return this.scheduleJobService.getLabTaskRelationMap(jobIdList, tenantId);
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

