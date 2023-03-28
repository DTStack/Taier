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
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.util.MathUtil;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.impl.pojo.ParamActionExt;
import com.dtstack.taier.scheduler.impl.pojo.ParamTaskAction;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.dtstack.taier.scheduler.service.ScheduleActionService;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 创建DataX脚本
     */
    private static final String CREATE_TEMP_DATAX_PY = "%s/bin/datax.py   %s";


    public ExecuteResultVO runScriptWithTask(Long userId, Long tenantId, String sqlText, Task task) throws Exception {
        if (EScheduleJobType.DATAX.getType().equals(task.getTaskType())){
            //init process builder
            ProcessBuilder processBuilder = new ProcessBuilder("b", "/Users/dtstack/ide/Taier/datax/job");
            Process process = processBuilder.start();
            int rc = process.waitFor();
        }
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
        actionParam.put("exeArgs", task.getExeArgs());
        actionParam.put("sqlText", sqlText);
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

}