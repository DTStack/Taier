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

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.domain.TenantComponent;
import com.dtstack.batch.engine.rdbms.common.util.SqlFormatUtil;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.service.impl.BatchFunctionService;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.TenantComponentService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.sql.ParseResult;
import com.dtstack.batch.sql.SqlType;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.dtcenter.loader.utils.DBUtil;
import com.dtstack.engine.common.enums.DataSourceType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.TaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class BatchSparkHiveSqlExeService {

    private static final String INSERT_REGEX = "(?i)insert\\s+.*";

    private static final String ONLY_FROM = "(?i)\\s+from\\s+";

    private static final Pattern FROM_PATTERN = Pattern.compile(ONLY_FROM);

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>((\\*|[a-zA-Z0-9_,\\s]*)\\s+|\\*))from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    private static final String SIMPLE_QUERY_REGEX_ONLY = "(?i)select\\s+.*";

    private static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    private static final List<SqlType> CreateTableTypeList = Lists.newArrayList(SqlType.CREATE, SqlType.CREATE_AS, SqlType.CREATE_LIKE);

    private static final String EXPLAIN_REDEX = "(?i)\\bexplain\\b[\\w\\W]*";

    private static final String SQL_EXCEPTION_REDEX = "(?i)[\\w\\W]*AnalysisException[\\w\\W]*";

    private static final Pattern TABLE_NOT_FOUND_PATTERN = Pattern.compile("Table\\sor\\sview\\snot\\sfound:\\s(?<table>[^;]*)");

    private static final String TABLE_NOT_FOUND = "Table or view not found:";

    private static final String CREATE_TEMP_FUNCTION = "create temporary function";

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    protected BatchHadoopSelectSqlService batchHadoopSelectSqlService;

    @Autowired
    protected TenantComponentService tenantEngineService;

    @Autowired
    protected BatchSelectSqlService selectSqlService;

    @Autowired
    protected BatchFunctionService batchFunctionService;

    @Autowired
    private EnvironmentContext environmentContext;


    /**
     * 直连jdbc执行sql
     * @param executeContent
     * @param tenantId
     * @param parseResult
     * @param result
     * @param tenantEngine
     * @param dataSourceType
     */
    protected void exeSqlDirect(ExecuteContent executeContent, Long tenantId, ParseResult parseResult, ExecuteResultVO<List<Object>> result, TenantComponent tenantEngine, DataSourceType dataSourceType) {
        try {
            if (SqlType.getShowType().contains(parseResult.getSqlType())
                    && !parseResult.getStandardSql().matches(INSERT_REGEX)) {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), tenantEngine.getComponentIdentity(), parseResult.getStandardSql());
                batchSqlExeService.dealResultDoubleList(executeResult);
                result.setResult(executeResult);
            } else {
                jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), tenantEngine.getComponentIdentity(), parseResult.getStandardSql());
            }
        } catch (Exception e) {
            log.error("exeHiveSqlDirect error {}", executeContent.getSql(),e);
            throw e;
        }
    }

    /**
     * 逐条处理sql
     * @param sqlText
     * @param database
     * @return
     */
    protected String processSql(String sqlText, String database) {
        sqlText = batchSqlExeService.removeComment(sqlText);
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        List<String> sqls = SqlFormatUtil.splitSqlText(sqlText);
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("use ").append(database.toLowerCase()).append(";\n");

        if (CollectionUtils.isNotEmpty(sqls)) {
            for (String sql : sqls) {
                sql = SqlFormatUtil.formatSql(sql);
                sql = SqlFormatUtil.getStandardSql(sql);
                if (StringUtils.isEmpty(sql)) {
                    continue;
                }

                sqlBuild.append(sql).append(";\n");
            }
        }
        return sqlBuild.toString();
    }

    /**
     * 执行create语句
     * @param parseResult
     * @param tenantId
     * @param db
     * @param eJobType
     */
    protected void executeCreateTableSql(ParseResult parseResult, Long tenantId, String db, EJobType eJobType) {
        Connection connection = null;
        try {
            connection = jdbcServiceImpl.getConnection(tenantId, null, eJobType, db);
            jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, eJobType, db, String.format("set hive.default.fileformat=%s", environmentContext.getCreateTableType()), connection);
            parseResult.getMainTable().setStoreType(environmentContext.getCreateTableType());
            jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, eJobType, db, parseResult.getStandardSql(), connection);
        } catch (Exception e) {
            log.error("", e);
            throw new RdosDefineException(ErrorCode.CREATE_TABLE_ERR, e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    /**
     * 判断是否简单查询
     * @param sql
     * @return
     */
    protected boolean isSimpleQuery(String sql) {
        sql = SqlFormatUtil.formatSql(sql);
        sql = SqlFormatUtil.getStandardSql(sql);
        Matcher matcher = FROM_PATTERN.matcher(sql);
        int fromCount = 0;
        while (matcher.find()) {
            fromCount++;
        }
        Matcher matcherSimple = SIMPLE_QUERY_PATTERN.matcher(sql);
        if ((matcherSimple.matches() && fromCount == 1) || (sql.matches(SIMPLE_QUERY_REGEX_ONLY) && fromCount == 0)) {
            //形如select DISTINCT id,t_int FROM chellner;的sql会被识别为简单查询
            String cols = null;
            try {
                cols = matcherSimple.group("cols");
            } catch (IllegalStateException e) {
                log.info("can not match 'cols',{}", e);
            }
            if (StringUtils.isNotEmpty(cols)){
                String[] split = cols.split(",");
                for (int i = 0; i < split.length; i++) {
                    String col = split[i].toUpperCase();
                    if (col.startsWith("DISTINCT ")){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected void checkSingleSqlSyntax(Long tenantId, String sql, String db, String taskParam, EJobType eJobType) {
        try {
            if (sql.trim().matches(EXPLAIN_REDEX)) {
                return;
            }
            List<String> variables = Lists.newArrayList();
            if (EJobType.SPARK_SQL == eJobType) {
                //添加set spark.sql.crossJoin.enabled=true
                variables.add("set spark.sql.crossJoin.enabled=true");
            }
            //建表语句中 有 lifecycle字段 需要去除
            sql = SqlFormatUtil.init(sql).removeCatalogue().removeLifecycle().getSql();
            String explainSql = "explain " + sql;
            // 处理自定义函数逻辑
            List<String> functionVariables = batchFunctionService.buildContainFunctions(sql, tenantId);
            variables.addAll(functionVariables);
            List<List<Object>> result = jdbcServiceImpl.executeQueryWithVariables(tenantId, null, eJobType, db, explainSql, variables, taskParam);
            if (CollectionUtils.isNotEmpty(result)) {
                String plan = result.get(1).get(0).toString();
                if (plan.matches(SQL_EXCEPTION_REDEX)) {
                    throw new RdosDefineException(plan);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new RdosDefineException(e.getMessage(), e);
        }
    }

    /**
     * 执行sql
     * @param executeContent
     * @param eJobType
     * @return
     */
    protected ExecuteResultVO executeSql(ExecuteContent executeContent, EJobType eJobType) {
        // 判断血缘解析结果，防止空指针
        if (null == executeContent.getParseResult()) {
            throw new RdosDefineException("SQL解析异常，结果为空");
        }
        Long tenantId = executeContent.getTenantId();
        Long userId = executeContent.getUserId();
        Long taskId = executeContent.getTaskId();
        String preJobId = executeContent.getPreJobId();
        String currDb = executeContent.getParseResult().getCurrentDb();
        ParseResult parseResult = executeContent.getParseResult();
        boolean useSelfFunction = batchFunctionService.validContainSelfFunction(executeContent.getSql(), tenantId, null);

        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        if (Objects.nonNull(parseResult) && Objects.nonNull(parseResult.getStandardSql()) && isSimpleQuery(parseResult.getStandardSql()) && !useSelfFunction) {
            result = simpleQuery(tenantId, parseResult, currDb, userId, eJobType);
            if (!result.getIsContinue()) {
                return result;
            }
        }

        DataSourceType dataSourceType = eJobType == EJobType.SPARK_SQL ? DataSourceType.Spark : DataSourceType.HIVE;
        if (SqlType.CREATE_AS.equals(parseResult.getSqlType())) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(tenantId, parseResult, userId, currDb.toLowerCase(), true, taskId, eJobType.getType(), preJobId);
            result.setJobId(jobId);
            result.setIsContinue(false);
            return result;
        } else if (SqlType.INSERT.equals(parseResult.getSqlType())
                || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())
                || SqlType.QUERY.equals(parseResult.getSqlType())
                || useSelfFunction) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(tenantId, parseResult, userId, currDb.toLowerCase(), taskId, eJobType.getType(), preJobId);
            result.setJobId(jobId);
        } else {
            if (!executeContent.isExecuteSqlLater()) {
                TenantComponent tenantEngine = tenantEngineService.getByTenantAndEngineType(executeContent.getTenantId(), executeContent.getTaskType());
                Preconditions.checkNotNull(tenantEngine, "引擎不能为空");
                if (SqlType.CREATE.equals(parseResult.getSqlType())
                        || SqlType.CREATE_LIKE.equals(parseResult.getSqlType())) {
                    executeCreateTableSql(parseResult, tenantId, tenantEngine.getComponentIdentity().toLowerCase(), eJobType);
                } else {
                    this.exeSqlDirect(executeContent, tenantId, parseResult, result, tenantEngine, dataSourceType);
                }
            }
        }
        result.setIsContinue(true);
        return result;
    }

    /**
     * 简单查询结果
     * @param tenantId
     * @param parseResult
     * @param currentDb
     * @param tenantId
     * @param userId
     * @param eJobType
     * @return
     */
    protected ExecuteResultVO<List<Object>> simpleQuery(Long tenantId, ParseResult parseResult, String currentDb, Long userId, EJobType eJobType) {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(parseResult.getStandardSql());
        if (matcher.find()) {
            String db = StringUtils.isEmpty(parseResult.getMainTable().getDb()) ? currentDb : parseResult.getMainTable().getDb();
            String tableName = parseResult.getMainTable().getName();

            //这里增加一条记录，保证简单查询sql也能下载数据
            String jobId = UUID.randomUUID().toString();

            String parseColumnsString = "{}";
            selectSqlService.addSelectSql(jobId, tableName, TempJobType.SIMPLE_SELECT.getType(), tenantId, parseResult.getStandardSql(), userId, parseColumnsString, eJobType.getType());
            result.setJobId(jobId);
            result.setIsContinue(false);
        } else {
            try {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(tenantId, null, eJobType, currentDb.toLowerCase(), parseResult.getStandardSql());
                batchSqlExeService.dealResultDoubleList(executeResult);
                result.setStatus(TaskStatus.FINISHED.getStatus());
                result.setResult(executeResult);
            } catch (Exception e) {
                log.error("", e);
                result.setStatus(TaskStatus.FAILED.getStatus());
                result.setMsg(e.getMessage());
            }

            result.setIsContinue(false);
        }
        return result;
    }

}
