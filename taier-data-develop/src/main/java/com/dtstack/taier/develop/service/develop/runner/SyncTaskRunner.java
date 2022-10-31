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
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParam;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskParamService;
import com.dtstack.taier.develop.service.develop.impl.TaskDirtyDataManageService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author yuebai
 * @date 2022/7/20
 */
@Component
public class SyncTaskRunner implements ITaskRunner {

    @Autowired
    protected DevelopTaskParamService developTaskParamService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private TaskDirtyDataManageService taskDirtyDataManageService;

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.SYNC);
    }

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariables) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        return null;
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        return null;
    }

    @Override
    public ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        return null;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        return null;
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema, Long datasourceId) {
        return null;
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long userId, Task task) {
        return null;
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        Map<String, Object> actionParam = Maps.newHashMap();
        try {
            List<DevelopTaskParam> taskParamsToReplace = developTaskParamService.getTaskParam(task.getId());
            addConfPropAndParseJob(actionParam, tenantId, task, taskParamsToReplace);
            String name = "run_sync_task_" + task.getName() + "_" + System.currentTimeMillis();
            actionParam.put("taskSourceId", task.getDatasourceId());
            actionParam.put("taskType", EScheduleJobType.SYNC.getVal());
            actionParam.put("name", name);
            actionParam.put("computeType", task.getComputeType());
            actionParam.put("sqlText", "");
            actionParam.put("tenantId", tenantId);
            actionParam.put("isFailRetry", false);
            actionParam.put("maxRetryNum", 0);
            //临时运行不做重试
            actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));
        } catch (Exception e) {
            throw new RdosDefineException(String.format("创建数据同步job失败: %s", e.getMessage()), e);
        }

        return actionParam;
    }

    private Integer parseSyncChannel(JSONObject syncJob) {
        //解析出并发度---sync 消耗资源是: 并发度*1
        try {
            JSONObject jobJson = syncJob.getJSONObject("job").getJSONObject("job");
            JSONObject settingJson = jobJson.getJSONObject("setting");
            JSONObject speedJson = settingJson.getJSONObject("speed");
            return speedJson.getInteger("channel");
        } catch (Exception e) {
            LOGGER.error("", e);
            //默认1
            return 1;
        }
    }

    public String replaceSyncParallelism(String taskParams, int parallelism) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(StandardCharsets.UTF_8)));
        properties.put("mr.job.parallelism", parallelism);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> tmp : properties.entrySet()) {
            sb.append(String.format("%s = %s%s", tmp.getKey(), tmp.getValue(), System.getProperty("line.separator")));
        }
        return sb.toString();
    }


    public void addConfPropAndParseJob(Map<String, Object> actionParam, Long tenantId, Task task, List<DevelopTaskParam> taskParamsToReplace) throws Exception {
        String sql = task.getSqlText() == null ? "" : task.getSqlText();
        String taskParams = task.getTaskParams();
        JSONObject syncJob = JSON.parseObject(task.getSqlText());
        taskParams = replaceSyncParallelism(taskParams, parseSyncChannel(syncJob));

        String job = syncJob.getString("job");
        // 向导模式根据job中的sourceId填充数据源信息，保证每次运行取到最新的连接信息
        job = datasourceService.setJobDataSourceInfo(job, tenantId, syncJob.getIntValue("createModel"));

        developTaskParamService.checkParams(developTaskParamService.checkSyncJobParams(job), taskParamsToReplace);

        JSONObject confProp = new JSONObject();
        taskDirtyDataManageService.buildTaskDirtyDataManageArgs(task.getTaskType(), task.getId(), confProp);
        actionParam.put("job", job);
        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
        actionParam.put("confProp", confProp.toJSONString());
    }

}
