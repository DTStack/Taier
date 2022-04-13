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

import com.dtstack.dtcenter.loader.utils.DBUtil;
import com.dtstack.taier.common.enums.DataSourceType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.dtstack.taier.develop.utils.develop.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.taier.develop.utils.develop.service.IJdbcService;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BatchSparkHiveSqlExeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchSparkHiveSqlExeService.class);

    private static final String INSERT_REGEX = "(?i)insert\\s+.*";

    private static final String ONLY_FROM = "(?i)\\s+from\\s+";

    private static final Pattern FROM_PATTERN = Pattern.compile(ONLY_FROM);

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>((\\*|[a-zA-Z0-9_,\\s]*)\\s+|\\*))from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    private static final String SIMPLE_QUERY_REGEX_ONLY = "(?i)select\\s+.*";

    private static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    private static final List<SqlType> CreateTableTypeList = Lists.newArrayList(SqlType.CREATE, SqlType.CREATE_AS, SqlType.CREATE_LIKE);

    private static final String EXPLAIN_REDEX = "(?i)\\bexplain\\b[\\w\\W]*";

    private static final String SQL_EXCEPTION_REDEX = "(?i)[\\w\\W]*AnalysisException[\\w\\W]*";

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    protected BatchHadoopSelectSqlService batchHadoopSelectSqlService;

    @Autowired
    protected DevelopTenantComponentService developTenantComponentService;

    @Autowired
    protected BatchSelectSqlService selectSqlService;

    @Autowired
    protected BatchFunctionService batchFunctionService;

    @Autowired
    private EnvironmentContext environmentContext;


    /**
     * 直连jdbc执行sql
     *
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
            LOGGER.error("exeHiveSqlDirect error {}", executeContent.getSql(), e);
            throw e;
        }
    }

    /**
     * 逐条处理sql
     *
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
     *
     * @param parseResult
     * @param tenantId
     * @param db
     * @param eScheduleJobType
     */
    protected void executeCreateTableSql(ParseResult parseResult, Long tenantId, String db, EScheduleJobType eScheduleJobType) {
        Connection connection = null;
        try {
            connection = jdbcServiceImpl.getConnection(tenantId, null, eScheduleJobType, db);
            jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, eScheduleJobType, db, String.format("set hive.default.fileformat=%s", environmentContext.getCreateTableType()), connection);
            parseResult.getMainTable().setStoreType(environmentContext.getCreateTableType());
            jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, eScheduleJobType, db, parseResult.getStandardSql(), connection);
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.CREATE_TABLE_ERR, e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    /**
     * 判断是否简单查询
     *
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
                LOGGER.info("can not match 'cols',{}", e);
            }
            if (StringUtils.isNotEmpty(cols)) {
                String[] split = cols.split(",");
                for (int i = 0; i < split.length; i++) {
                    String col = split[i].toUpperCase();
                    if (col.startsWith("DISTINCT ")) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    protected void checkSingleSqlSyntax(Long tenantId, String sql, String db, String taskParam, EScheduleJobType eScheduleJobType) {
        try {
            if (sql.trim().matches(EXPLAIN_REDEX)) {
                return;
            }
            List<String> variables = Lists.newArrayList();
            if (EScheduleJobType.SPARK_SQL == eScheduleJobType) {
                //添加set spark.sql.crossJoin.enabled=true
                variables.add("set spark.sql.crossJoin.enabled=true");
            }
            //建表语句中 有 lifecycle字段 需要去除
            sql = SqlFormatUtil.init(sql).removeCatalogue().removeLifecycle().getSql();
            String explainSql = "explain " + sql;
            // 处理自定义函数逻辑
            List<String> functionVariables = batchFunctionService.buildContainFunctions(sql, tenantId, eScheduleJobType.getType());
            variables.addAll(functionVariables);
            List<List<Object>> result = jdbcServiceImpl.executeQueryWithVariables(tenantId, null, eScheduleJobType, db, explainSql, variables, taskParam);
            if (CollectionUtils.isNotEmpty(result)) {
                String plan = result.get(1).get(0).toString();
                if (plan.matches(SQL_EXCEPTION_REDEX)) {
                    throw new RdosDefineException(plan);
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage(), e);
        }
    }

    /**
     * 执行sql
     *
     * @param executeContent
     * @param scheduleJobType
     * @return
     */
    protected ExecuteResultVO executeSql(ExecuteContent executeContent, EScheduleJobType scheduleJobType) {
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
        boolean useSelfFunction = batchFunctionService.validContainSelfFunction(executeContent.getSql(), tenantId, null, scheduleJobType.getType());

        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        if (Objects.nonNull(parseResult) && Objects.nonNull(parseResult.getStandardSql()) && isSimpleQuery(parseResult.getStandardSql()) && !useSelfFunction) {
            result = simpleQuery(tenantId, parseResult, currDb, userId, scheduleJobType);
            if (!result.getContinue()) {
                return result;
            }
        }
        DataSourceType dataSourceType = null;
        if (EScheduleJobType.SPARK_SQL ==scheduleJobType){
             dataSourceType = DataSourceType.SPARKTHRIFT2_1;
        }else if (EScheduleJobType.HIVE_SQL ==scheduleJobType){
             dataSourceType = DataSourceType.HIVE1X;
        }
        //DataSourceType dataSourceType = scheduleJobType == EScheduleJobType.SPARK_SQL ? DataSourceType.SPARKTHRIFT2_1 : null;
        if (SqlType.CREATE_AS.equals(parseResult.getSqlType())) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(tenantId, parseResult, userId, currDb.toLowerCase(), true, taskId, scheduleJobType.getType(), preJobId);
            result.setJobId(jobId);
            result.setContinue(false);
            return result;
        } else if (SqlType.INSERT.equals(parseResult.getSqlType())
                || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())
                || SqlType.QUERY.equals(parseResult.getSqlType())
                || useSelfFunction) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(tenantId, parseResult, userId, currDb.toLowerCase(), taskId, scheduleJobType.getType(), preJobId);
            result.setJobId(jobId);
        } else {
            if (!executeContent.isExecuteSqlLater()) {
                TenantComponent tenantEngine = null;
                if (executeContent.getTaskType().equals(EScheduleJobType.HIVE_SQL.getType())){
                    tenantEngine = developTenantComponentService.getByTenantAndEngineType(executeContent.getTenantId(), EScheduleJobType.SPARK_SQL.getType());
                }else {
                    tenantEngine = developTenantComponentService.getByTenantAndEngineType(executeContent.getTenantId(), executeContent.getTaskType());
                }
                Preconditions.checkNotNull(tenantEngine, "引擎不能为空");
                if (SqlType.CREATE.equals(parseResult.getSqlType())
                        || SqlType.CREATE_LIKE.equals(parseResult.getSqlType())) {
                    executeCreateTableSql(parseResult, tenantId, tenantEngine.getComponentIdentity().toLowerCase(), scheduleJobType);
                } else {
                    this.exeSqlDirect(executeContent, tenantId, parseResult, result, tenantEngine, dataSourceType);
                }
            }
        }
        result.setContinue(true);
        return result;
    }

    /**
     * 简单查询结果
     *
     * @param tenantId
     * @param parseResult
     * @param currentDb
     * @param tenantId
     * @param userId
     * @param scheduleJobType
     * @return
     */
    protected ExecuteResultVO<List<Object>> simpleQuery(Long tenantId, ParseResult parseResult, String currentDb, Long userId, EScheduleJobType scheduleJobType) {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(parseResult.getStandardSql());
        if (matcher.find()) {
            String tableName = parseResult.getMainTable().getName();

            //这里增加一条记录，保证简单查询sql也能下载数据
            String jobId = UUID.randomUUID().toString();

            String parseColumnsString = "{}";
            selectSqlService.addSelectSql(jobId, tableName, TempJobType.SIMPLE_SELECT.getType(), tenantId, parseResult.getStandardSql(), userId, parseColumnsString, scheduleJobType.getType());
            result.setJobId(jobId);
            result.setContinue(false);
        } else {
            try {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(tenantId, null, scheduleJobType, currentDb.toLowerCase(), parseResult.getStandardSql());
                batchSqlExeService.dealResultDoubleList(executeResult);
                result.setStatus(TaskStatus.FINISHED.getStatus());
                result.setResult(executeResult);
            } catch (Exception e) {
                LOGGER.error("", e);
                result.setStatus(TaskStatus.FAILED.getStatus());
                result.setMsg(e.getMessage());
            }
            result.setContinue(false);
        }
        return result;
    }

}
