package com.dtstack.engine.master.job.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.dtstack.dtcenter.common.cache.connection.CacheConnectionHelper;
import com.dtstack.dtcenter.common.engine.*;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.hadoop.HadoopConf;
import com.dtstack.dtcenter.common.hadoop.HdfsOperator;
import com.dtstack.dtcenter.common.util.DBUtil;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.common.util.RetryUtil;
import com.dtstack.dtcenter.hive.service.HiveJdbcService;
import com.dtstack.dtcenter.hive.service.HiveTableService;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.BatchJobDao;
import com.dtstack.engine.domain.BatchJob;
import com.dtstack.engine.domain.BatchTaskShade;
import com.dtstack.engine.dto.BatchTaskParamShade;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.impl.ClusterService;
import com.dtstack.task.send.TaskUrlConstant;
import com.dtstack.engine.master.job.IJobStartTrigger;
import com.dtstack.engine.master.scheduler.JobParamReplace;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author yuebai
 * @date 2019-11-05
 */
@Service
public class BatchHadoopJobStartTrigger implements IJobStartTrigger {

    private static final Logger LOG = LoggerFactory.getLogger(BatchHadoopJobStartTrigger.class);

    @Autowired
    private JobParamReplace jobParamReplace;

    @Autowired
    private HiveJdbcService hiveJdbcService;

    @Autowired
    private BatchJobDao batchJobDao;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private HiveTableService hiveTableService;

    private DateTimeFormatter dayFormatterAll = DateTimeFormat.forPattern("yyyyMMddHHmmss");

    private static final String KEY_OPEN_CHECKPOINT = "openCheckpoint";

    private static final String KEY_CHECKPOINT_STATE_BACKEND = "flink.checkpoint.stateBackend";

    private static final String KEY_CHECKPOINT_INTERVAL = "flink.checkpoint.interval";

    private static final String DEFAULT_VAL_CHECKPOINT_INTERVAL = "300000";

    private static final String JOB_SAVEPOINT_ARGS_TEMPLATE = "-confProp %s";

    private static final String JOB_ARGS_TEMPLATE = "-jobid %s -job %s";

    private static final String KEY_SAVEPOINT = "state.checkpoints.dir";

    private static final String JOB_ID = "${jobId}";

