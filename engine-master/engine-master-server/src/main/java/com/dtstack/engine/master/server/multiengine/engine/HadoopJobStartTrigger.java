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

package com.dtstack.engine.master.server.multiengine.engine;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.dto.ScheduleTaskParamShade;
import com.dtstack.engine.common.enums.ScheduleEngineType;
import com.dtstack.engine.master.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.common.constrant.TaskConstant;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.pluginapi.enums.EngineType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.DtStringUtil;
import com.dtstack.engine.pluginapi.util.RetryUtil;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.pluginapi.enums.EDeployMode;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.engine.master.impl.ComponentService;
import com.dtstack.engine.master.impl.TaskParamsService;
import com.dtstack.engine.master.server.multiengine.JobStartTriggerBase;
import com.dtstack.engine.master.server.scheduler.JobParamReplace;
import com.dtstack.engine.common.enums.DataBaseType;
import com.dtstack.engine.common.enums.DataSourceType;
import com.dtstack.engine.pluginapi.enums.EScheduleJobType;
import com.dtstack.engine.common.metric.batch.IMetric;
import com.dtstack.engine.common.metric.batch.MetricBuilder;
import com.dtstack.engine.common.metric.prometheus.PrometheusMetricQuery;
import com.dtstack.engine.common.util.Base64Util;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Service
public class HadoopJobStartTrigger extends JobStartTriggerBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopJobStartTrigger.class);

    private static final String USER_NAME = "user.name";
    private static final String USER_LABEL = "node.label";

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private TaskParamsService taskParamsService;

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private static final String KEY_OPEN_CHECKPOINT = "openCheckpoint";

    private static final String KEY_CHECKPOINT_STATE_BACKEND = "flink.checkpoint.stateBackend";

    private static final String KEY_CHECKPOINT_INTERVAL = "flink.checkpoint.interval";

    private static final String DEFAULT_VAL_CHECKPOINT_INTERVAL = "300000";

    private static final String JOB_SAVEPOINT_ARGS_TEMPLATE = "-confProp %s";

    private static final String JOB_ARGS_TEMPLATE = "-jobid %s -job %s";

    private static final String KEY_SAVEPOINT = "state.checkpoints.dir";

    private static final String ADD_PART_TEMP = "alter table %s add partition(task_name='%s',time='%s')";

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob) throws Exception {

        //info信息中数据
        String sql = (String) actionParam.get("sqlText");
        sql = sql == null ? "" : sql;

        String taskParams = taskShade.getTaskParams();

        List<ScheduleTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), ScheduleTaskParamShade.class);
        //统一替换下sql
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, scheduleJob.getCycTime());

        String taskExeArgs = null;
        String uploadPath = null;

        if (EScheduleJobType.SPARK_SQL.getVal().equals(taskShade.getTaskType()) || EScheduleJobType.HIVE_SQL.getVal().equals(taskShade.getTaskType())
                || EScheduleJobType.CARBON_SQL.getVal().equals(taskShade.getTaskType())) {
        } else if (EScheduleJobType.SYNC.getVal().equals(taskShade.getTaskType())) {
            String job = (String) actionParam.get("job");
            job = this.replaceSyncJobString(actionParam, taskShade, scheduleJob, taskParamsToReplace, job);

            // 构造savepoint参数
            String savepointArgs = null;
            if (isRestore(job)) {
                String savepointPath = this.getSavepointPath(taskShade.getDtuicTenantId());
                savepointArgs = this.buildSyncTaskExecArgs(savepointPath, taskParams);

                taskParams += String.format(" \n %s=%s", KEY_OPEN_CHECKPOINT, Boolean.TRUE.toString());
            }

            job = URLEncoder.encode(job.replace(TaskConstant.JOB_ID, scheduleJob.getJobId()), Charsets.UTF_8.name());
            taskExeArgs = String.format(JOB_ARGS_TEMPLATE, scheduleJob.getJobName(), job);
            if (savepointArgs != null) {
                taskExeArgs += " " + savepointArgs;
            }
        } else if (taskShade.getEngineType().equals(ScheduleEngineType.Learning.getVal())
                || taskShade.getEngineType().equals(ScheduleEngineType.Shell.getVal())
                || taskShade.getEngineType().equals(ScheduleEngineType.DtScript.getVal())
                || (taskShade.getEngineType().equals(ScheduleEngineType.Spark.getVal()) && !taskShade.getTaskType().equals(EScheduleJobType.SPARK.getVal()))
                || taskShade.getEngineType().equals(ScheduleEngineType.Python2.getVal())
                || taskShade.getEngineType().equals(ScheduleEngineType.Python3.getVal())) {
            //提交
            String exeArgs = (String) actionParam.get("exeArgs");
            //替换系统参数
            String content = jobParamReplace.paramReplace(exeArgs, taskParamsToReplace, scheduleJob.getCycTime());
            //替换jobId
            taskExeArgs = content.replace(TaskConstant.JOB_ID, scheduleJob.getJobId());
            //提交上传路径
            uploadPath = this.uploadSqlTextToHdfs(scheduleJob.getDtuicTenantId(), taskShade.getSqlText(), taskShade.getTaskType(),
                    taskShade.getName(), taskShade.getTenantId(), taskShade.getProjectId(), taskParamsToReplace, scheduleJob.getCycTime());
            taskExeArgs = taskExeArgs.replace(TaskConstant.UPLOADPATH, uploadPath);
            if (StringUtils.isNotBlank(sql) && sql.contains(TaskConstant.UPLOADPATH)) {
                sql = sql.replace(TaskConstant.UPLOADPATH, uploadPath);
            }
        } else if(taskShade.getEngineType().equals(ScheduleEngineType.Hadoop.getVal()) || taskShade.getEngineType().equals(ScheduleEngineType.Spark.getVal())){
            //hadoop spark mr提交 不用上传文件
            String exeArgs = (String) actionParam.get("exeArgs");
            if (StringUtils.isNotBlank(exeArgs)) {
                //替换系统参数
                taskExeArgs = jobParamReplace.paramReplace(exeArgs, taskParamsToReplace, scheduleJob.getCycTime());
            }
        }

        if (taskExeArgs != null) {
           this.replaceTaskExeArgs(actionParam, scheduleJob, taskParamsToReplace, taskExeArgs,uploadPath);
        }

        taskParams = addTaskPrams(taskParams,taskShade.getEngineType(),scheduleJob);

        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
        //engine 不需要用到的参数 去除
        actionParam.remove("taskParamsToReplace");
    }

    private void replaceTaskExeArgs(Map<String, Object> actionParam, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace,
                                    String taskExeArgs,String uploadPath) throws UnsupportedEncodingException {
        //替换jobId
        taskExeArgs = taskExeArgs.replace(TaskConstant.JOB_ID, scheduleJob.getJobId());
        if(StringUtils.isNotBlank(uploadPath)){
            taskExeArgs = taskExeArgs.replace(TaskConstant.UPLOADPATH, uploadPath);
        }

        //替换组件的exeArgs中的cmd参数
        if (taskExeArgs.contains(TaskConstant.LAUNCH)) {
            String modelParam = (String) actionParam.get("modelParam");
            String launchCmd = (String) actionParam.get(TaskConstant.LAUNCH_CMD);
            if (StringUtils.isNotBlank(modelParam)) {
                if (StringUtils.isNotBlank(uploadPath)) {
                    //替换文件名
                    String fileName = uploadPath.substring(StringUtils.lastIndexOf(uploadPath, "/") + 1);
                    launchCmd = launchCmd.replace(TaskConstant.FILE_NAME, fileName);
                }
                //如果存在modelParam参数 需要进行cycTime替换url加密
                modelParam = URLEncoder.encode(jobParamReplace.paramReplace(modelParam,taskParamsToReplace,scheduleJob.getCycTime()), Charsets.UTF_8.name());
                launchCmd = launchCmd.replace(TaskConstant.MODEL_PARAM, modelParam);
            }
            launchCmd = jobParamReplace.paramReplace(launchCmd, taskParamsToReplace, scheduleJob.getCycTime());
            //替换参数 base64 生成launchCmd
            taskExeArgs = taskExeArgs.replace(TaskConstant.LAUNCH, Base64Util.baseEncode(URLEncoder.encode(launchCmd, Charsets.UTF_8.name())));
            LOGGER.info(" replaceTaskExeArgs job {} exeArgs {} ", scheduleJob.getJobId(), taskExeArgs);
        }
        if (taskExeArgs.contains(TaskConstant.CMD_OPTS)){
            List<String> argList = DtStringUtil.splitIngoreBlank(taskExeArgs);
            for (int i = 0; i < argList.size(); i++) {
                if(TaskConstant.CMD_OPTS.equals(argList.get(i))){
                    String base64 = argList.get(i + 1);
                    try {
                        base64 = Base64Util.baseEncode(jobParamReplace.paramReplace(Base64Util.baseDecode(base64),taskParamsToReplace, scheduleJob.getCycTime()));
                        argList.set(i+1,base64);
                    }catch (Exception e){
                        argList.set(i+1,jobParamReplace.paramReplace(base64,taskParamsToReplace, scheduleJob.getCycTime()));
                    }
                    break;
                }
            }
            taskExeArgs = String.join(" ", argList);
        }
        actionParam.put("exeArgs", taskExeArgs);
    }

    private String buildTensorflowOrKeras(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace) {
        String taskExeArgs;
        //tensorflow 参数
        //--files ${uploadPath} --python-version 3 --launch-cmd ${launch} --app-type tensorflow --app-name dddd
        String exeArgs = (String) actionParam.get("exeArgs");
        String launchCmd = (String) actionParam.getOrDefault(TaskConstant.LAUNCH_CMD, "python ${file}");
        //分为资源上传 和 hdfs上传
        String fileName = "";
        if (launchCmd.contains(TaskConstant.FILE_NAME) || launchCmd.contains(TaskConstant.UPLOADPATH)) {
            String uploadPath = this.uploadSqlTextToHdfs(scheduleJob.getDtuicTenantId(), taskShade.getSqlText(), taskShade.getTaskType(),
                    taskShade.getName(), taskShade.getTenantId(), taskShade.getProjectId(), taskParamsToReplace, scheduleJob.getCycTime());
            fileName = uploadPath.substring(StringUtils.lastIndexOf(uploadPath, "/") + 1);
            exeArgs = exeArgs.replace(TaskConstant.UPLOADPATH, uploadPath);
        }
        launchCmd = jobParamReplace.paramReplace(launchCmd, taskParamsToReplace, scheduleJob.getCycTime());
        //替换参数 base64 生成launchCmd
        String launchString = Base64Util.baseEncode(launchCmd.replace(TaskConstant.FILE_NAME, fileName));
        taskExeArgs = exeArgs.replace(TaskConstant.LAUNCH, launchString);
        LOGGER.info(" TensorFlow job {} fileName {} exeArgs {} ", scheduleJob.getJobId(), fileName, taskExeArgs);
        return taskExeArgs;
    }

    /**
     * 替换数据同步中部分信息
     * @param actionParam
     * @param taskShade
     * @param scheduleJob
     * @param taskParamsToReplace
     * @param job
     * @return
     */
    private String replaceSyncJobString(Map<String, Object> actionParam, ScheduleTaskShade taskShade, ScheduleJob scheduleJob, List<ScheduleTaskParamShade> taskParamsToReplace, String job) {
        if (StringUtils.isBlank(job)) {
            throw new RdosDefineException("Data synchronization information cannot be empty");
        }

        //替换系统参数
        job = jobParamReplace.paramReplace(job, taskParamsToReplace, scheduleJob.getCycTime());

        //TODO 数据资产任务值为空 需要设置默认值
        Integer sourceType = (Integer) actionParam.getOrDefault("dataSourceType", DataSourceType.HIVE.getVal());
        //有可能 mysql-kudu 脏数据表是hive 用以区分数据同步目标表类型 还是脏数据表类型
        Integer dirtyDataSourceType = (Integer) actionParam.getOrDefault("dirtyDataSourceType", DataSourceType.HIVE.getVal());
        String engineIdentity = (String) actionParam.get("engineIdentity");
        // 获取脏数据存储路径
        try {
            job = this.replaceTablePath(true, job, taskShade.getName(), dirtyDataSourceType, engineIdentity,taskShade.getDtuicTenantId());
        } catch (Exception e) {
            LOGGER.error("create dirty table  partition error {}", scheduleJob.getJobId(), e);
        }

        try {
            // 创建数据同步目标表分区
            job = this.createPartition(taskShade.getDtuicTenantId(), job, sourceType, actionParam);
        } catch (Exception e) {
            LOGGER.error("create partition error {}", scheduleJob.getJobId(), e);
            throw e;
        }


        // 查找上一次同步位置
        if (scheduleJob.getType() == EScheduleType.NORMAL_SCHEDULE.getType()) {
            job = getLastSyncLocation(taskShade.getTaskId(), job, scheduleJob.getCycTime(),taskShade.getDtuicTenantId(),taskShade.getAppType(),taskShade.getTaskParams(),
                    scheduleJob.getJobId());
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
     * @param sourceType
     * @return
     * @throws Exception
     */
    public String replaceTablePath(boolean saveDirty, String sqlText, String taskName, Integer sourceType, String db, Long dtuicTenantId) throws Exception {
        if (StringUtils.isBlank(db)) {
            return sqlText;
        }
        JSONObject sqlObject = JSONObject.parseObject(sqlText);
        JSONObject job = sqlObject.getJSONObject("job");
        JSONObject setting = job.getJSONObject("setting");

        if (setting.containsKey("dirty")) {

            if (!saveDirty) {
                setting.remove("dirty");
                return sqlObject.toJSONString();
            }

            JSONObject dirty = setting.getJSONObject("dirty");
            String tableName = dirty.getString("tableName");
            String path = null;

            if (StringUtils.isNotEmpty(tableName)) {
                //任务提交到task 之前 脏数据表 必须要在 ide 创建
                if (!tableName.contains(".")) {
                    tableName = String.format("%s.%s", db, tableName);
                }
                Long time = Timestamp.valueOf(LocalDateTime.now()).getTime();
                String alterSql = String.format(ADD_PART_TEMP, tableName, taskName, time);
                String location = "";
                if (DataSourceType.IMPALA.getVal() == sourceType) {
                    String jdbcInfo = clusterService.getConfigByKey(dtuicTenantId, EComponentType.IMPALA_SQL.getConfName(),true,null);
                    JSONObject pluginInfo = JSONObject.parseObject(jdbcInfo);
                    pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, DataBaseType.Impala.getTypeName());
                    workerOperator.executeQuery(DataBaseType.Impala.getTypeName(), pluginInfo.toJSONString(), alterSql, db);
                    location = this.getTableLocation(pluginInfo, db, DataBaseType.Impala.getTypeName(), String.format("DESCRIBE formatted %s", tableName));
                } else if (DataSourceType.hadoopDirtyDataSource.contains(sourceType)) {
                    Cluster cluster = clusterService.getCluster(dtuicTenantId);
                    Component metadataComponent = componentService.getMetadataComponent(cluster.getId());
                    EComponentType metadataComponentType = EComponentType.getByCode(null == metadataComponent ? EComponentType.SPARK_THRIFT.getTypeCode() : metadataComponent.getComponentTypeCode());
                    String jdbcInfo = clusterService.getConfigByKey(dtuicTenantId, metadataComponentType.getConfName(), true, null);
                    JSONObject pluginInfo = JSONObject.parseObject(jdbcInfo);
                    String engineType = DataBaseType.getHiveTypeName(DataSourceType.getSourceType(sourceType));
                    pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, engineType);
                    pluginInfo.compute(ConfigConstant.JDBCURL, (jdbcUrl, val) -> {
                        String jdbcUrlVal = (String) val;
                        if (StringUtils.isBlank(jdbcUrlVal)) {
                            return null;
                        }
                        return jdbcUrlVal.replace("/%s", environmentContext.getComponentJdbcToReplace());
                    });
                    workerOperator.executeQuery(engineType, pluginInfo.toJSONString(), alterSql, db);
                    location = this.getTableLocation(pluginInfo, db, engineType, String.format("desc formatted %s", tableName));
                }
                if (StringUtils.isBlank(location)) {
                    LOGGER.warn("table {} replace dirty path is null,dirtyType {} ", tableName, sourceType);
                }
                String partName = String.format("task_name=%s/time=%s", taskName, time);
                path = location + "/" + partName;

                dirty.put("path", path);
                setting.put("dirty", dirty);
                job.put("setting", setting);
                sqlObject.put("job", job);
            }
        }
        return sqlObject.toJSONString();
    }

    public String getTableLocation(JSONObject pluginInfo, String dbName, String engineType,String sql) throws Exception {
        String location = null;
        List<List<Object>> result = workerOperator.executeQuery(engineType, pluginInfo.toJSONString(), sql,dbName);
        Iterator var6 = result.iterator();

        while(var6.hasNext()) {
            List<Object> objects = (List)var6.next();
            if (objects.get(0).toString().contains("Location")) {
                location = objects.get(1).toString();
            }
        }

        return location;
    }

    /**
     * 创建分区
     */
    public String createPartition(Long dtuicTenantId, String job,Integer sourceType,Map<String, Object> actionParam) {
        JSONObject jobJSON = JSONObject.parseObject(job);
        JSONObject jobObj = jobJSON.getJSONObject("job");
        JSONObject parameter = jobObj.getJSONArray("content").getJSONObject(0)
                .getJSONObject("writer").getJSONObject("parameter");

        if (parameter.containsKey("partition") && parameter.containsKey("connection")) {
            JSONObject connection = parameter.getJSONArray("connection").getJSONObject(0);
            String username = parameter.containsKey(ConfigConstant.USERNAME) ? parameter.getString(ConfigConstant.USERNAME) : "";
            String password = parameter.containsKey(ConfigConstant.PASSWORD) ? parameter.getString(ConfigConstant.PASSWORD) : "";
            String jdbcUrl = connection.getString(ConfigConstant.JDBCURL);
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
            try {
                RetryUtil.executeWithRetry(() -> {
                    LOGGER.info("create partition dtuicTenantId {} {}", dtuicTenantId, sql);
                    JSONObject pluginInfo = buildDataSourcePluginInfo(parameter.getJSONObject("hadoopConfig"), sourceType, username, password, jdbcUrl);
                    String realDataBase =  pluginInfo.getString("realDataBase");
                    workerOperator.executeQuery(DataBaseType.getHiveTypeName(DataSourceType.getSourceType(sourceType)),pluginInfo.toJSONString(),sql, null != realDataBase ? realDataBase : "");
                    cleanFileName(parameter);
                    return null;
                }, environmentContext.getRetryFrequency(), environmentContext.getRetryInterval(), false, null);
            } catch (Exception e) {
                LOGGER.error("create partition error:", e);
                throw new RdosDefineException("create partition error:" + ExceptionUtil.getErrorMessage(e));
            }
        }
        return jobJSON.toJSONString();
    }


    /**
     * 拼接数据源的连接信息
     * hive 需要判断是否开启了kerberos
     * @param sourceType
     * @param username
     * @param password
     * @param jdbcUrl
     * @return
     */
    private JSONObject buildDataSourcePluginInfo(JSONObject hadoopConfig, Integer sourceType, String username, String password, String jdbcUrl) {
        JSONObject pluginInfo = new JSONObject();
        //解析jdbcUrl中的database,将数据库名称替换成default，防止数据库不存在报 NoSuchDatabaseException
        try {
            String jdbcUrlStr = jdbcUrl;
            if(jdbcUrl.contains(";")) {
                //是开启了kerbers的url
                jdbcUrlStr = jdbcUrl.substring(0,jdbcUrl.indexOf(";"));
            }
            String realDataBase = jdbcUrlStr.substring(jdbcUrlStr.lastIndexOf("/")+1);
            String newJdbcUrl = jdbcUrl.replaceFirst(realDataBase, "default");
            pluginInfo.put("realDataBase",realDataBase);
            pluginInfo.put(ConfigConstant.JDBCURL, newJdbcUrl);
        } catch (Exception e) {
            //替换database异常，则走原来逻辑
            pluginInfo.put(ConfigConstant.JDBCURL,jdbcUrl);
        }
        pluginInfo.put(ConfigConstant.USERNAME, username);
        pluginInfo.put(ConfigConstant.PASSWORD, password);
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY, DataBaseType.getHiveTypeName(DataSourceType.getSourceType(sourceType)));
        if (null == hadoopConfig) {
            return pluginInfo;
        }
        boolean isOpenKerberos = ConfigConstant.KERBEROS.equalsIgnoreCase(hadoopConfig.getString("hadoop.security.authentication"))
                || ConfigConstant.KERBEROS.equalsIgnoreCase(hadoopConfig.getString("hive.server2.authentication"))
                || ConfigConstant.KERBEROS.equalsIgnoreCase(hadoopConfig.getString("hive.server.authentication"));
        if (isOpenKerberos) {
            //开启了kerberos 用数据同步中job 中配置项
            pluginInfo.put(ConfigConstant.OPEN_KERBEROS, Boolean.TRUE.toString());
            String remoteDir = hadoopConfig.getString(ConfigConstant.REMOTE_DIR);
            if(StringUtils.isBlank(remoteDir)){
                throw new RdosDefineException(" data synchronization task hadoopConfig remoteDir field cannot be empty");
            }
            pluginInfo.put(ConfigConstant.REMOTE_DIR,remoteDir);

            String principalFile = hadoopConfig.getString(ConfigConstant.PRINCIPAL_FILE);
            if(StringUtils.isBlank(principalFile)){
                throw new RdosDefineException(" data synchronization hadoopConfig principalFile field cannot be empty");
            }
            pluginInfo.put(ConfigConstant.PRINCIPAL_FILE,principalFile);
            pluginInfo.putIfAbsent(ConfigConstant.PRINCIPAL,hadoopConfig.getString(ConfigConstant.PRINCIPAL));

            JSONObject sftpConf = hadoopConfig.getJSONObject(EComponentType.SFTP.getConfName());
            if (null == sftpConf || sftpConf.size() <= 0) {
                throw new RdosDefineException(" data synchronization hadoopConfig sftpConf field cannot be empty");
            }
            pluginInfo.put(EComponentType.SFTP.getConfName(), sftpConf);
            //krb5.conf的文件名
            String krb5Conf = hadoopConfig.getString(ConfigConstant.JAVA_SECURITY_KRB5_CONF);
            if(StringUtils.isBlank(krb5Conf)){
                //平台不传 暂时设置默认值
                krb5Conf = ConfigConstant.KRB5_CONF;
            }
            pluginInfo.put(ConfigConstant.KRB_NAME, krb5Conf);
            pluginInfo.put(EComponentType.YARN.getConfName(), hadoopConfig);

        }
        return pluginInfo;
    }


    /**
     * 查找上一次同步位置 通过prometheus
     *
     * @return
     */
    private String getLastSyncLocation(Long taskId, String jobContent, String cycTime, Long dtuicTenantId, Integer appType, String taskparams, String jobId) {
        JSONObject jsonJob = JSONObject.parseObject(jobContent);

        Timestamp time = new Timestamp(dayFormatterAll.parseDateTime(cycTime).toDate().getTime());
        // 查找上一次成功的job
        ScheduleJob job = scheduleJobDao.getByTaskIdAndStatusOrderByIdLimit(taskId, RdosTaskStatus.FINISHED.getStatus(), time, appType);
        if (job != null && StringUtils.isNotEmpty(job.getEngineJobId())) {
            try {
                JSONObject reader = (JSONObject) JSONPath.eval(jsonJob, "$.job.content[0].reader");
                Object increCol = JSONPath.eval(reader, "$.parameter.increColumn");
                if (null != increCol && null != job.getExecStartTime() && null != job.getExecEndTime()) {
                    String lastEndLocation = this.queryLastLocation(dtuicTenantId, job.getEngineJobId(), job.getExecStartTime().getTime(), job.getExecEndTime().getTime(), taskparams, job.getComputeType(), jobId);
                    LOGGER.info("job {} last job {} applicationId {} startTime {} endTime {} location {}", job, job.getJobId(), job.getEngineJobId(), job.getExecStartTime(), job.getExecEndTime(), lastEndLocation);
                    reader.getJSONObject("parameter").put("startLocation", lastEndLocation);
                }

            } catch (Exception e) {
                LOGGER.error("get sync job {} lastSyncLocation error ", job.getJobId(), e);
            }
        }

        return jsonJob.toJSONString();
    }

    public String queryLastLocation(Long dtUicTenantId, String engineJobId, long startTime, long endTime, String taskParam,Integer computeType,String jobId) {
        endTime = endTime + 1000 * 60;
        List<ComponentsConfigOfComponentsVO> componentsConfigOfComponentsVOS = componentService.listConfigOfComponents(dtUicTenantId, MultiEngineType.HADOOP.getType(),null);
        if (CollectionUtils.isEmpty(componentsConfigOfComponentsVOS)) {
            return null;
        }
        Optional<ComponentsConfigOfComponentsVO> flinkComponent = componentsConfigOfComponentsVOS.stream().filter(c -> c.getComponentTypeCode().equals(EComponentType.FLINK.getTypeCode())).findFirst();
        if(flinkComponent.isPresent()){
            ComponentsConfigOfComponentsVO componentsVO = flinkComponent.get();
            JSONObject flinkJsonObject = JSONObject.parseObject(componentsVO.getComponentConfig());
            EDeployMode eDeployMode = taskParamsService.parseDeployTypeByTaskParams(taskParam,computeType, EngineType.Flink.name(),dtUicTenantId);
            JSONObject flinkConfig = flinkJsonObject.getJSONObject(eDeployMode.getMode());
            String prometheusHost = flinkConfig.getString("prometheusHost");
            String prometheusPort = flinkConfig.getString("prometheusPort");
            LOGGER.info("last job {} deployMode {} prometheus host {} port {}", jobId, eDeployMode.getType(), prometheusHost, prometheusPort);
            //prometheus的配置信息 从控制台获取
            PrometheusMetricQuery prometheusMetricQuery = new PrometheusMetricQuery(String.format("%s:%s", prometheusHost, prometheusPort));
            IMetric numReadMetric = MetricBuilder.buildMetric("endLocation", engineJobId, startTime, endTime, prometheusMetricQuery);
            if (numReadMetric != null) {
                String startLocation = String.valueOf(numReadMetric.getMetric());
                LOGGER.info("job {} deployMode {} startLocation [{}]", jobId, eDeployMode.getType(),startLocation);
                if (StringUtils.isEmpty(startLocation) || "0".equalsIgnoreCase(startLocation)) {
                    return null;
                }
                return String.valueOf(numReadMetric.getMetric());
            }
        }
        return null;
    }

    public void cleanFileName(JSONObject parameter) {
        String jobPartition = parameter.getString("fileName").replaceAll("'", "").replaceAll("\"", "").replaceAll(" ", "");
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
     * @param dtuicTenantId 租户id
     * @return checkpoint存储路径
     */
    private String getSavepointPath(Long dtuicTenantId) {
        String clusterInfoStr = clusterService.clusterInfo(dtuicTenantId);
        JSONObject clusterJson = JSONObject.parseObject(clusterInfoStr);
        JSONObject flinkConf = clusterJson.getJSONObject(EComponentType.FLINK.getConfName());
        if (!flinkConf.containsKey(KEY_SAVEPOINT)) {
            return null;
        }

        String savepointPath = flinkConf.getString(KEY_SAVEPOINT);
        LOGGER.info("savepoint path:{}", savepointPath);

        if (StringUtils.isEmpty(savepointPath)) {
            throw new RdosDefineException("savepoint path can not be null");
        }

        return savepointPath;
    }


    private String buildSyncTaskExecArgs(String savepointPath, String taskParams) throws Exception {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes(Charsets.UTF_8.name())));
        String interval = properties.getProperty(KEY_CHECKPOINT_INTERVAL, DEFAULT_VAL_CHECKPOINT_INTERVAL);

        JSONObject confProp = new JSONObject();
        confProp.put(KEY_CHECKPOINT_STATE_BACKEND, savepointPath);
        confProp.put(KEY_CHECKPOINT_INTERVAL, interval);
        return String.format(JOB_SAVEPOINT_ARGS_TEMPLATE, URLEncoder.encode(confProp.toJSONString(), Charsets.UTF_8.name()));
    }


    private String uploadSqlTextToHdfs(Long dtuicTenantId, String content, Integer taskType, String taskName, Long tenantId, Long projectId,
                                       List<ScheduleTaskParamShade> taskParamShades, String cycTime) {
        String hdfsPath = null;
        try {

            //content统一处理参数
            if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamShades)) {
                content = jobParamReplace.paramReplace(content, taskParamShades, cycTime);
            }
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s_%s.sh", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.PYTHON.getVal()) ||
                    taskType.equals(EScheduleJobType.NOTEBOOK.getVal())) {
                fileName = String.format("python_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.DEEP_LEARNING.getVal())) {
                fileName = String.format("learning_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.SPARK_PYTHON.getVal())) {
                fileName = String.format("pyspark_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.TENSORFLOW_1_X.getVal())) {
                fileName = String.format("tensorflow_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.KERAS.getVal())){
                fileName = String.format("keras_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EScheduleJobType.PYTORCH.getVal())) {
                fileName = String.format("pytorch_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            }

            if (fileName != null) {
                hdfsPath = environmentContext.getHdfsTaskPath() + fileName;
                if (taskType.equals(EScheduleJobType.SHELL.getVal())) {
                    content = content.replaceAll("\r\n", System.getProperty("line.separator"));
                }

                JSONObject pluginInfoWithComponentType = clusterService.pluginInfoJSON(dtuicTenantId,ScheduleEngineType.Hadoop.getEngineName(),null,null,null);
                String typeName = pluginInfoWithComponentType.getString(ConfigConstant.TYPE_NAME_KEY);
                String hdfsUploadPath = workerOperator.uploadStringToHdfs(typeName, pluginInfoWithComponentType.toJSONString(), content, hdfsPath);
                if(StringUtils.isBlank(hdfsUploadPath)){
                    throw new RdosDefineException("Update task to HDFS failure hdfsUploadPath is blank");
                }
                return hdfsUploadPath;
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }
        throw new RdosDefineException("Update task to HDFS failure:");
    }

    /**
     * 添加任务参数
     */
    private String addTaskPrams(String taskParam,Integer taskType,ScheduleJob scheduleJob){
        if (ScheduleEngineType.DTSCRIPT_AGENT.getVal() == taskType){
            List<String> paramList = DtStringUtil.splitIgnoreQuota(taskParam, '\n');
            Map<String,String> labelUserMap = new HashMap<>(2);
            for (String param : paramList) {
                if (!param.contains("=")){
                   continue;
                }
                String[] properties = param.split("=");
                if (USER_NAME.equals(properties[0] = properties[0].trim()) || USER_LABEL.equals(properties[0])){
                    labelUserMap.put(properties[0],properties[1].trim());
                    if (labelUserMap.size() == 2){
                        break;
                    }
                }
            }
            if (labelUserMap.size() != 2){
                return taskParam;
            }
            // 离线会传入dtUicId
            ComponentUser user = componentService.getComponentUser(scheduleJob.getDtuicTenantId(), EComponentType.DTSCRIPT_AGENT.getTypeCode(), labelUserMap.get(USER_LABEL), labelUserMap.get(USER_NAME));
            taskParam = Objects.nonNull(user) && StringUtils.isNotBlank(user.getPassword())?taskParam + String.format("\r\n%s=%s", "user.password", Base64Util.baseDecode(user.getPassword())):taskParam;
        }
        return taskParam;
    }

}
