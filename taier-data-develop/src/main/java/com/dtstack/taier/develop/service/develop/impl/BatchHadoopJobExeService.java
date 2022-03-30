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
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.BatchTaskParam;
import com.dtstack.taier.dao.domain.BatchTaskParamShade;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.CheckSyntaxResult;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.develop.IBatchJobExeService;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.job.SourceType;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * hadoop 相关类型Job执行
 * Date: 2019/5/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchHadoopJobExeService implements IBatchJobExeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchHadoopJobExeService.class);

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String JOB_ARGS_TEMPLATE = "-jobid %s -job %s";

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        if (!task.getTaskType().equals(EScheduleJobType.SYNC.getVal())) {
            throw new RdosDefineException("只支持同步任务直接运行");
        }
        Map<String, Object> actionParam = Maps.newHashMap();
        try {
            String taskParams = task.getTaskParams();
            List<BatchTaskParam> taskParamsToReplace = batchTaskParamService.getTaskParam(task.getId());

            JSONObject syncJob = JSON.parseObject(task.getSqlText());
            taskParams = replaceSyncParll(taskParams, parseSyncChannel(syncJob));

            String job = syncJob.getString("job");

            // 向导模式根据job中的sourceId填充数据源信息，保证每次运行取到最新的连接信息
            job = datasourceService.setJobDataSourceInfo(job, tenantId, syncJob.getIntValue("createModel"));

            batchTaskParamService.checkParams(batchTaskParamService.checkSyncJobParams(job), taskParamsToReplace);

            String name = "run_sync_task_" + task.getName() + "_" + System.currentTimeMillis();
            String taskExeArgs = String.format(JOB_ARGS_TEMPLATE, name, job);
            actionParam.put("taskSourceId",task.getId());
            actionParam.put("taskType", EScheduleJobType.SYNC.getVal());
            actionParam.put("name", name);
            actionParam.put("computeType", task.getComputeType());
            actionParam.put("sqlText", "");
            actionParam.put("taskParams", taskParams);
            actionParam.put("tenantId", tenantId);
            actionParam.put("sourceType", SourceType.TEMP_QUERY.getType());
            actionParam.put("isFailRetry", false);
            actionParam.put("maxRetryNum", 0);
            actionParam.put("job", job);
            actionParam.put("taskParamsToReplace", JSON.toJSONString(taskParamsToReplace));
            DataSourceType writerDataSourceType = getSyncJobWriterDataSourceType(job);
            if (Objects.nonNull(writerDataSourceType)) {
                actionParam.put("dataSourceType", writerDataSourceType.getVal());
            }
            if (Objects.nonNull(taskExeArgs)) {
                actionParam.put("exeArgs", taskExeArgs);
            }
        } catch (Exception e) {
            throw new RdosDefineException(String.format("创建数据同步job失败: %s", e.getMessage()), e);
        }

        return actionParam;
    }

    /**
     * 获取数据同步写入插件的数据源类型
     * 注意：目前只调整Inceptor类型，其他数据源类型没有出现问题，不进行变动
     *
     * @param jobStr
     * @return
     */
    public DataSourceType getSyncJobWriterDataSourceType(String jobStr) {
        JSONObject job = JSONObject.parseObject(jobStr);
        JSONObject jobContent = job.getJSONObject("job");
        JSONObject content = jobContent.getJSONArray("content").getJSONObject(0);
        JSONObject writer = content.getJSONObject("writer");
        String writerName = writer.getString("name");
        switch (writerName) {
            case PluginName.INCEPTOR_W:
                return DataSourceType.INCEPTOR;
            default:
                return null;
        }
    }


    /**
     * 真正运行SQL任务的逻辑
     * @param userId
     * @param tenantId
     * @param uniqueKey
     * @param taskId
     * @param sql
     * @param isRoot
     * @param task
     * @param dtToken
     * @param isEnd
     * @param jobId
     * @return
     * @throws Exception
     */
    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String uniqueKey, Long taskId, String sql,
                                               Boolean isRoot, Task task, String dtToken, Boolean isEnd, String jobId) throws Exception {
        if (EScheduleJobType.SPARK_SQL.getVal().equals(task.getTaskType())) {
            ExecuteContent content = new ExecuteContent();
            content.setTenantId(tenantId).setUserId(userId).setSql(sql).setTaskId(taskId).setTaskType(task.getTaskType()).setPreJobId(jobId)
                    .setRootUser(isRoot).setCheckSyntax(environmentContext.getExplainEnable()).setIsdirtyDataTable(false).setSessionKey(uniqueKey).setEnd(isEnd);
            return batchSqlExeService.executeSql(content);
        }
        throw new RdosDefineException(String.format("不支持%s类型的任务直接运行", EScheduleJobType.getByTaskType(task.getTaskType()).getName()));
    }

    /**
     * 构建 exeArgs、sqlText、taskParams
     * @param actionParam
     * @param tenantId
     * @param task
     * @param taskParamsToReplace
     * @throws Exception
     */
    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long tenantId, Task task, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        String sql = task.getSqlText() == null ? "" : task.getSqlText();
        String taskParams = task.getTaskParams();
        if (EScheduleJobType.SPARK_SQL.getVal().equals(task.getTaskType())) {
            //Spark SQL任务默认在前面加上建表的类型
            sql = String.format("set hive.default.fileformat=%s;\n ", environmentContext.getCreateTableType()) + sql;
            batchTaskParamService.checkParams(sql, taskParamsToReplace);

            // 构建运行的SQL
            CheckSyntaxResult result = batchSqlExeService.processSqlText(tenantId, task.getTaskType(), sql);
            sql = result.getSql();
        } else if (EScheduleJobType.SYNC.getVal().equals(task.getTaskType())) {
            JSONObject syncJob = JSON.parseObject(task.getSqlText());
            taskParams = replaceSyncParll(taskParams, parseSyncChannel(syncJob));

            String job = syncJob.getString("job");

            // 向导模式根据job中的sourceId填充数据源信息，保证每次运行取到最新的连接信息
            job = datasourceService.setJobDataSourceInfo(job, tenantId, syncJob.getIntValue("createModel"));

            batchTaskParamService.checkParams(batchTaskParamService.checkSyncJobParams(job), taskParamsToReplace);
            actionParam.put("job", job);
        }
        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
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

    public String replaceSyncParll(String taskParams, int parallelism) throws IOException {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(StandardCharsets.UTF_8)));
        properties.put("mr.job.parallelism", parallelism);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Object, Object> tmp : properties.entrySet()) {
            sb.append(String.format("%s = %s%s", tmp.getKey(), tmp.getValue(), LINE_SEPARATOR));
        }
        return sb.toString();
    }

}