    private static final String ADD_PART_TEMP = "alter table %s add partition(task_name='%s',time='%s')";

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, BatchTaskShade taskShade, BatchJob batchJob) throws Exception {

        String sql = (String) actionParam.get("sqlText");
        sql = sql == null ? "" : sql;
        String taskParams = taskShade.getTaskParams();

        List<BatchTaskParamShade> taskParamsToReplace = JSONObject.parseArray((String) actionParam.get("taskParamsToReplace"), BatchTaskParamShade.class);

        String taskExeArgs = null;

        if (EJobType.SPARK_SQL.getVal().equals(taskShade.getTaskType()) || EJobType.HIVE_SQL.getVal().equals(taskShade.getTaskType())) {
            sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, batchJob.getCycTime());
        } else if (EJobType.CARBON_SQL.getVal().equals(taskShade.getTaskType())) {
            sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, batchJob.getCycTime());
        } else if (EJobType.SYNC.getVal().equals(taskShade.getTaskType())) {
            String job = (String) actionParam.get("job");
            if (StringUtils.isBlank(job)) {
                throw new DtCenterDefException("数据同步信息不能为空");
            }

            //替换系统参数
            job = jobParamReplace.paramReplace(job, taskParamsToReplace, batchJob.getCycTime());

            Integer tableType = (Integer) actionParam.getOrDefault("tableType", ETableType.HIVE.getType());
            String engineIdentity = (String) actionParam.get("engineIdentity");
            // 获取脏数据存储路径
            try {
                job = this.replaceTablePath(true, job, taskShade.getName(), tableType, engineIdentity,taskShade.getDtuicTenantId());
            } catch (Exception e) {
                LOG.error("create dirty table  partition error {}", batchJob.getJobId(), e);
            }

            // 创建分区
            try {
                if (ETableType.HIVE.getType() == tableType) {
                    job = this.createPartition(taskShade.getDtuicTenantId(), job);
                } else if (ETableType.IMPALA.getType() == tableType) {
                    job = this.createPartitionImpala(taskShade.getDtuicTenantId(), job, actionParam);
                }
            } catch (Exception e) {
                LOG.error("create partition error {}", batchJob.getJobId(), e);
                throw e;
            }


            // 查找上一次同步位置
            if (batchJob.getType() == EScheduleType.NORMAL_SCHEDULE.getType()) {
                job = getLastSyncLocation(taskShade.getTaskId(), job, batchJob.getCycTime());
            } else {
                job = removeIncreConf(job);
            }

            // 构造savepoint参数
            String savepointArgs = null;
            if (isRestore(job)) {
                String savepointPath = this.getSavepointPath(taskShade.getTenantId());
                savepointArgs = this.buildSyncTaskExecArgs(savepointPath, taskParams);

                taskParams += String.format(" \n %s=%s", KEY_OPEN_CHECKPOINT, Boolean.TRUE.toString());
            }

            job = URLEncoder.encode(job, Charsets.UTF_8.name());
            taskExeArgs = String.format(JOB_ARGS_TEMPLATE, batchJob.getJobName(), job);
            if (savepointArgs != null) {
                taskExeArgs += " " + savepointArgs;
            }
        }

        if (EJobType.SPARK_SQL.getVal().equals(taskShade.getTaskType())) {
            //sparkSql已经参数替换过
        } else if (taskShade.getEngineType().equals(EngineType.Learning.getVal())
                || taskShade.getEngineType().equals(EngineType.Shell.getVal())
                || taskShade.getEngineType().equals(EngineType.DtScript.getVal())
                || taskShade.getEngineType().equals(EngineType.Spark.getVal())
                || taskShade.getEngineType().equals(EngineType.Python2.getVal())
                || taskShade.getEngineType().equals(EngineType.Python3.getVal())) {
            //提交
            String exeArgs = (String) actionParam.get("exeArgs");
            //替换系统参数
            String content = jobParamReplace.paramReplace(exeArgs, taskParamsToReplace, batchJob.getCycTime());
            //替换jobId
            taskExeArgs = content.replace(JOB_ID, batchJob.getJobId());
            //提交上传路径
            if (StringUtils.isNotBlank(taskExeArgs) && taskExeArgs.contains(TaskUrlConstant.UPLOADPATH)) {
                taskExeArgs = taskExeArgs.replace(TaskUrlConstant.UPLOADPATH, this.uploadSqlTextToHdfs(batchJob.getDtuicTenantId(), taskShade.getSqlText(), taskShade.getTaskType(),
                        taskShade.getName(), taskShade.getTenantId(), taskShade.getProjectId(), taskParamsToReplace, batchJob.getCycTime()));
            } else if (StringUtils.isNotBlank(sql) && sql.contains(TaskUrlConstant.UPLOADPATH)) {
                //上传代码到hdfs
                sql = sql.replace(TaskUrlConstant.UPLOADPATH, this.uploadSqlTextToHdfs(batchJob.getDtuicTenantId(), taskShade.getSqlText(), taskShade.getTaskType(),
                        taskShade.getName(), taskShade.getTenantId(), taskShade.getProjectId(), taskParamsToReplace, batchJob.getCycTime()));
            }

        }

        if (taskExeArgs != null) {
            actionParam.put("exeArgs", taskExeArgs);
        }
        //统一替换下sql
        sql = jobParamReplace.paramReplace(sql, taskParamsToReplace, batchJob.getCycTime());

        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
    }

    /**
     * 创建脏数据表的分区数据
     *
     * @param saveDirty
     * @param sqlText
     * @param taskName
     * @param tableType
     * @return
     * @throws Exception
     */
    private String replaceTablePath(boolean saveDirty, String sqlText, String taskName, Integer tableType, String db, Long dtuicTenantId) throws Exception {
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
                if (ETableType.IMPALA.getType() == tableType) {
                    executeQuery(dtuicTenantId, db, alterSql);
                    location = getTableLocation(dtuicTenantId, db, tableName);
                } else if (ETableType.HIVE.getType() == tableType) {
                    hiveJdbcService.executeQuery(dtuicTenantId, db, alterSql);
                    location =  hiveTableService.getTableLocation(dtuicTenantId,db,tableName);
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


    private String getTableLocation(Long dtuicTenantId, String dbName, String tableName) throws Exception {
        String location = null;
        List<List<Object>> result = executeQuery(dtuicTenantId, dbName, String.format("DESCRIBE formatted %s", tableName));
        Iterator var6 = result.iterator();

        while(var6.hasNext()) {
            List<Object> objects = (List)var6.next();
            if (objects.get(0).toString().contains("Location:")) {
                location = objects.get(1).toString();
            }
        }

        return location;
    }


    private List<List<Object>> executeQuery(Long dtuicTenantId, String dbName, String sql) throws Exception {
        return this.executeQuery(dtuicTenantId, dbName, sql, true);
    }

    private List<List<Object>> executeQuery(Long dtuicTenantId, String dbName, String sql, Boolean isEnd) throws Exception {
        return this.executeQuery(dtuicTenantId, null, null, dbName, sql, isEnd);
    }

    private List<List<Object>> executeQuery(Long dtuicTenantId, String userName, String password, String dbName, String sql, Boolean isEnd) throws Exception {
        JdbcInfo jdbcInfo = this.getJdbcInfo(dtuicTenantId);
        Connection connection = this.getConnection(dtuicTenantId, userName, password, dbName);
        JdbcQuery jdbcQuery = (new JdbcQuery(connection, dbName, dtuicTenantId, sql, org.apache.commons.lang3.BooleanUtils.isFalse(isEnd))).maxRows(jdbcInfo.getMaxRows());
        return this.executeBaseQuery(jdbcQuery.done());
    }

    private JdbcInfo getJdbcInfo(Long dtuicTenantId) {
        JdbcInfo jdbcInfo = null;
        if (dtuicTenantId != null) {
            jdbcInfo = getImpalaJDBCInfo(dtuicTenantId);
        }

        if (jdbcInfo == null) {
            throw new DtCenterDefException("can't get impala jdbc conf from console");
        } else {
            JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
            return jdbcInfo;
        }
    }

    public JdbcInfo getImpalaJDBCInfo(Long dtuicTenantId) {
        String tenantIdStr = dtuicTenantId.toString();
        JdbcInfo data = (JdbcInfo) ConsoleUtil.getImpala(tenantIdStr, JdbcInfo.class);
        if (data == null) {
            tenantIdStr = tenantIdStr.intern();
            //todo
            synchronized(tenantIdStr) {
                data = (JdbcInfo)ConsoleUtil.getImpala(tenantIdStr, JdbcInfo.class);
                if (data == null) {
                    data = impalaInfo(dtuicTenantId);
                    if (data != null) {
                        ConsoleUtil.setImpala(tenantIdStr, data);
                    }
                }
            }
        }

        return data;
    }

    private JdbcInfo impalaInfo(Long dtuicTenantId) {
        Object data = clusterService.impalaInfo(dtuicTenantId);
        JdbcInfo JDBCInfo = null;
        if (data != null) {
            try {
                JDBCInfo = (JdbcInfo) PublicUtil.strToObject(data.toString(), JdbcInfo.class);
            } catch (IOException e) {
                LOG.error("", e);
            }
        }

        return JDBCInfo;
    }



    private Connection getConnection(Long dtuicTenantId, String userName, String password, String dbName) {
        JdbcInfo jdbcInfo = this.getJdbcInfo(dtuicTenantId);
        password = StringUtils.isBlank(userName) ? jdbcInfo.getPassword() : password;
        userName = StringUtils.isBlank(userName) ? jdbcInfo.getUsername() : userName;
        return DBUtil.getConnection(DataBaseType.Impala, String.format(jdbcInfo.getJdbcUrl(), dbName), userName, password, (Map)null);
    }

    private List<List<Object>> executeBaseQuery(JdbcQuery jdbcQuery) throws Exception {
        List<List<Object>> result = Lists.newArrayList();
        Statement stmt = null;
        ResultSet res = null;

        try {
            stmt = jdbcQuery.getConnection().createStatement();
            stmt.setQueryTimeout(jdbcQuery.getQueryTimeout());
            stmt.setMaxRows(jdbcQuery.getMaxRows());
            LOG.info("impala query:{}", jdbcQuery.getSql());
            if (StringUtils.isNotEmpty(jdbcQuery.getDatabase())) {
                stmt.execute("use " + jdbcQuery.getDatabase());
            }

            if (stmt.execute(jdbcQuery.getSql())) {
                res = stmt.getResultSet();
                int columns = res.getMetaData().getColumnCount();
                List<Object> cloumnName = Lists.newArrayList();

                for(int i = 1; i <= columns; ++i) {
                    String name = res.getMetaData().getColumnName(i);
                    if (name.contains(".")) {
                        name = name.split("\\.")[1];
                    }

                    cloumnName.add(name);
                }

                result.add(cloumnName);

                while(res.next()) {
                    List<Object> objects = Lists.newArrayList();

                    for(int i = 1; i <= columns; ++i) {
                        objects.add(res.getObject(i));
                    }

                    result.add(objects);
                }
            }
        } catch (Throwable var12) {
            if (var12.getMessage() != null && var12.getMessage().contains("AuthorizationException")) {
                throw new DtCenterDefException("未授权", var12);
            }

            throw var12;
        } finally {
            if (stmt != null) {
                stmt.close();
            }

            if (res != null) {
                res.close();
            }

            if (!jdbcQuery.getMultiplex() && jdbcQuery.getConnection() != null && CacheConnectionHelper.getSessionKey() == null) {
                jdbcQuery.getConnection().close();
                LOG.info("success close impala connection");
            }

        }

        return result;
    }

    private String createPartitionImpala(Long dtuicTenantId, String job, Map<String, Object> actionParam) {
        String name = (String) actionParam.getOrDefault("name", "");
        String engineIdentity = (String) actionParam.getOrDefault("engineIdentity", "");
        if (StringUtils.isBlank(name) || StringUtils.isBlank(engineIdentity)) {
            return job;
        }
        String tableName = "dirty_" + name;
        tableName = String.format("%s.%s", engineIdentity, tableName);
        long time = System.currentTimeMillis();
        String alterSql = String.format(ADD_PART_TEMP, tableName, name, time);
        try {
            executeQuery(dtuicTenantId, engineIdentity, alterSql);
        } catch (Exception e) {
            LOG.error("createPartitionImpala error {} ", alterSql, e);
            return job;
        }
        LOG.info("alterSql {}", alterSql);
        //job -> setting -> path 第一次提交是当天到  之后得每次执行都得创建当天分区 并修改对应path
        JSONObject jobJSON = JSONObject.parseObject(job);
        JSONObject jobObj = jobJSON.getJSONObject("job");
        if (Objects.nonNull(jobObj)) {
            JSONObject setting = jobObj.getJSONObject("setting");
            if (Objects.nonNull(setting)) {
                JSONObject dirty = setting.getJSONObject("dirty");
                if (Objects.nonNull(dirty)) {
                    String path = dirty.getString("path");
                    //替换时间
                    String substring = path.substring(0, path.lastIndexOf("/"));
                    dirty.put("path", String.format("%s/time=%s", substring, time));
                    return jobJSON.toJSONString();
                }
            }
        }
        return job;
    }


    /**
     * 创建hive的分区
     */
    private String createPartition(Long dtuicTenantId, String job) {
        JSONObject jobJSON = JSONObject.parseObject(job);
        JSONObject jobObj = jobJSON.getJSONObject("job");
        JSONObject parameter = jobObj.getJSONArray("content").getJSONObject(0)
                .getJSONObject("writer").getJSONObject("parameter");

        if (parameter.containsKey("partition") && parameter.containsKey("connection")) {
            JSONObject connection = parameter.getJSONArray("connection").getJSONObject(0);
            String username = parameter.containsKey("username") ? parameter.getString("username") : "";
            String password = parameter.containsKey("password") ? parameter.getString("password") : "";
            String jdbcUrl = connection.getString("jdbcUrl");
            String table = connection.getJSONArray("table").getString(0);

            String partition = parameter.getString("partition");
            Map<String, String> split = Splitter.on("/").withKeyValueSeparator("=").split(partition);
            Map<String, String> formattedMap = new HashMap<>();
            for (Map.Entry<String, String> entry : split.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();
                value = value.trim();
                if (value.startsWith("'")) {
                    value = value.substring(1);
                }
                if (value.endsWith("'")) {
                    value = value.substring(0, value.length() - 1);
                }
                formattedMap.put(key, value);
            }
            String join = Joiner.on("',").withKeyValueSeparator("='").join(formattedMap);
            partition = join + "'";
            String sql = String.format("alter table %s add if not exists partition (%s)", table, partition);
            try {
                RetryUtil.executeWithRetry(() -> {
                    LOG.info("create partition dtuicTenantId {} {}", dtuicTenantId, sql);
                    hiveJdbcService.executeQuery(dtuicTenantId, jdbcUrl, username, password, sql);
                    cleanFileName(parameter);
                    return null;
                }, 3, 2000, false, Lists.newArrayList(SocketTimeoutException.class));
            } catch (Exception e) {
                LOG.error("create partition error", e);
                throw new RdosDefineException("create partition error:" + e.getMessage());
            }
        }
        return jobJSON.toJSONString();
    }


    /**
     * 查找上一次同步位置
     *
     * @return
     */
    private String getLastSyncLocation(Long taskId, String jobContent, String cycTime) {
        JSONObject jsonJob = JSONObject.parseObject(jobContent);

        Timestamp time = new Timestamp(dayFormatterAll.parseDateTime(cycTime).toDate().getTime());
        // 查找上一次成功的job
        BatchJob job = batchJobDao.getByTaskIdAndStatusOrderByIdLimit(taskId, TaskStatus.FINISHED.getStatus(), time);
        if (job != null && StringUtils.isNotBlank(job.getJobId())) {
            //日志需要从engine获取
            JSONObject logInfoFromEngine = this.getLogInfoFromEngine(job.getJobId());
            if (Objects.isNull(logInfoFromEngine)) {
                return jsonJob.toJSONString();
            }
            try {
                JSONObject jsonLog = JSONObject.parseObject(logInfoFromEngine.getString("engineLog"));
                JSONObject increConfHistory = jsonLog.getJSONObject("increConf");
                if (increConfHistory != null) {
                    JSONObject reader = (JSONObject) JSONPath.eval(jsonJob, "$.job.content[0].reader");
                    String table = JSONPath.eval(reader, "$.parameter.connection[0].table[0]").toString();
                    table = this.getTableName(table);

                    String increCol = JSONPath.eval(reader, "$.parameter.increColumn").toString();
                    String lastTable = increConfHistory.getString("table");
                    lastTable = this.getTableName(lastTable);

                    String lastIncreCol = increConfHistory.getString("increColumn");
                    if (StringUtils.isNotEmpty(lastTable) && lastTable.equals(table)
                            && StringUtils.isNotEmpty(lastIncreCol) && lastIncreCol.equals(increCol)) {
                        String lastEndLocation = increConfHistory.getString("endLocation");
                        if (!lastEndLocation.startsWith("-")) {
                            reader.getJSONObject("parameter").put("startLocation", lastEndLocation);
                        }
                    }
                }
            } catch (Exception e) {
                LOG.warn("上游任务没有增量配置: {}", job.getJobId());
            }
        }

        return jsonJob.toJSONString();
    }


    public void cleanFileName(JSONObject parameter) {
        String jobPartition = parameter.getString("fileName").replaceAll("'", "").replaceAll("\"", "").replaceAll(" ", "");
        parameter.put("fileName", jobPartition);
    }

    public JSONObject getLogInfoFromEngine(@Param("jobId") String jobId) {
        try {
            String log = actionService.log(jobId, ComputeType.BATCH.getType());
            return JSONObject.parseObject(log);
        } catch (Exception e) {
            LOG.error("Exception when getLogInfoFromEngine by jobId: {} and computeType: {}", jobId, ComputeType.BATCH.getType(), e);
        }
        return null;
    }


    public String getTableName(String table) {
        String simpleTableName = table;
        if (StringUtils.isNotEmpty(table)) {
            String[] tablePart = table.split("\\.");
            if (tablePart.length == 1) {
                simpleTableName = tablePart[0];
            } else if (tablePart.length == 2) {
                simpleTableName = tablePart[1];
            }
        }

        return simpleTableName;
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
    private String getSavepointPath(Long tenantId) {
        String clusterInfoStr = clusterService.clusterInfo(tenantId);
        JSONObject clusterJson = JSONObject.parseObject(clusterInfoStr);
        JSONObject flinkConf = clusterJson.getJSONObject("flinkConf");
        if (!flinkConf.containsKey(KEY_SAVEPOINT)) {
            return null;
        }

        String savepointPath = flinkConf.getString(KEY_SAVEPOINT);
        LOG.info("savepoint path:{}", savepointPath);

        if (StringUtils.isEmpty(savepointPath)) {
            throw new RdosDefineException("savepoint path can not be null");
        }

        return savepointPath;
    }


    private String buildSyncTaskExecArgs(String savepointPath, String taskParams) throws Exception {
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(taskParams.getBytes("UTF-8")));
        String interval = properties.getProperty(KEY_CHECKPOINT_INTERVAL, DEFAULT_VAL_CHECKPOINT_INTERVAL);

        JSONObject confProp = new JSONObject();
        confProp.put(KEY_CHECKPOINT_STATE_BACKEND, savepointPath);
        confProp.put(KEY_CHECKPOINT_INTERVAL, interval);
        return String.format(JOB_SAVEPOINT_ARGS_TEMPLATE, URLEncoder.encode(confProp.toJSONString(), Charsets.UTF_8.name()));
    }


    private String uploadSqlTextToHdfs(Long dtuicTenantId, String content, Integer taskType, String taskName, Long tenantId, Long projectId,
                                       List<BatchTaskParamShade> taskParamShades, String cycTime) {
        String hdfsPath = null;
        try {

            //content统一处理参数
            if (StringUtils.isNotBlank(content) && CollectionUtils.isNotEmpty(taskParamShades)) {
                content = jobParamReplace.paramReplace(content, taskParamShades, cycTime);
            }
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s_%s.sh", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.PYTHON.getVal()) ||
                    taskType.equals(EJobType.NOTEBOOK.getVal())) {
                fileName = String.format("python_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.DEEP_LEARNING.getVal())) {
                fileName = String.format("learning_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.SPARK_PYTHON.getVal())) {
                fileName = String.format("pyspark_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            }

            if (fileName != null) {
                hdfsPath = environmentContext.getHdfsTaskPath() + fileName;
                if (taskType.equals(EJobType.SHELL.getVal())) {
                    content = content.replaceAll("\r\n", System.getProperty("line.separator"));
                }
                HdfsOperator.uploadInputStreamToHdfs(HadoopConf.getConfiguration(dtuicTenantId), content.getBytes(), hdfsPath);
            }
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }

        return HadoopConf.getDefaultFs(dtuicTenantId) + hdfsPath;
    }
}
