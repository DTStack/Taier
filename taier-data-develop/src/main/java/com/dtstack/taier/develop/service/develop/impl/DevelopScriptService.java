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
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.impl.pojo.ParamTaskAction;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 执行脚本
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-14 17:47
 */
@Service
public class DevelopScriptService {
    private static final Logger LOG = LoggerFactory.getLogger(DevelopScriptService.class);

    @Autowired
    private ScheduleActionService actionService;

    @Autowired
    protected DevelopTaskParamService developTaskParamService;

    @Autowired
    private DevelopSelectSqlService developSelectSqlService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private DatasourceOperator datasourceOperator;

    public ExecuteResultVO runScriptWithTask(Long userId, Long tenantId, String sqlText, Task task) throws Exception {
        Map<String, Object> actionParam = readyForScriptImmediatelyJob(task, sqlText, tenantId);
        String extraInfo = JSON.toJSONString(actionParam);
        ParamTaskAction paramTaskAction = new ParamTaskAction();
        ScheduleTaskShade scheduleTaskShade = JSON.parseObject(extraInfo, ScheduleTaskShade.class);
        scheduleTaskShade.setExtraInfo(extraInfo);
        scheduleTaskShade.setTaskId(task.getId());
        scheduleTaskShade.setScheduleConf(task.getScheduleConf());
        scheduleTaskShade.setComponentVersion(task.getComponentVersion());
        paramTaskAction.setTask(scheduleTaskShade);
        ParamActionExt paramActionExt = actionService.paramActionExt(paramTaskAction.getTask(), paramTaskAction.getJobId(), paramTaskAction.getFlowJobId());
        actionService.start(paramActionExt);

        String taskShadeName = MathUtil.getString(actionParam.get("name"));
        developSelectSqlService.addSelectSql(paramActionExt.getJobId(), taskShadeName,
                TempJobType.PYTHON_SHELL.getType(), task.getTenantId(),
                buildSaveData(taskShadeName), userId, task.getTaskType()
        );
        ExecuteResultVO resultVO = new ExecuteResultVO(paramActionExt.getJobId());
        // indicating frontEnd should polling
        resultVO.setContinue(true);
        return resultVO;
    }

    private Map<String, Object> readyForScriptImmediatelyJob(Task task, String sqlText, Long tenantId) {
        Map<String, Object> actionParam = Maps.newHashMap();
        actionParam.put("taskType", task.getTaskType());
        String name = String.format(CommonConstant.TASK_NAME_PREFIX, task.getName(), System.currentTimeMillis());
        actionParam.put("name", name);
        actionParam.put("computeType", task.getComputeType());
        actionParam.put("tenantId", tenantId);
        actionParam.put("isFailRetry", false);
        actionParam.put("maxRetryNum", 0);
        List<DevelopTaskParam> taskParamsToReplace = developTaskParamService.getTaskParam(task.getId());
        actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));
        // 临时运行，将 sqlText 上传到 hdfs，填充路径
        actionParam.put("exeArgs", buildScriptExeArgs(task, sqlText, tenantId));
        actionParam.put("taskParams", task.getTaskParams());
        return actionParam;
    }

    private String buildSaveData(String taskName) {
        JSONObject exeArgs = new JSONObject();
        exeArgs.put("--app-name", taskName);
        exeArgs.put("--cmd-opts", "");
        exeArgs.put("--files", null);

        JSONObject saveData = new JSONObject();
        saveData.put("exeArgs", exeArgs);
        return saveData.toJSONString();
    }

    private String buildScriptExeArgs(Task task, String sqlText, Long tenantId) {
        String fileDir = uploadSqlTextToHdfs(sqlText, task.getTaskType(), task.getName(), tenantId);
        return buildExeArgs(task, fileDir);
    }

    @NotNull
    public String buildExeArgs(Task task, String fileDir) {
        JSONObject exeArgsJson = JSON.parseObject(task.getExeArgs());
        exeArgsJson = collectArgs(exeArgsJson, fileDir);

        List<String> exeArgsList = new ArrayList<>();
        // json --> list
        exeArgsJson.forEach((k, v) -> exeArgsList.add(String.format("%s %s", k, v)));

        StringBuffer resultString = new StringBuffer();
        exeArgsList.forEach(exeArg -> resultString.append(exeArg.trim()).append(" "));
        return resultString.toString().trim();
    }

    public String uploadSqlTextToHdfs(String sqlText, Integer taskType, String taskName, Long tenantId) {
        String hdfsPath = null;
        try {
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s.sh", tenantId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.PYTHON.getVal())) {
                fileName = String.format("python_%s_%s_%s.py", tenantId,
                        taskName, System.currentTimeMillis());
            }

            JSONObject hdfsConf = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
            String hdfsURI = hdfsConf.getString(ConfigConstant.FS_DEFAULT);
            hdfsPath = String.format("%s%s%s", hdfsURI, environmentContext.getHdfsBatchPath(), fileName);

            if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
                sqlText = sqlText.replaceAll("\r\n", System.getProperty("line.separator"));
            }
            datasourceOperator.uploadInputStreamToHdfs(hdfsConf, tenantId, sqlText.getBytes(), hdfsPath);
        } catch (Exception e) {
            LOG.error("Update task to HDFS failure", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }

        return hdfsPath;
    }

    /**
     * collect args
     * @param exeArgsJson
     * @param fileDir
     * @return
     */
    private JSONObject collectArgs(JSONObject exeArgsJson, String fileDir) {
        exeArgsJson = rmUnConcerned(exeArgsJson);
        exeArgsJson.put("--files", fileDir);
        // using 「--app-type」 to determine python version
        return exeArgsJson;
    }

    /**
     * only collect args starting with「--」
     * @param exeArgsJson
     * @return
     */
    private JSONObject rmUnConcerned(JSONObject exeArgsJson) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Object> entry : exeArgsJson.entrySet()) {
            if (entry.getKey().startsWith("--")) {
                obj.put(entry.getKey(), entry.getValue());
            }
        }
        return obj;
    }
}