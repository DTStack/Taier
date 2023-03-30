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

package com.dtstack.taier.scheduler.server.pipeline.operator;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.DataSourceTypeEnum;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.metric.batch.IMetric;
import com.dtstack.taier.common.metric.batch.MetricBuilder;
import com.dtstack.taier.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.taier.common.source.SourceDTOLoader;
import com.dtstack.taier.common.util.TaskParamsUtils;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleTaskShade;
import com.dtstack.taier.dao.dto.ScheduleTaskParamShade;
import com.dtstack.taier.dao.mapper.ScheduleJobMapper;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.pluginapi.enums.EDeployMode;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.dtstack.taier.scheduler.server.pipeline.IPipeline;
import com.dtstack.taier.scheduler.server.pipeline.JobParamReplace;
import com.dtstack.taier.scheduler.service.ComponentConfigService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author yuebai
 * @date 2021-05-17
 */
@org.springframework.stereotype.Component
public class SyncOperatorPipeline extends IPipeline.AbstractPipeline {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncOperatorPipeline.class);

    private static final String KEY_OPEN_CHECKPOINT = "openCheckpoint";

    private static final String KEY_CHECKPOINT_STATE_BACKEND = "flink.checkpoint.stateBackend";

    private static final String KEY_CHECKPOINT_INTERVAL = "flink.checkpoint.interval";

    private static final String DEFAULT_VAL_CHECKPOINT_INTERVAL = "300000";

    private static final String JOB_SAVEPOINT_ARGS_TEMPLATE = "-confProp %s";

    private static final String JOB_ARGS_TEMPLATE = "-job %s";

    private static final String KEY_SAVEPOINT = "state.checkpoints.dir";

    private static final String ADD_PART_TEMP = "alter table %s add partition(task_name='%s',time='%s')";

    private static final String CONF_PROPERTIES = "confProp";

    @Autowired
    private SourceDTOLoader sourceDTOLoader;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    public SyncOperatorPipeline() {
        super(null);
    }

    public SyncOperatorPipeline(String pipelineKey) {
        super(pipelineKey);
    }

    @Override
    public void pipeline(Map<String, Object> actionParam, Map<String, Object> pipelineParam) throws Exception {
        ScheduleTaskShade taskShade = (ScheduleTaskShade) pipelineParam.get(taskShadeKey);
        ScheduleJob scheduleJob = (ScheduleJob) pipelineParam.get(scheduleJobKey);
        List<ScheduleTaskParamShade> taskParamShades = (List) pipelineParam.get(taskParamsToReplaceKey);

        String taskParams = (String) actionParam.get("taskParams");
        String job = (String) actionParam.get("job");
        EDeployMode deployMode = TaskParamsUtils.parseDeployTypeByTaskParams(taskParams, taskShade.getComputeType());
        job = this.replaceSyncJobString(actionParam, taskShade, scheduleJob, taskParamShades, job, deployMode);

        JSONObject confProp = new JSONObject();
        // 构造savepoint参数
        JSONObject savepointArgs = null;
        String taskExeArgs = null;
        if (isRestore(job)) {
            String savepointPath = this.getSavepointPath(taskShade.getTenantId(), deployMode, taskShade.getComponentVersion());
            savepointArgs = this.buildSyncTaskExecArgs(savepointPath, taskParams);
            confProp.putAll(savepointArgs);

            taskParams += String.format(" \n %s=%s", KEY_OPEN_CHECKPOINT, Boolean.TRUE);
        }

        String confPath = (String) actionParam.getOrDefault(CONF_PROPERTIES, "");
        if (StringUtils.isNotBlank(confPath)) {
            confProp.putAll(JSONObject.parseObject(confPath));
        }

        job = URLEncoder.encode(job.replace(CommonConstant.JOB_ID, scheduleJob.getJobId()), Charsets.UTF_8.name());
        taskExeArgs = String.format(JOB_ARGS_TEMPLATE, job);
        if (savepointArgs != null) {
            taskExeArgs += " " + savepointArgs;
        }
        if (MapUtils.isNotEmpty(confProp)) {
            String confPropStr = String.format(JOB_SAVEPOINT_ARGS_TEMPLATE, URLEncoder.encode(confProp.toJSONString(), Charsets.UTF_8.name()));
            taskExeArgs += " " + confPropStr;
        }
        actionParam.put("exeArgs", taskExeArgs);
        actionParam.put("taskParams", taskParams);
    }


    private String replaceSyncJobString(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace,
                                        String job, EDeployMode deployMode) {
        if (StringUtils.isBlank(job)) {
            throw new TaierDefineException("Data synchronization information cannot be empty");
        }

        //替换系统参数
        job = JobParamReplace.paramReplace(job, taskParamsToReplace, scheduleJob.getCycTime());

        if (actionParam.containsKey(CONF_PROPERTIES)) {
            // 获取脏数据存储路径
            try {
                JSONObject confObj = JSONObject.parseObject((String) actionParam.get(CONF_PROPERTIES));
                job = replaceTablePath(true, job, taskShade.getName(), confObj);
            } catch (Exception e) {
                LOGGER.error("create dirty table  partition error {}", scheduleJob.getJobId(), e);
            }
        }

        try {
            // 创建数据同步目标表分区
            job = createPartition(taskShade.getTenantId(), job, scheduleJob.getType());
        } catch (Exception e) {
            LOGGER.error("create partition error {}", scheduleJob.getJobId(), e);
            throw e;
        }


        // 查找上一次同步位置
        if (EScheduleType.NORMAL_SCHEDULE.getType().equals(scheduleJob.getType())) {
            job = getLastSyncLocation(taskShade.getTaskId(), job, scheduleJob.getCycTime(), scheduleJob.getJobId(), taskShade.getComponentVersion(), deployMode);
        } else {
            job = removeIncreConf(job);
        }
        return job;
    }


    /**
     * 创建脏数据表的分区数据
     *
     * @param saveDirty
     * @param sqlText
     * @param taskName
     * @return
     * @throws Exception
     */
    public String replaceTablePath(boolean saveDirty, String sqlText, String taskName, JSONObject confObj) {
        if (MapUtils.isEmpty(confObj)) {
            return sqlText;
        }
        Integer dirtySourceType = confObj.getInteger(CommonConstant.DATASOURCE_TYPE);
        Long dirtyDatasourceId = confObj.getLong(CommonConstant.DATASOURCE_ID);
        //需要建分区
        if (!DataSourceTypeEnum.hadoopDirtyDataSource.contains(dirtySourceType)) {
            return sqlText;
        }
        JSONObject sqlObject = JSONObject.parseObject(sqlText);
        JSONObject job = sqlObject.getJSONObject("job");
        JSONObject setting = job.getJSONObject("setting");

        if (!setting.containsKey("dirty")) {
            return sqlText;
        }

        if (!saveDirty) {
            setting.remove("dirty");
            return sqlObject.toJSONString();
        }

        JSONObject dirty = setting.getJSONObject("dirty");
        String tableName = dirty.getString("tableName");
        String path = null;

        if (StringUtils.isBlank(tableName)) {
            return sqlObject.toJSONString();
        }
        ISourceDTO sourceDTO = sourceDTOLoader.buildSourceDTO(dirtyDatasourceId);
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        String db = client.getCurrentDatabase(sourceDTO);
        //任务提交到task 之前 脏数据表 已经创建
        if (!tableName.contains(".")) {
            tableName = String.format("%s.%s", db, tableName);
        }
        Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
        String alterSql = String.format(ADD_PART_TEMP, tableName, taskName, time);


        client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(alterSql).schema(db).build());
        String location = this.getTableLocation(client, sourceDTO, db, String.format("desc formatted %s", tableName));
        if (StringUtils.isBlank(location)) {
            LOGGER.warn("table {} replace dirty path is null,dirtyType {} ", tableName, dirtySourceType);
        }
        String partName = String.format("task_name=%s/time=%s", taskName, time);
        path = location + "/" + partName;

        dirty.put("path", path);
        setting.put("dirty", dirty);
        job.put("setting", setting);
        sqlObject.put("job", job);
        return sqlObject.toJSONString();
    }

    public String getTableLocation(IClient client, ISourceDTO sourceDTO, String dbName, String sql) {
        String location = null;
        List<Map<String, Object>> result = client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(sql).schema(dbName).build());

        for (Map<String, Object> next : result) {
            List<Object> objects = Lists.newArrayList(next.values());
            if (objects.get(0).toString().contains("Location")) {
                location = objects.get(1).toString();
            }
        }

        return location;
    }

    /**
     * 创建分区
     */
    public String createPartition(Long tenantId, String job, Integer scheduleType) {
        JSONObject jobJSON = JSONObject.parseObject(job);
        JSONObject jobObj = jobJSON.getJSONObject("job");
        JSONObject parameter = jobObj.getJSONArray("content").getJSONObject(0)
                .getJSONObject("writer").getJSONObject("parameter");

        JSONArray sourceIds = parameter.getJSONArray("sourceIds");
        if (CollectionUtils.isEmpty(sourceIds)) {
            return jobJSON.toJSONString();
        }
        Long sourceId = sourceIds.getLong(0);
        ISourceDTO sourceDTO = sourceDTOLoader.buildSourceDTO(sourceId);

        if (parameter.containsKey("partition") && parameter.containsKey("connection")) {
            JSONObject connection = parameter.getJSONArray("connection").getJSONObject(0);
            String table = connection.getJSONArray("table").getString(0);

            String partition = parameter.getString("partition");
            Map<String, String> split = new HashMap<>();
            //(etl_date='2020-09-17'/etl_hour='23')
            if (StringUtils.countMatches(partition, "/") == 1 && StringUtils.countMatches(partition, "=") == 1) {
                //pt=2020/04 分区中带/
                String[] splits = partition.split("=");
                split.put(splits[0], splits[1]);
            } else {
                //pt='asdfasd'/ds='1231231' 2级分区
                split = Splitter.on("/").withKeyValueSeparator("=").split(partition);
            }
            Map<String, String> formattedMap = new HashMap<>();
            for (Map.Entry<String, String> entry : split.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                if (value.startsWith("'") || value.startsWith("\"")) {
                    value = value.substring(1);
                }
                if (value.endsWith("'") || value.endsWith("\"")) {
                    value = value.substring(0, value.length() - 1);
                }
                formattedMap.put(key, value);
            }
            // fileName  需要处理引号
            parameter.put("fileName", partition);
            String join = Joiner.on("',").withKeyValueSeparator("='").join(formattedMap);
            partition = join + "'";
            String sql = String.format("alter table %s add if not exists partition (%s)", table, partition);
            int retryFrequency = EScheduleType.TEMP_JOB.getType().equals(scheduleType) ? 1 : environmentContext.getRetryFrequency();
            try {
                RetryUtil.executeWithRetry(() -> {
                    LOGGER.info("create partition tenantId {} {}", tenantId, sql);
                    IClient client = ClientCache.getClient(sourceDTO.getSourceType());
                    client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(sql).build());
                    cleanFileName(parameter);
                    return null;
                }, retryFrequency, environmentContext.getRetryInterval(), false, null);
            } catch (Exception e) {
                LOGGER.error("create partition error:", e);
                throw new TaierDefineException("create partition error:" + ExceptionUtil.getErrorMessage(e));
            }
        }
        return jobJSON.toJSONString();
    }


    /**
     * 查找上一次同步位置 通过prometheus
     *
     * @return
     */
    private String getLastSyncLocation(Long taskId, String jobContent, String cycTime, String jobId, String componentVersion, EDeployMode deployMode) {
        JSONObject jsonJob = JSONObject.parseObject(jobContent);
        Timestamp time = new Timestamp(dayFormatterAll.parseDateTime(cycTime).toDate().getTime());
        // 查找上一次成功的job
        ScheduleJob job = scheduleJobMapper.getByTaskIdAndStatusOrderByIdLimit(taskId, TaskStatus.FINISHED.getStatus(), time, EScheduleType.NORMAL_SCHEDULE.getType());
        if (job != null && StringUtils.isNotEmpty(job.getEngineJobId())) {
            try {
                JSONObject reader = (JSONObject) JSONPath.eval(jsonJob, "$.job.content[0].reader");
                Object increCol = JSONPath.eval(reader, "$.parameter.increColumn");
                if (null != increCol && null != job.getExecStartTime() && null != job.getExecEndTime()) {
                    String lastEndLocation = queryLastLocation(job.getTenantId(), job.getEngineJobId(), job.getExecStartTime().getTime(), job.getExecEndTime().getTime(), deployMode, jobId, componentVersion);
                    LOGGER.info("job {} last job {} applicationId {} startTime {} endTime {} location {}", job, job.getJobId(), job.getEngineJobId(), job.getExecStartTime(), job.getExecEndTime(), lastEndLocation);
                    reader.getJSONObject("parameter").put("startLocation", lastEndLocation);
                }

            } catch (Exception e) {
                LOGGER.error("get sync job {} lastSyncLocation error ", job.getJobId(), e);
            }
        }

        return jsonJob.toJSONString();
    }

    public String queryLastLocation(Long tenantId, String engineJobId, long startTime, long endTime, EDeployMode deployMode, String jobId, String componentVersion) {
        endTime = endTime + 1000 * 60;
        List<Component> components = componentService.listComponentsByComponentType(tenantId, EComponentType.FLINK.getTypeCode());
        if (CollectionUtils.isEmpty(components)) {
            return null;
        }
        Optional<Component> componentOptional;
        if (StringUtils.isBlank(componentVersion)) {
            componentOptional = components.stream().filter(Component::getIsDefault).findFirst();
        } else {
            componentOptional = components.stream().filter(c -> c.getVersionValue().equals(componentVersion)).findFirst();
        }
        if (!componentOptional.isPresent()) {
            return null;
        }
        Map<String, Object> componentConfigToMap = componentConfigService.convertComponentConfigToMap(componentOptional.get().getId(), true);
        Map<String, Object> flinkConfig = (Map<String, Object>) componentConfigToMap.get(deployMode.getMode());
        String prometheusHost = (String) flinkConfig.get("prometheusHost");
        String prometheusPort = (String) flinkConfig.get("prometheusPort");
        LOGGER.info("last job {} deployMode {} prometheus host {} port {}", jobId, deployMode.getType(), prometheusHost, prometheusPort);
        //prometheus的配置信息 从控制台获取
        PrometheusMetricQuery prometheusMetricQuery = new PrometheusMetricQuery(String.format("%s:%s", prometheusHost, prometheusPort));
        IMetric numReadMetric = MetricBuilder.buildMetric("endLocation", engineJobId, startTime, endTime, prometheusMetricQuery);
        if (numReadMetric != null) {
            String startLocation = String.valueOf(numReadMetric.getMetric());
            LOGGER.info("job {} deployMode {} startLocation [{}]", jobId, deployMode.getType(), startLocation);
            if (StringUtils.isEmpty(startLocation) || "0".equalsIgnoreCase(startLocation)) {
                return null;
            }
            return String.valueOf(numReadMetric.getMetric());
        }
        return null;
    }

    public void cleanFileName(JSONObject parameter) {
        String jobPartition = parameter.getString("fileName").
                replaceAll("'", "").
                replaceAll("\"", "").
                replaceAll(" ", "");
        parameter.put("fileName", jobPartition);
    }


    private String removeIncreConf(String jobContent) {
        JSONObject jobJson = JSONObject.parseObject(jobContent);
        JSONPath.remove(jobJson, "$.job.content[0].reader.parameter.increColumn");
        JSONPath.remove(jobJson, "$.job.content[0].reader.parameter.startLocation");

        return jobJson.toJSONString();
    }

    private boolean isRestore(String job) {
        JSONObject jobJson = JSONObject.parseObject(job);
        Object isRestore = JSONPath.eval(jobJson, "$.job.setting.restore.isRestore");
        return BooleanUtils.toBoolean(String.valueOf(isRestore));
    }

    /**
     * 获取flink任务checkpoint的存储路径
     *
     * @param tenantId 租户id
     * @return checkpoint存储路径
     */
    private String getSavepointPath(Long tenantId, EDeployMode deployMode, String componentVersion) {
        List<Component> components = componentService.listComponentsByComponentType(tenantId, EComponentType.FLINK.getTypeCode());
        if (CollectionUtils.isEmpty(components)) {
            return null;
        }
        Optional<Component> componentOptional = components.stream().filter(c -> c.getVersionValue().equals(componentVersion)).findFirst();
        if (!componentOptional.isPresent()) {
            return null;
        }
        Map<String, Object> componentConfigToMap = componentConfigService.convertComponentConfigToMap(componentOptional.get().getId(), true);
        Map<String, Object> flinkConfig = (Map<String, Object>) componentConfigToMap.get(deployMode.getMode());
        String savepointPath = (String) flinkConfig.get(KEY_SAVEPOINT);
        LOGGER.info("savepoint path:{}", savepointPath);
        if (StringUtils.isEmpty(savepointPath)) {
            throw new TaierDefineException("savepoint path can not be null");
        }

        return savepointPath;
    }


    private JSONObject buildSyncTaskExecArgs(String savepointPath, String taskParams) throws Exception {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(Charsets.UTF_8.name())));
        String interval = properties.getProperty(KEY_CHECKPOINT_INTERVAL, DEFAULT_VAL_CHECKPOINT_INTERVAL);

        JSONObject confProp = new JSONObject();
        confProp.put(KEY_CHECKPOINT_STATE_BACKEND, savepointPath);
        confProp.put(KEY_CHECKPOINT_INTERVAL, interval);
        return confProp;
    }

}

