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

package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.impl.DevelopScriptService;
import com.dtstack.taier.develop.service.develop.impl.JobParamReplace;
import com.dtstack.taier.develop.service.schedule.JobExpandService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.service.task.TaskTemplateService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.develop.utils.develop.hive.service.LogPluginDownload;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-14 16:28
 */
@Component
public class ScriptTaskRunner implements ITaskRunner {

    @Autowired
    private DevelopScriptService developScriptService;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    protected JobService jobService;

    @Autowired
    protected JobExpandService jobExpandService;

    @Autowired
    protected EnvironmentContext environmentContext;

    @Autowired
    protected ClusterService clusterService;

    @Override
    public List<EScheduleJobType> support() {
        return ImmutableList.of(EScheduleJobType.PYTHON, EScheduleJobType.SHELL);
    }

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariables) throws Exception {
        task.setTaskParams(TaskTemplateService.formatEnvTaskParams(task.getTaskParams()));
        sql = jobParamReplace.paramReplace(sql, taskVariables, DateTime.now().toString("yyyyMMddHHmmss"));
        return developScriptService.runScriptWithTask(userId, tenantId, sql, task);
    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        ExecuteResultVO executeResultVO = new ExecuteResultVO(selectSql.getJobId());
        executeResultVO.setStatus(getSchedulerStatus(selectSql));
        return executeResultVO;
    }

    @Override
    public ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        ExecuteResultVO resultVO = new ExecuteResultVO();
        StringBuilder log = new StringBuilder();
        IDownload download = logDownLoad(tenantId, jobId, Objects.isNull(limitNum) ? environmentContext.getLogsLimitNum() : limitNum);
        if (Objects.nonNull(download)) {
            LOGGER.error("-----download log file error-----");
            while (!download.reachedEnd()) {
                Object row = download.readNext();
                log.append(row);
            }
        } else {
            log.append(scheduleRunLog(jobId));
        }
        resultVO.setDownload(String.format(CommonConstant.DOWNLOAD_LOG, jobId, taskType, tenantId));
        resultVO.setMsg(log.toString());
        return resultVO;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        ScheduleJob scheduleJob = jobService.getScheduleJob(jobId);
            if (StringUtils.isBlank(scheduleJob.getApplicationId())) {
            return null;
        }
        // 判断是否运行在本地
        ScheduleJobExpand jobExpand = jobExpandService.selectOneByJobId(jobId);
        String jobExtraInfo = jobExpand.getJobExtraInfo();
        if (StringUtils.isNotBlank(jobExtraInfo)) {
            JSONObject extraInfo = JSONObject.parseObject(jobExtraInfo);
            String runMode = extraInfo.getString("runMode");
            if (EDeployMode.STANDALONE.getMode().equalsIgnoreCase(runMode)) {
                return null;
            }
        }
        IDownload iDownload = null;
        try {
            iDownload = RetryUtil.executeWithRetry(() -> {
                Map yarnConf = clusterService.getComponentByTenantId(tenantId, EComponentType.YARN.getTypeCode(), false,
                        Map.class, null);
                Map hadoopConf = clusterService.getComponentByTenantId(tenantId, EComponentType.HDFS.getTypeCode(), false,
                        Map.class, null);
                final LogPluginDownload downloader = new LogPluginDownload(scheduleJob.getApplicationId(), yarnConf, hadoopConf,
                        scheduleJob.getSubmitUserName(), limitNum);
                return downloader;
            }, 3, 1000L, false);
        } catch (Exception e) {
            LOGGER.error("downloadJobLog {}  失败:{}", jobId, e);
            return null;
        }
        return iDownload;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        throw new TaierDefineException("not support");
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema, Long datasourceId) {
        throw new TaierDefineException("not support");
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long userId, Task task) {
        throw new TaierDefineException("not support");
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        throw new TaierDefineException("not support");
    }

    private Integer getSchedulerStatus(DevelopSelectSql selectSql) {
        ScheduleJob scheduleJob = jobService.getScheduleJob(selectSql.getJobId());
        if (Objects.isNull(scheduleJob)) {
            return TaskStatus.NOTFOUND.getStatus();
        }
        return TaskStatus.getShowStatus(scheduleJob.getStatus());
    }

    public String scheduleRunLog(String jobId) {
        ScheduleJobExpand jobExpand = jobExpandService.selectOneByJobId(jobId);
        String logInfo = jobExpand.getLogInfo();

        StringBuilder logBuild = new StringBuilder();
        if (StringUtils.isNotBlank(logInfo)) {
            JSONObject baseLogJSON = JSONObject.parseObject(logInfo);
            logBuild.append("====================基本日志====================").append("\n");
            logBuild.append(baseLogJSON.getString("msg_info")).append("\n");

            JSONObject extraInfo = JSONObject.parseObject(jobExpand.getJobExtraInfo());
            if (Objects.nonNull(extraInfo)
                    && EDeployMode.STANDALONE.getMode().equalsIgnoreCase(extraInfo.getString("runMode"))) {
                logBuild.append("====================运行日志====================").append("\n");
                String jobExtraInfo = jobExpand.getJobExtraInfo();
                if (StringUtils.isNotBlank(jobExtraInfo)) {
                    String shellLogPath = extraInfo.getString("shellLogPath");
                    try {
                        String content = FileUtils.readFileToString(new File(shellLogPath));
                        logBuild.append(content);
                    } catch (IOException e) {
                        LOGGER.error("读取本地文件失败, 失败原因：{}", e.getMessage(), e);
                    }
                }
            } else {
                String engineLog = jobExpand.getEngineLog();
                if (StringUtils.isNotBlank(engineLog) && isJSON(engineLog)) {
                    try {
                        JSONObject appLogJSON = JSONObject.parseObject(engineLog);
                        JSONArray appLogs = appLogJSON.getJSONArray("appLog");
                        if (appLogs != null) {
                            logBuild.append("====================appLogs====================").append("\n");
                            for (Object log : appLogs) {
                                logBuild.append(((JSONObject) log).getString("value")).append("\n");
                            }
                        } else {
                            logBuild.append(engineLog).append("\n");
                        }
                    } catch (JSONException e) {
                        LOGGER.error("", e);
                        logBuild.append(engineLog).append("\n");
                    }
                } else if (StringUtils.isNotBlank(engineLog)) {
                    logBuild.append(engineLog).append("\n");
                }
            }
        }
        return logBuild.toString();
    }

    private boolean isJSON(String str) {
        try {
            JSON.parse(str);
            return true;
        } catch (Exception ex) {
            LOGGER.error("parse error",ex);
        }
        return false;
    }
}