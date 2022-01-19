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

package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.TempJobType;
import com.dtstack.batch.dao.BatchSelectSqlDao;
import com.dtstack.batch.domain.BatchSelectSql;
import com.dtstack.batch.domain.TenantComponent;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.service.impl.BatchDownloadService;
import com.dtstack.batch.service.impl.BatchFunctionService;
import com.dtstack.batch.service.impl.TenantComponentService;
import com.dtstack.batch.service.job.IBatchSelectSqlService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.service.user.UserService;
import com.dtstack.batch.sql.ParseResult;
import com.dtstack.batch.sql.SqlType;
import com.dtstack.batch.sync.job.SourceType;
import com.dtstack.batch.vo.BuildSqlVO;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.engine.common.constrant.TaskStatusConstrant;
import com.dtstack.engine.common.engine.JdbcInfo;
import com.dtstack.engine.common.enums.AppType;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.TaskStatus;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.Strings;
import com.dtstack.engine.domain.BatchTask;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.impl.pojo.ParamActionExt;
import com.dtstack.engine.master.service.ScheduleActionService;
import com.dtstack.engine.master.service.ScheduleJobService;
import com.dtstack.engine.master.vo.action.ActionJobEntityVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 执行选中的sql或者脚本
 *
 * @author jiangbo
 */
