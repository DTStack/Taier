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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.batch.MetricBuilder;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.util.DataFilter;
import com.dtstack.taier.common.util.JsonUtils;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.dto.DevelopTaskVersionDetailDTO;
import com.dtstack.taier.develop.common.convert.BinaryConversion;
import com.dtstack.taier.develop.dto.devlop.BatchServerLogVO;
import com.dtstack.taier.develop.dto.devlop.SyncStatusLogInfoVO;
import com.dtstack.taier.develop.enums.develop.YarnAppLogType;
import com.dtstack.taier.develop.service.schedule.TaskService;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.dtstack.taier.develop.utils.develop.service.impl.Engine2DTOService;
import com.dtstack.taier.develop.vo.develop.result.BatchServerLogByAppLogTypeResultVO;
import com.dtstack.taier.pluginapi.enums.ComputeType;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.dtstack.taier.scheduler.vo.action.ActionJobEntityVO;
import com.dtstack.taier.scheduler.vo.action.ActionLogVO;
import com.dtstack.taier.scheduler.vo.action.ActionRetryLogVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class DevelopServerLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopServerLogService.class);

    @Resource(name = "batchJobParamReplace")
    private JobParamReplace jobParamReplace;

    @Autowired
    private DevelopTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private DevelopDownloadService batchDownloadService;

    @Autowired
    private DevelopTaskVersionService batchTaskVersionService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private DevelopTaskService batchTaskService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    private ClusterService clusterService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String DOWNLOAD_LOG = "/taier/developDownload/downloadJobLog?jobId=%s&taskType=%s";
    private static final String DOWNLOAD_TYPE_LOG = "/taier/developDownload/downloadAppTypeLog?jobId=%s&logType=%s";


    private static final int SECOND_LENGTH = 10;
    private static final int MILLIS_LENGTH = 13;
    private static final int MICRO_LENGTH = 16;
    private static final int NANOS_LENGTH = 19;


    public BatchServerLogVO getLogsByJobId(String jobId, Integer pageInfo) {

        if (StringUtils.isBlank(jobId)) {
            return null;
        }

        final ScheduleJob job = scheduleJobService.getByJobId(jobId);
        if (Objects.isNull(job)) {
            LOGGER.info("can not find job by id:{}.", jobId);
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        final Long tenantId = job.getTenantId();

        final ScheduleTaskShade scheduleTaskShade = this.taskService.findTaskByTaskId(job.getTaskId());
        if (Objects.isNull(scheduleTaskShade)) {
            LOGGER.info("can not find task shade  by jobId:{}.", jobId);
            throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION);
        }

        final BatchServerLogVO batchServerLogVO = new BatchServerLogVO();

        //日志从engine获取
        final JSONObject logsBody = new JSONObject(2);
        logsBody.put("jobId", jobId);
        logsBody.put("computeType", ComputeType.BATCH.getType());
        ActionLogVO actionLogVO = actionService.log(jobId);
        JSONObject info = new JSONObject();
        if (!Strings.isNullOrEmpty(actionLogVO.getLogInfo())) {
            try {
                info = JSON.parseObject(actionLogVO.getLogInfo());
            } catch (final Exception e) {
                LOGGER.error(String.format("parse jobId： %s  logInfo：%s", jobId, actionLogVO.getLogInfo()), e);
                info.put("msg_info", actionLogVO.getLogInfo());
            }
        }

        if (Objects.nonNull(job.getVersionId())) {
            // 需要获取执行任务时候版本对应的sql
            DevelopTaskVersionDetailDTO taskVersion = this.batchTaskVersionService.getByVersionId((long) job.getVersionId());
            if (Objects.nonNull(taskVersion)) {
                if (StringUtils.isEmpty(taskVersion.getOriginSql())){
                    String jsonSql = StringUtils.isEmpty(taskVersion.getSqlText()) ? "{}" : taskVersion.getSqlText();
                    scheduleTaskShade.setSqlText(jsonSql);
                } else {
                    scheduleTaskShade.setSqlText(taskVersion.getOriginSql());
                }
            }

        }

        info.put("status", job.getStatus());
        if (EScheduleJobType.SPARK_SQL.getVal().equals(scheduleTaskShade.getTaskType())) {
            // 处理sql注释，先把注释base64编码，再处理非注释的自定义参数
            String sql = SqlFormatUtil.dealAnnotationBefore(scheduleTaskShade.getSqlText());
            final List<DevelopTaskParamShade> taskParamsToReplace = this.batchTaskParamShadeService.getTaskParam(scheduleTaskShade.getId());
            sql = this.jobParamReplace.paramReplace(sql, taskParamsToReplace, job.getCycTime());
            sql = SqlFormatUtil.dealAnnotationAfter(sql);
            info.put("sql", sql);
        } else if (EScheduleJobType.SYNC.getVal().equals(scheduleTaskShade.getTaskType())) {
            final JSONObject jobJson;
            //taskShade 需要解码
            JSONObject sqlJson = null;
            try {
                sqlJson = JSON.parseObject(scheduleTaskShade.getSqlText());
            } catch (final Exception e) {
                sqlJson = JSON.parseObject(scheduleTaskShade.getSqlText());
            }
            jobJson = sqlJson.getJSONObject("job");

            // 密码脱敏

            DataFilter.passwordFilter(jobJson);

            String jobStr = jobJson.toJSONString();
            final List<DevelopTaskParamShade> taskParamsToReplace = this.batchTaskParamShadeService.getTaskParam(scheduleTaskShade.getId());
            jobStr = this.jobParamReplace.paramReplace(jobStr, taskParamsToReplace, job.getCycTime());
            info.put("sql", JsonUtils.formatJSON(jobStr));
            if (Objects.nonNull(job.getExecEndTime()) && Objects.nonNull(job.getExecStartTime())) {
                List<ActionJobEntityVO> engineEntities = actionService.entitys(Collections.singletonList(logsBody.getString("jobId")));
                String engineJobId = "";
                if (CollectionUtils.isNotEmpty(engineEntities)) {
                    engineJobId =  engineEntities.get(0).getEngineJobId();
                }
                this.parseIncreInfo(info, jobStr, tenantId, engineJobId, job.getExecStartTime().getTime(), job.getExecEndTime().getTime(),"");
            }
        }

        if (job.getJobId() != null) {
            try {
                if (StringUtils.isNotBlank(actionLogVO.getEngineLog())) {
                    final Map<String, Object> engineLogMap = DevelopServerLogService.objectMapper.readValue(actionLogVO.getEngineLog(), Map.class);
                    this.dealPerfLog(engineLogMap);
                    info.putAll(engineLogMap);

                    // 去掉统计信息，界面不展示，调度端统计使用
                    info.remove("countInfo");
                }
            } catch (Exception e) {
                // 非json格式的日志也返回
                info.put("msg_info", actionLogVO.getEngineLog());
                LOGGER.error("", e);
            }
        }

        // 增加重试日志
        final String retryLog = this.buildRetryLog(jobId, pageInfo, batchServerLogVO);
        this.formatForLogInfo(info, job.getType(),scheduleTaskShade.getTaskType(), retryLog, null,
                null, null, batchServerLogVO, tenantId,jobId);

        if (!scheduleTaskShade.getTaskType().equals(EScheduleJobType.SYNC.getVal())
                && !scheduleTaskShade.getTaskType().equals(EScheduleJobType.VIRTUAL.getVal())
                && !scheduleTaskShade.getTaskType().equals(EScheduleJobType.WORK_FLOW.getVal())
                && TaskStatus.getStoppedStatus().contains(job.getStatus())) {
            batchServerLogVO.setDownloadLog(String.format(DOWNLOAD_LOG, jobId, scheduleTaskShade.getTaskType()));
        }

        batchServerLogVO.setName(scheduleTaskShade.getName());
        batchServerLogVO.setComputeType(scheduleTaskShade.getComputeType());
        batchServerLogVO.setTaskType(scheduleTaskShade.getTaskType());

        return batchServerLogVO;
    }


    /**
     * 处理性能指标日志
     *
     * @param engineLogMap
     */
    private void dealPerfLog(final Map<String, Object> engineLogMap) {

        if (!engineLogMap.containsKey("perf")) {
            return;
        }

        String str = (String) engineLogMap.get("perf");
        //转换成汉文加数字的字符串数组
        final String[] strings = str.split("\n");

        final StringBuilder temp = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            // //转换成汉文和数字的俩个元素的字符串数组
            final String[] s = strings[i].split("\t");
            if (i == 2 || i == 5) {
                s[1] = BinaryConversion.getPrintSize(Long.parseLong(s[1]), true);
            } else {
                s[1] = String.format("%,d", Long.parseLong(s[1]));
            }

            //字符串数组重新转换成字符串
            strings[i] = s[0] + "\t" + s[1];
            temp.append(strings[i]).append("\n");
        }

        str = temp.toString();
        engineLogMap.put("perf", str);
    }


    /**
     * 组装重试日志
     *
     * @param jobId
     * @param pageInfo         第几次的重试日志 如果传入0  默认是最新的  -1 展示所有的（为了兼容内部调用）
     * @param batchServerLogVO
     * @return
     */
    private String buildRetryLog(final String jobId, Integer pageInfo, final BatchServerLogVO batchServerLogVO) {
        final Map<String, Object> retryParamsMap = Maps.newHashMap();
        retryParamsMap.put("jobId", jobId);
        retryParamsMap.put("computeType", ComputeType.BATCH.getType());
        //先获取engine的日志总数信息
        List<ActionRetryLogVO> actionRetryLogVOs = actionService.retryLog(jobId);
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
        retryParamsMap.put("retryNum", pageInfo);
        //获取对应的日志
        ActionRetryLogVO retryLogContent = actionService.retryLogDetail(jobId, pageInfo);
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
     * 解析增量同步信息
     */
    private void parseIncreInfo(final JSONObject info, final String job, final Long tenantId, final String jobId, final long startTime, final long endTime, final String taskParams) {
        if (StringUtils.isEmpty(jobId)) {
            return;
        }
        try {
            final EDeployMode deployModeEnum = TaskParamsUtils.parseDeployTypeByTaskParams(taskParams,ComputeType.BATCH.getType());
            JSONObject flinkJsonObject = Engine2DTOService.getComponentConfig(tenantId, EComponentType.FLINK);
            final String prometheusHost = flinkJsonObject.getJSONObject(deployModeEnum.name()).getString("prometheusHost");
            final String prometheusPort = flinkJsonObject.getJSONObject(deployModeEnum.name()).getString("prometheusPort");
            //prometheus的配置信息 从控制台获取
            final PrometheusMetricQuery prometheusMetricQuery = new PrometheusMetricQuery(String.format("%s:%s", prometheusHost, prometheusPort));
            final IMetric startLocationMetric = MetricBuilder.buildMetric("startLocation", jobId, startTime, endTime, prometheusMetricQuery);
            final IMetric endLocationMetric = MetricBuilder.buildMetric("endLocation", jobId, startTime, endTime, prometheusMetricQuery);
            String startLocation = null;
            String endLocation = null;
            if (startLocationMetric != null) {
                startLocation = String.valueOf(startLocationMetric.getMetric());
            }
            if (Objects.nonNull(endLocationMetric)) {
                endLocation = String.valueOf(endLocationMetric.getMetric());
            }

            if (StringUtils.isBlank(job)) {
                return;
            }
            final JSONObject jobJson = JSON.parseObject(job);
            final String increColumn = (String) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.increColumn");

            final String table = (String) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.connection[0].table[0]");
            final StringBuilder increStrBuild = new StringBuilder();
            increStrBuild.append("数据表:  \t").append(table).append("\n");
            increStrBuild.append("增量标识:\t").append(increColumn).append("\n");

            if (StringUtils.isEmpty(endLocation) || endLocation.startsWith("-")) {
                increStrBuild.append("开始位置:\t").append("同步数据条数为0").append("\n");
                info.put("increInfo", increStrBuild.toString());
                return;
            }

            boolean isDateCol = false;

            final JSONArray columns = (JSONArray) JSONPath.eval(jobJson, "$.job.content[0].reader.parameter.column");
            for (final Object column : columns) {
                if (column instanceof JSONObject) {
                    final String name = ((JSONObject) column).getString("name");
                    if (name != null && name.equals(increColumn)) {
                        final String type = ((JSONObject) column).getString("type");
                        Boolean typeCheck = type != null && (type.matches("(?i)date|datetime|time") || type.toLowerCase().contains("timestamp"));
                        if (typeCheck) {
                            isDateCol = true;
                        }
                    }
                }
            }

            if (StringUtils.isEmpty(startLocation)) {
                startLocation = "全量同步";
            }

            if (isDateCol) {
                startLocation = this.formatLongStr(startLocation);
                endLocation = this.formatLongStr(endLocation);
            }

            increStrBuild.append("开始位置:\t").append(startLocation).append("\n");
            increStrBuild.append("结束位置:\t").append(endLocation).append("\n");
            info.put("increInfo", increStrBuild.toString());
        } catch (Exception e) {
            LOGGER.warn("{}", e);
        }
    }

    private String formatLongStr(final String longStr) {
        if (StringUtils.isEmpty(longStr)) {
            return "";
        }

        if ("0".equalsIgnoreCase(longStr)){
            return "";
        }
        if(!NumberUtils.isNumber(longStr)){
            return longStr;
        }

        final long time = Long.parseLong(longStr);

        final Timestamp ts = new Timestamp(this.getMillis(time));
        ts.setNanos(this.getNanos(time));

        return this.getNanosTimeStr(ts.toString());
    }

    private String getNanosTimeStr(String timeStr) {
        if (timeStr.length() < 29) {
            timeStr += org.apache.commons.lang.StringUtils.repeat("0", 29 - timeStr.length());
        }

        return timeStr;
    }

    private int getNanos(final long startLocation) {
        final String timeStr = String.valueOf(startLocation);
        final int nanos;
        if (timeStr.length() == DevelopServerLogService.SECOND_LENGTH) {
            nanos = 0;
        } else if (timeStr.length() == DevelopServerLogService.MILLIS_LENGTH) {
            nanos = Integer.parseInt(timeStr.substring(DevelopServerLogService.SECOND_LENGTH, DevelopServerLogService.MILLIS_LENGTH)) * 1000000;
        } else if (timeStr.length() == DevelopServerLogService.MICRO_LENGTH) {
            nanos = Integer.parseInt(timeStr.substring(DevelopServerLogService.SECOND_LENGTH, DevelopServerLogService.MICRO_LENGTH)) * 1000;
        } else if (timeStr.length() == DevelopServerLogService.NANOS_LENGTH) {
            nanos = Integer.parseInt(timeStr.substring(DevelopServerLogService.SECOND_LENGTH, DevelopServerLogService.NANOS_LENGTH));
        } else {
            throw new IllegalArgumentException("Unknown time unit:startLocation=" + startLocation);
        }

        return nanos;
    }

    private long getMillis(final long startLocation) {
        final String timeStr = String.valueOf(startLocation);
        final long millisSecond;
        if (timeStr.length() == DevelopServerLogService.SECOND_LENGTH) {
            millisSecond = startLocation * 1000;
        } else if (timeStr.length() == DevelopServerLogService.MILLIS_LENGTH) {
            millisSecond = startLocation;
        } else if (timeStr.length() == DevelopServerLogService.MICRO_LENGTH) {
            millisSecond = startLocation / 1000;
        } else if (timeStr.length() == DevelopServerLogService.NANOS_LENGTH) {
            millisSecond = startLocation / 1000000;
        } else {
            throw new IllegalArgumentException("Unknown time unit:startLocation=" + startLocation);
        }

        return millisSecond;
    }

    public BatchServerLogVO.SyncJobInfo parseExecLog(final String perf, final Long execTime) {
        if (Strings.isNullOrEmpty(perf)) {
            return new BatchServerLogVO.SyncJobInfo();
        }

        final String[] arr = perf.split("\\n");
        final BatchServerLogVO.SyncJobInfo syncJobInfo = new BatchServerLogVO.SyncJobInfo();
        Integer readNum = 0;
        Integer errorNum = 0;
        Integer writeNum = 0;

        for (final String tmp : arr) {
            if (tmp.contains("读取记录数:")) {
                readNum = this.parseNumFromLog(tmp);
            } else if (tmp.contains("错误记录数:")) {
                errorNum = this.parseNumFromLog(tmp);
            } else if (tmp.contains("写入记录数:")) {
                writeNum = this.parseNumFromLog(tmp);
            }
        }

        syncJobInfo.setReadNum(readNum);
        syncJobInfo.setWriteNum(writeNum);
        syncJobInfo.setExecTime(execTime);
        if (errorNum == null || readNum == null) {
            syncJobInfo.setDirtyPercent(0F);
        } else {
            if (readNum == 0) {
                syncJobInfo.setDirtyPercent(0F);
            } else {
                syncJobInfo.setDirtyPercent(Float.valueOf(errorNum) / Float.valueOf(readNum) * 100);
            }
        }
        return syncJobInfo;
    }

    private Integer parseNumFromLog(final String tmp) {
        return MathUtil.getIntegerVal(tmp.split("\t")[1].trim().replace(",", ""));
    }

    private void formatForLogInfo(final JSONObject jobInfo, final Integer jobType, final Integer taskType, final String retryLog, final Timestamp startTime,
                                  final Timestamp endTime, final Long execTime, final BatchServerLogVO batchServerLogVO, final Long tenantId, final String jobId) {
        if (!taskType.equals(EScheduleJobType.SYNC.getVal())) {
            if (jobInfo.containsKey("engineLogErr")) {
                // 有这个字段表示日志没有获取到，目前engine端只对flink任务做了这种处理，这里先提前加上
                jobInfo.put("msg_info", jobInfo.getString("engineLogErr"));
            } else {
                final String msgInfo = Optional.ofNullable(jobInfo.getString("msg_info")).orElse("");
                jobInfo.put("msg_info", msgInfo + "\n" + retryLog);
            }

            batchServerLogVO.setLogInfo(jobInfo.toJSONString());
            return;
        }

        this.formatForSyncLogInfo(jobInfo, jobType,retryLog, startTime, endTime, execTime, batchServerLogVO, tenantId,jobId);
    }

    private void formatForSyncLogInfo(final JSONObject jobInfo, final Integer jobType, final String retryLog, final Timestamp startTime,
                                      final Timestamp endTime, final Long execTime, final BatchServerLogVO batchServerLogVO, final Long tenantId, final String jobId) {

        try {
            final Map<String, Object> sqlInfoMap = (Map<String, Object>) DevelopServerLogService.objectMapper.readValue(jobInfo.getString("sql"), Object.class);
            final JSONObject res = new JSONObject();
            res.put("job", sqlInfoMap.get("job"));
            res.put("parser", sqlInfoMap.get("parser"));
            res.put("createModel", sqlInfoMap.get("createModel"));

            final Map<String, Object> jobInfoMap = (Map<String, Object>) DevelopServerLogService.objectMapper.readValue(jobInfo.toString(), Object.class);

            final JSONObject logInfoJson = new JSONObject();
            logInfoJson.put("jobid", jobInfoMap.get("jobid"));
            logInfoJson.put("msg_info", jobInfoMap.get("msg_info") + retryLog);
            logInfoJson.put("turncated", jobInfoMap.get("turncated"));
            if (jobInfoMap.get("ruleLogList") != null) {
                logInfoJson.put("ruleLogList", jobInfoMap.get("ruleLogList"));
            }

            String perfLogInfo = jobInfoMap.getOrDefault("perf", StringUtils.EMPTY).toString();
            final boolean parsePerfLog = startTime != null && endTime != null
                    && jobInfoMap.get("jobid") != null;

            if (parsePerfLog) {
                perfLogInfo = this.formatPerfLogInfo(jobInfoMap.get("jobid").toString(),jobId, startTime.getTime(), endTime.getTime(), tenantId);
            }

            logInfoJson.put("perf", perfLogInfo);
            //补数据没有增量标志信息
            if (EScheduleType.NORMAL_SCHEDULE.getType().equals(jobType)){
                logInfoJson.put("increInfo", jobInfo.getString("increInfo"));
            }
            logInfoJson.put("sql", res);

            String allExceptions = "";
            if (jobInfoMap.get("root-exception") != null) {
                allExceptions = jobInfoMap.get("root-exception").toString();
                if (!Strings.isNullOrEmpty(retryLog)) {
                    allExceptions += retryLog;
                }
            }

            // 如果没有拿到日志，并且有engineLogErr属性，可能是flink挂了
            if (StringUtils.isEmpty(allExceptions.trim()) && jobInfoMap.containsKey("engineLogErr")) {
                if (!TaskStatus.FINISHED.getStatus().equals(Integer.valueOf(jobInfoMap.get("status").toString()))) {
                    //成功默认为空
                    allExceptions = jobInfoMap.get("engineLogErr").toString();
                } else {
                    allExceptions = "";
                }

            }

            logInfoJson.put("all-exceptions", allExceptions);
            logInfoJson.put("status", jobInfoMap.get("status"));

            batchServerLogVO.setLogInfo(logInfoJson.toString());

            //解析出数据同步的信息
            final BatchServerLogVO.SyncJobInfo syncJobInfo = this.parseExecLog(perfLogInfo, execTime);
            batchServerLogVO.setSyncJobInfo(syncJobInfo);
        } catch (final Exception e) {
            LOGGER.error("logInfo 解析失败", e);
            batchServerLogVO.setLogInfo(jobInfo.toString());
        }
    }


    public String formatPerfLogInfo(final String engineJobId, final String jobId, final long startTime, final long endTime, final Long tenantId) {

        final ScheduleJob job = scheduleJobService.getByJobId(jobId);
        if (Objects.isNull(job)) {
            LOGGER.info("can not find job by id:{}.", jobId);
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_JOB);
        }
        if (job.getTaskId() == null || job.getTaskId() == -1){
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_TASK);
        }
        Task batchTaskById = batchTaskService.getBatchTaskById(job.getTaskId());
        //prometheus的配置信息 从控制台获取
        final Pair<String, String> prometheusHostAndPort = this.getPrometheusHostAndPort(tenantId,batchTaskById.getTaskParams(),ComputeType.BATCH);
        if (prometheusHostAndPort == null){
            return "promethues配置为空";
        }
        final PrometheusMetricQuery prometheusMetricQuery = new PrometheusMetricQuery(String.format("%s:%s", prometheusHostAndPort.getKey(), prometheusHostAndPort.getValue()));

        //之后查询是可以直接获取最后一条记录的方法
        //防止数据同步执行时间太长 查询prometheus的时候返回exceeded maximum resolution of 11,000 points per timeseries
        final long maxGapTime = 60 * 1000 * 60 * (long)8;
        long gapStartTime = startTime;
        if (endTime - startTime >= maxGapTime) {
            //超过11,000 points 查询1小时间隔内
            gapStartTime = endTime - 60 * 1000 * 60;
        }

        final IMetric numReadMetric = MetricBuilder.buildMetric("numRead", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final IMetric byteReadMetric = MetricBuilder.buildMetric("byteRead", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final IMetric readDurationMetric = MetricBuilder.buildMetric("readDuration", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final IMetric numWriteMetric = MetricBuilder.buildMetric("numWrite", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final IMetric byteWriteMetric = MetricBuilder.buildMetric("byteWrite", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final IMetric writeDurationMetric = MetricBuilder.buildMetric("writeDuration", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final IMetric numErrorMetric = MetricBuilder.buildMetric("nErrors", engineJobId, gapStartTime, endTime, prometheusMetricQuery);
        final SyncStatusLogInfoVO formatPerfLogInfo = this.getFormatPerfLogInfo(numReadMetric, byteReadMetric, readDurationMetric, numWriteMetric, byteWriteMetric, writeDurationMetric, numErrorMetric);
        return formatPerfLogInfo.buildReadableLog();
    }

    public Pair<String,String> getPrometheusHostAndPort(final Long tenantId, final String taskParams,ComputeType computeType){
        Boolean hasStandAlone = clusterService.hasStandalone(tenantId, EComponentType.FLINK.getTypeCode());
        JSONObject flinkJsonObject ;
        if (hasStandAlone) {
            flinkJsonObject = clusterService.getConfigByKey(tenantId, EComponentType.FLINK.getConfName(), null);
        }else {

            JSONObject jsonObject = Engine2DTOService.getComponentConfig(tenantId, EComponentType.FLINK);
            if (null == jsonObject) {
                LOGGER.info("console tenantId {} pluginInfo is null", tenantId);
                return null;
            }
            EDeployMode deployModeEnum = TaskParamsUtils.parseDeployTypeByTaskParams(taskParams,computeType.getType());
            flinkJsonObject = jsonObject.getJSONObject(deployModeEnum.name().toLowerCase(Locale.ROOT));
        }
        String prometheusHost = flinkJsonObject.getString("prometheusHost");
        String prometheusPort = flinkJsonObject.getString("prometheusPort");
        if (StringUtils.isBlank(prometheusHost) || StringUtils.isBlank(prometheusPort)) {
            LOGGER.info("prometheus http info is blank prometheusHost：{} prometheusPort：{}", prometheusHost, prometheusPort);
            return null;
        }
        return new Pair<>(prometheusHost,prometheusPort);
    }


    private SyncStatusLogInfoVO getFormatPerfLogInfo(final IMetric numReadMetric, final IMetric byteReadMetric, final IMetric readDurationMetric,
                                                     final IMetric numWriteMetric, final IMetric byteWriteMetric, final IMetric writeDurationMetric,
                                                     final IMetric numErrorMetric){
        final SyncStatusLogInfoVO logInfoVO = new SyncStatusLogInfoVO();
        if (numReadMetric != null){
            logInfoVO.setNumRead(this.getLongValue(numReadMetric.getMetric()));
        }
        if (byteReadMetric != null){
            logInfoVO.setByteRead(this.getLongValue(byteReadMetric.getMetric()));
        }
        if (readDurationMetric != null){
            logInfoVO.setReadDuration(this.getLongValue(readDurationMetric.getMetric()));
        }
        if (numWriteMetric != null){
            logInfoVO.setNumWrite(this.getLongValue(numWriteMetric.getMetric()));
        }
        if (byteWriteMetric != null){
            logInfoVO.setByteWrite(this.getLongValue(byteWriteMetric.getMetric()));
        }
        if (writeDurationMetric != null){
            logInfoVO.setWriteDuration(this.getLongValue(writeDurationMetric.getMetric()));
        }
        if (numErrorMetric != null){
            logInfoVO.setnErrors(getLongValue(numErrorMetric.getMetric()));
        }
        return logInfoVO;
    }


    private long getLongValue(final Object obj) {
        if (obj == null) {
            return 0L;
        }

        return Long.valueOf(obj.toString());
    }


    public JSONObject getLogsByAppId(Long tenantId, Integer taskType, String jobId) {
        if (EScheduleJobType.SYNC.getVal().equals(taskType)
                || EScheduleJobType.VIRTUAL.getVal().equals(taskType)
                || EScheduleJobType.WORK_FLOW.getVal().equals(taskType)) {
            throw new RdosDefineException("数据同步、虚节点、工作流的任务日志不支持下载");
        }
        final JSONObject result = new JSONObject(YarnAppLogType.values().length);
        for (final YarnAppLogType type : YarnAppLogType.values()) {
            final String msg = this.batchDownloadService.downloadAppTypeLog(tenantId, jobId, 100,
                    type.name().toUpperCase(), taskType);
            final JSONObject typeLog = new JSONObject(2);
            typeLog.put("msg", msg);
            typeLog.put("download", String.format(DevelopServerLogService.DOWNLOAD_TYPE_LOG, jobId, type.name().toUpperCase()));
            result.put(type.name(), typeLog);
        }

        return result;
    }

    public BatchServerLogByAppLogTypeResultVO getLogsByAppLogType(Long tenantId, Integer taskType, String jobId, String logType) {
        if (EScheduleJobType.SYNC.getVal().equals(taskType)
                || EScheduleJobType.VIRTUAL.getVal().equals(taskType)
                || EScheduleJobType.WORK_FLOW.getVal().equals(taskType)) {
            throw new RdosDefineException("数据同步、虚节点、工作流的任务日志不支持下载");
        }

        if (YarnAppLogType.getType(logType) == null) {
            throw new RdosDefineException("not support the logType:" + logType);
        }

        final String msg = this.batchDownloadService.downloadAppTypeLog(tenantId, jobId, 100,
                logType.toUpperCase(), taskType);
        BatchServerLogByAppLogTypeResultVO resultVO = new BatchServerLogByAppLogTypeResultVO();
        resultVO.setMsg(msg);
        resultVO.setDownload(String.format(DevelopServerLogService.DOWNLOAD_TYPE_LOG, jobId, logType));

        return resultVO;
    }

}