@Service
public class BatchHadoopSelectSqlService implements IBatchSelectSqlService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BatchHadoopSelectSqlService.class);

    @Autowired
    private BatchSelectSqlDao batchHiveSelectSqlDao;


    @Autowired
    private HadoopDataDownloadService hadoopDataDownloadService;

    @Autowired
    private BatchDownloadService batchDownloadService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private TenantComponentService tenantEngineService;

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ScheduleActionService actionService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String CREATE_TEMP_TABLE = "use %s; create table %s stored as orc as select * from (%s)temp";

    private static final String CREATE_FUNCTION_TEMP_TABLE = "use %s;%s create table %s stored as orc as select * from (%s)temp";

    private static final String CREATE_TEMP_WITH_TABLE = "use %s; create table %s stored as orc as %s";

    private static final String USE_DB = "use %s; %s ;";

    private static final String USER_DB_TEMP_FUNCTION = "use %s; %s %s ;";

    public static final String TEMP_TABLE_PREFIX = "select_sql_temp_table_";

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>((\\*|[a-zA-Z0-9_,\\s]*)\\s+|\\*))from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    public static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    private static final String WITH_SQL_REGET = "(?i)with[a-zA-Z0-9_,\\s]*as\\s*\\(\\s*select\\s*(\\*|[a-zA-Z0-9_,\\s]*?)\\s*from[a-zA-Z0-9_,\\s]*\\)\\s*(?<option>[a-zA-Z]+)\\s*";

    private static final Pattern WITH_SQL_PATTERN = Pattern.compile(WITH_SQL_REGET);

    private static final Pattern SELECT_PATTERN = Pattern.compile("(?i)select\\s+(?<cols>(\\*|[a-zA-Z0-9_,\\s]*?))\\s+from\\s+");

    public static final String SQL_AS_REDEX = "(?i)[0-9a-z_]+\\s+?as\\s+[0-9a-z_]+";

    private static final String TASK_NAME_PREFIX = "run_%s_task_%s";

    private static final String DOWNLOAD_LOG = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s&tenantId=%s";


    @Override
    public String runSqlByTask(Long tenantId, ParseResult parseResult, Long userId, String database, Long taskId, Integer taskType, String preJobId) {
        return runSqlByTask(tenantId, parseResult, userId, database, false, taskId, taskType, preJobId);
    }

    /**
     * 使用任务的方式运行sql
     */
    @Override
    public String runSqlByTask(Long tenantId, ParseResult parseResult, Long userId, String database, Boolean isCreateAs, Long taskId, Integer taskType, String preJobId) {
        try {
            BuildSqlVO buildSqlVO = buildSql(parseResult, tenantId, userId, database, isCreateAs, taskId);
            // 发送sql任务
            sendSqlTask(tenantId, buildSqlVO.getSql(), SourceType.TEMP_QUERY, buildSqlVO.getTaskParam(), preJobId, taskId, taskType);

            // 记录job
            batchSelectSqlService.addSelectSql(preJobId, buildSqlVO.getTempTable(), buildSqlVO.getIsSelectSql(), tenantId,
                    parseResult.getOriginSql(), userId, buildSqlVO.getParsedColumns(), taskType);
            return preJobId;
        } catch (Exception e) {
            LOGGER.error("{}", e);
            throw new RdosDefineException("任务执行sql失败");
        }
    }

    /**
     * 获取查询sql对应的id和拼接参数之后的sql
     * @param tenantId
     * @param parseResult
     * @param tenantId
     * @param userId
     * @param database
     * @param isCreateAs
     * @param taskId
     * @return
     */
    public BuildSqlVO  getSqlIdAndSql(Long tenantId, ParseResult parseResult, Long userId, String database, Boolean isCreateAs, Long taskId, Integer taskType){
        BuildSqlVO buildSqlVO = buildSql(parseResult, tenantId, userId, database, isCreateAs, taskId);
        String jobId = UUID.randomUUID().toString();
        // 记录job
        batchSelectSqlService.addSelectSql(jobId, buildSqlVO.getTempTable(), buildSqlVO.getIsSelectSql(), tenantId,
                buildSqlVO.getOriginSql(), userId, buildSqlVO.getParsedColumns(), taskType);
        return buildSqlVO.setJobId(jobId);
    }

    /**
     * 构建 SQL，需要处理函数和 use db 操作
     *
     * @param originSql
     * @param tenantId
     * @param database
     * @param addCustomFunction
     * @return
     */
    public String buildCustomFunctionAndDbSql(String originSql, Long tenantId, String database, Boolean addCustomFunction) {
        if (BooleanUtils.isTrue(addCustomFunction)) {
            String functionSql = batchFunctionService.buildContainFunction(originSql, tenantId);
            if (StringUtils.isNotBlank(functionSql)) {
                return String.format(USER_DB_TEMP_FUNCTION, database, functionSql, originSql);
            }
        }
        return String.format(USE_DB, database, originSql);
    }

    /**
     * sql类型为select的语句判断是否有自定义函数
     *
     * @param originSql
     * @param tenantId
     * @param database
     * @param tempTable
     * @return
     */
    public String buildSelectSqlCustomFunction(String originSql, Long tenantId, String database, String tempTable) {
        // 判断是否是自定义函数
        String createFunction = batchFunctionService.buildContainFunction(originSql, tenantId);
        if (StringUtils.isNotBlank(createFunction)) {
            return String.format(CREATE_FUNCTION_TEMP_TABLE, database, createFunction, tempTable, originSql);
        }
        return String.format(CREATE_TEMP_TABLE, database, tempTable, originSql);

    }



    /**
     * 解析sql
     * @param parseResult
     * @param tenantId
     * @param userId
     * @param database
     * @param isCreateAs
     * @param taskId
     * @return
     */
    public BuildSqlVO buildSql(ParseResult parseResult, Long tenantId, Long userId, String database, Boolean isCreateAs, Long taskId) {
        String originSql = parseResult.getStandardSql();
        // 生成临时表名
        String tempTable = TEMP_TABLE_PREFIX + System.nanoTime();

        if (StringUtils.isEmpty(originSql)) {
            return null;
        }

        String parsedColumns = "";
        int isSelectSql;
        Matcher witchMatcher = WITH_SQL_PATTERN.matcher(originSql);
        String sql = null;
        if (SqlType.CREATE.equals(parseResult.getSqlType())) {
            isSelectSql = TempJobType.CREATE.getType();
            sql = buildCustomFunctionAndDbSql(originSql, tenantId, database, true);
        } else if (isCreateAs) {
            isSelectSql = TempJobType.CREATE_AS.getType();
            sql = buildCustomFunctionAndDbSql(originSql, tenantId, database, true);
        } else if (SqlType.INSERT.equals(parseResult.getSqlType()) || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())) {
            isSelectSql = TempJobType.INSERT.getType();
            sql = buildCustomFunctionAndDbSql(originSql, tenantId, database, true);
        } else if (witchMatcher.find()) {
            TempJobType jobType = getTempJobType(witchMatcher.group("option"));
            isSelectSql = jobType.getType();
            sql = formatSql(jobType, database, tempTable, originSql);
        } else {
            isSelectSql = TempJobType.SELECT.getType();
            sql = buildSelectSqlCustomFunction(originSql, tenantId, database, tempTable);
        }

        //设置需要环境参数
        BatchTask batchTask = batchTaskService.getBatchTaskById(taskId);
        String taskParam = batchTask.getTaskParams();

        return new BuildSqlVO().
                setSql(sql)
                .setTaskParam(taskParam)
                .setIsSelectSql(isSelectSql)
                .setOriginSql(originSql)
                .setParsedColumns(parsedColumns)
                .setTenantId(tenantId)
                .setTempTable(tempTable)
                .setTenantId(tenantId)
                .setUserId(userId);
    }

    private TempJobType getTempJobType(String option) {
        if ("insert".equalsIgnoreCase(option)) {
            return TempJobType.INSERT;
        }
        return TempJobType.SELECT;
    }

	private String formatSql(TempJobType jobType, String database, String tempTable, String originSql) {
		if (TempJobType.INSERT.equals(jobType) && !originSql.contains(String.format(USE_DB, database,StringUtils.EMPTY))) {
			return String.format("use %s;%s", database, originSql);
		}
		return String.format(CREATE_TEMP_WITH_TABLE, database, tempTable, originSql);
	}

    /**
     * 从临时表查询数据
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    @Override
    public ExecuteResultVO selectData(BatchTask batchTask, BatchSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        String jobId = selectSql.getJobId();
        ExecuteResultVO result = new ExecuteResultVO(jobId);
        if (selectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            result.setResult(queryData(tenantId, selectSql.getSqlText(), taskType));
            result.setSqlText(selectSql.getSqlText());
        } else {
            ActionJobEntityVO engineEntity = null;
            //高级运行的临时表记录和发送到engine的jobId不是同一条记录
            if (StringUtils.isNotEmpty(selectSql.getFatherJobId())){
                engineEntity = getTaskStatus(selectSql.getFatherJobId());
            }else {
                engineEntity = getTaskStatus(jobId);
            }
            if (engineEntity == null) {
                return result;
            }
            Integer status = TaskStatusConstrant.getShowStatus(engineEntity.getStatus());
            result.setStatus(status);

            if (buildDataWithCheckTaskStatus(selectSql, tenantId, result, status)) {
                return result;
            }
            // update time
            batchSelectSqlService.updateGmtModify(jobId, tenantId);
        }
        return result;
    }

    /**
     * 获取sql运行日志
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectRunLog(BatchTask batchTask, BatchSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        String jobId = selectSql.getJobId();
        ExecuteResultVO result = new ExecuteResultVO(jobId);
        // 是否需要脱敏，非admin用户并且是sparkSql needMask才会为true ps:hiveSql不支持脱敏
        if (TempJobType.SIMPLE_SELECT.getType().equals(selectSql.getIsSelectSql())) {
            return result;
        }

        ActionJobEntityVO engineEntity = null;
        //高级运行的临时表记录和发送到engine的jobId不是同一条记录
        if (StringUtils.isNotEmpty(selectSql.getFatherJobId())) {
            engineEntity = getTaskStatus(selectSql.getFatherJobId());
        } else {
            engineEntity = getTaskStatus(jobId);
        }
        if (engineEntity == null) {
            return result;
        }
        Integer status = TaskStatusConstrant.getShowStatus(engineEntity.getStatus());
        result.setStatus(status);

        if (EJobType.HIVE_SQL.getVal().equals(taskType)) {
            buildHiveSqlRunLog(result, status, jobId, taskType, engineEntity, tenantId);
        } else {
            if (buildLogsWithCheckTaskStatus(batchTask, selectSql, tenantId, userId, result,
                    StringUtils.isNotEmpty(selectSql.getFatherJobId()) ? selectSql.getFatherJobId() : jobId, engineEntity, status)) {
                return result;
            }
        }
        // update time
        batchSelectSqlService.updateGmtModify(jobId, tenantId);
        return result;
    }

    /**
     * 获取sql 执行结果
     * @param task
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     */
    @Override
    public ExecuteResultVO selectStatus(BatchTask task, BatchSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        ExecuteResultVO executeResultVO = new ExecuteResultVO(selectSql.getJobId());
        executeResultVO.setStatus(getExecuteSqlStatus(selectSql));
        return executeResultVO;
    }

    /**
     * 获取sql 执行结果
     *
     * @param selectSql
     * @return
     */
    public Integer getExecuteSqlStatus(BatchSelectSql selectSql){
        Integer status = TaskStatus.UNSUBMIT.getStatus();
        if (selectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            return TaskStatus.FINISHED.getStatus();
        }
        ActionJobEntityVO engineEntity = null;
        //高级运行的临时表记录和发送到engine的jobId不是同一条记录
        if (StringUtils.isNotEmpty(selectSql.getFatherJobId())) {
            engineEntity = getTaskStatus(selectSql.getFatherJobId());
        } else {
            engineEntity = getTaskStatus(selectSql.getJobId());
        }
        if (engineEntity == null) {
            return status;
        }
        return TaskStatusConstrant.getShowStatus(engineEntity.getStatus());
    }

    /**
     * 组装HiveSql 运行日志
     *
     * @param result
     * @param status
     * @param jobId
     * @param taskType
     * @param engineEntity
     * @param tenantId
     * @throws Exception
     */
    private void buildHiveSqlRunLog(ExecuteResultVO result, Integer status, String jobId, Integer taskType, ActionJobEntityVO engineEntity, Long tenantId) throws Exception {
        // HIVE SQL 没有日志统一处理 hive 逻辑，不管成功或者失败都走表查询
        if ((TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.FAILED.getStatus().equals(status))
                && EJobType.HIVE_SQL.getVal().equals(taskType)) {
            ScheduleJob batchEngineJob = scheduleJobService.getByJobId(jobId);
//            buildLog(engineEntity.getLogInfo(), batchEngineJob != null && StringUtils.isNotBlank(batchEngineJob.getEngineLog()) ?
//                    batchEngineJob.getEngineLog() : null, tenantId, jobId, false, result);
            result.setDownload(null);
        }
    }

    /**
     * 组装运行日志
     *
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param userId
     * @param result
     * @param jobId
     * @param engineEntity
     * @param status
     * @return
     * @throws Exception
     */
    private boolean buildLogsWithCheckTaskStatus(BatchTask batchTask, BatchSelectSql selectSql, Long tenantId, Long userId, ExecuteResultVO result, String jobId, ActionJobEntityVO engineEntity, Integer status) throws Exception {
        if (TaskStatus.FINISHED.getStatus().equals(status)) {
            List<TempJobType> values = Arrays.asList(TempJobType.values());
            List<Integer> types = values.stream().map(TempJobType::getType).collect(Collectors.toList());
            if (types.contains(selectSql.getIsSelectSql()) && StringUtils.isEmpty(selectSql.getFatherJobId())) {
                buildLog(engineEntity.getLogInfo(), engineEntity.getEngineLog(), tenantId, jobId, true, result);
                result.setDownload(String.format(DOWNLOAD_LOG, jobId, EJobType.SPARK_SQL.getVal(),tenantId));
            }
            if (TempJobType.INSERT.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.CREATE_AS.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.PYTHON_SHELL.getType().equals(selectSql.getIsSelectSql())) {
                return true;
            }
        } else if (TaskStatus.FAILED.getStatus().equals(status)) {
            buildLog(engineEntity.getLogInfo(), engineEntity.getEngineLog(), tenantId, jobId, true, result);
            result.setDownload(String.format(DOWNLOAD_LOG, jobId, EJobType.SPARK_SQL.getVal(),tenantId));
        }
        return false;
    }

    /**
     * 组装sql执行结果
     *
     * @param selectSql
     * @param tenantId
     * @param result
     * @param status
     * @return
     * @throws Exception
     */
    private boolean buildDataWithCheckTaskStatus(BatchSelectSql selectSql, Long tenantId, ExecuteResultVO result, Integer status) throws Exception {
        if (TaskStatus.FINISHED.getStatus().equals(status)) {
            if (TempJobType.INSERT.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.CREATE_AS.getType().equals(selectSql.getIsSelectSql())) {
                return true;
            }
            TenantComponent tenantEngine = tenantEngineService.getByTenantAndEngineType(tenantId, result.getTaskType());
            Preconditions.checkNotNull(tenantEngine, String.format("tenant %d not support hadoop engine.", tenantId));
            List<Object> data = hadoopDataDownloadService.queryDataFromTempTable(tenantId, selectSql.getTempTableName(), tenantEngine.getComponentIdentity());
            result.setSqlText(selectSql.getSqlText());
            result.setResult(data);
        }
        return false;
    }


    /**
     * 获取简单查询的查询结果字段
     *
     * @param sql
     * @return
     */
    public static List<String> getSimpleSqlCols(String sql) {
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return Collections.emptyList();
        }
        String colsStr = matcher.group("cols");
        return Arrays.asList(Strings.splitIgnoreQuotaBrackets(colsStr, ","));
    }

    /**
     * 获取简单查询的字段
     *
     * @param sql
     * @param aliasNames
     * @return
     */
    public static List<String> getSimpleQueryFieldNames(String sql, Boolean aliasNames) {
        List<String> sqlCols = getSimpleSqlCols(sql);
        List<String> fieldNames = new ArrayList<>();
        for (String colStr : sqlCols) {
            colStr = colStr.trim();
            if (colStr.equals("*")) {
                break;
            }
            if (colStr.matches(SQL_AS_REDEX)) {
                String[] colAndAlias = Strings.splitIgnoreQuotaBrackets(colStr, "(?i)\\s+as\\s+");
                fieldNames.add(BooleanUtils.isTrue(aliasNames) ? colAndAlias[1] : colAndAlias[0]);
            } else {
                fieldNames.add(colStr);
            }
        }
        return fieldNames;
    }

    /**
     * 查询表数据 - 简单查询
     *
     * @param tenantId uic租户id
     * @param sql 查询sql
     * @param taskType 任务类型
     * @return 查询数据
     * @throws Exception
     */
    private List<Object> queryData(Long tenantId, String sql, Integer taskType) throws Exception {
        List<Object> queryResult = Lists.newArrayList();
        IDownload resultDownload = hadoopDataDownloadService.getSimpleSelectDownLoader(tenantId, sql, taskType);
        Integer num = getMaxQueryNum(sql, tenantId, taskType);
        int readCounter = 0;
        // 第一行插入传字段信息
        queryResult.add(resultDownload.getMetaInfo());
        while (!resultDownload.reachedEnd()) {
            if (readCounter >= num) {
                break;
            }
            queryResult.add(resultDownload.readNext());
            readCounter++;
        }
        return queryResult;
    }

    /**
     * 从简单查询sql中获取最大条数
     *
     * @param sql 简单查询sql
     * @param tenantId 租户id
     * @return 最大条数
     */
    public Integer getMaxQueryNum(String sql, Long tenantId, Integer taskType) {
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            throw new RdosDefineException("该sql不符合简单查询!");
        }
        String limitStr = matcher.group("num");
        Integer num = null;
        if (StringUtils.isNotEmpty(limitStr)) {
            num = Integer.parseInt(limitStr);
        }
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, null, EJobType.getEJobType(taskType));
        if (num == null || num > jdbcInfo.getMaxRows()) {
            num = jdbcInfo.getMaxRows();
        }
        return num;
    }


    /**
     * 获取任务状态
     *
     * @param jobId
     * @return
     */
    @Override
    public ActionJobEntityVO getTaskStatus(String jobId) {
        List<ActionJobEntityVO> engineEntities = actionService.entitys(Arrays.asList(jobId));
        if (CollectionUtils.isNotEmpty(engineEntities)) {
            return engineEntities.get(0);
        }
        return null;
    }

    public BatchSelectSql getByJobId(String jobId, Long tenantId, Integer isDeleted) {
        BatchSelectSql selectSql = batchHiveSelectSqlDao.getByJobId(jobId, tenantId, isDeleted);
        if (selectSql == null) {
            throw new RdosDefineException("select job not exists");
        }
        return selectSql;
    }

    public void stopSelectJob(String jobId, Long tenantId) {
        try {
            actionService.stop(Collections.singletonList(jobId));
            // 这里用逻辑删除，是为了在调度端删除可能生成的临时表
            batchHiveSelectSqlDao.deleteByJobId(jobId, tenantId);
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, String sql, Long userId) {
        this.addSelectSql(jobId, tempTable, isSelectSql, tenantId, sql, userId, "");
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, String sql, Long userId, String parsedColumns) {
        BatchSelectSql hiveSelectSql = new BatchSelectSql();
        hiveSelectSql.setJobId(jobId);
        hiveSelectSql.setTempTableName(tempTable);
        hiveSelectSql.setTenantId(tenantId);
        hiveSelectSql.setIsSelectSql(isSelectSql);
        hiveSelectSql.setSqlText(sql);
        hiveSelectSql.setUserId(userId);
        hiveSelectSql.setParsedColumns(parsedColumns);

        batchHiveSelectSqlDao.insert(hiveSelectSql);
    }

    public String sendSqlTask(Long tenantId, String sql, SourceType sourceType, String taskParams, String jobId, Long taskId, Integer taskType) {
        JSONObject taskParam = new JSONObject();
        taskParam.put("taskType", taskType);
        taskParam.put("sqlText", sql);
        taskParam.put("computeType", ComputeType.BATCH.getType());
        taskParam.put("taskId", jobId);
        taskParam.put("name", String.format(TASK_NAME_PREFIX, "sql", System.currentTimeMillis()));
        taskParam.put("pluginInfo", null);
        taskParam.put("sourceType", sourceType.getType());
        taskParam.put("taskParams", taskParams);
        taskParam.put("tenantId", tenantId);
        taskParam.put("isFailRetry", false);
        taskParam.put("maxRetryNum", 0);
        taskParam.put("userId", tenantId);
        taskParam.put("appType", AppType.RDOS.getType());
        taskParam.put("taskSourceId", taskId);
        if (taskParam.containsKey("job")){
            taskParam.remove("job").toString();
        }

        ParamActionExt actionExt = null;
        try {
            actionExt = objectMapper.readValue(taskParam.toJSONString(), ParamActionExt.class);
        } catch (IOException e) {
            LOGGER.error("",e);
            throw new DtCenterDefException("参数异常");
        }
        actionService.start(actionExt);
        return jobId;
    }


    /**
     * 生成日志，优先取hdfs前30000个字节，如果hdfs日志获取失败，则取engine日志
     * @param logInfo
     * @param engineLog
     * @param tenantId 租户ID
     * @param jobId jobId
     * @param needDownload 是否需要去hdfs下载日志
     * @return 日志
     */
    private void buildLog(String logInfo, String engineLog, Long tenantId, String jobId, boolean needDownload, ExecuteResultVO result) {
        String log = "";
        if (needDownload) {
            try {
                log = batchDownloadService.loadJobLog(tenantId, EJobType.SPARK_SQL.getVal(), jobId, 30000);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        result.setRetryLog(false);
        if (StringUtils.isEmpty(log)) {
            if (needDownload) {
                result.setRetryLog(true);
            }
            log = getEngineLog(logInfo, engineLog);
        }
        result.setMsg(log);
    }

    /**
     * 获取engine日志
     * @param logInfo logInfo
     * @param engineLog engineLog
     * @return engine日志
     */
    private String getEngineLog(String logInfo, String engineLog) {
        StringBuilder logBuild = new StringBuilder();
        if (StringUtils.isNotBlank(logInfo)) {
            JSONObject baseLogJSON = JSONObject.parseObject(logInfo);
            logBuild.append("====================基本日志====================").append("\n");
            logBuild.append(baseLogJSON.getString("msg_info")).append("\n");
            if (StringUtils.isNotBlank(engineLog)) {
                try {
                    JSONObject appLogJSON = JSONObject.parseObject(engineLog);
                    JSONArray appLogs = appLogJSON.getJSONArray("appLog");
                    if (appLogs != null) {
                        logBuild.append("====================appLogs====================").append("\n");
                        for (Object log : appLogs) {
                            logBuild.append(((JSONObject) log).getString("value")).append("\n");
                        }
                    }
                } catch (JSONException e) {
                    LOGGER.error("", e);
                    logBuild.append(engineLog).append("\n");
                }
            }
        }
        return logBuild.toString();
    }
}
