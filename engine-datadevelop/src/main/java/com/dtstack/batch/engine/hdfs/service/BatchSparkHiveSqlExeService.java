package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.ErrorCode;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.service.impl.BatchFunctionService;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.utils.ParseResultUtils;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.dtcenter.loader.utils.DBUtil;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.api.service.LineageService;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SelectColumn;
import com.dtstack.engine.api.vo.lineage.SqlParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private BatchTableFilterService batchTableFilterService;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private LineageService lineageService;

    @Autowired
    protected BatchHadoopSelectSqlService batchHadoopSelectSqlService;

    @Autowired
    protected ProjectEngineService projectEngineService;

    @Autowired
    private ITableService tableServiceImpl;

    @Autowired
    protected BatchSelectSqlService selectSqlService;

    @Autowired
    protected BatchFunctionService batchFunctionService;

    @Autowired
    protected UserService userService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private EnvironmentContext environmentContext;

    protected void directExecutionSql(Long dtUicTenantId,Long dtUicUserId, String dbName, String sql, EJobType eJobType) {
        if (!StringUtils.isEmpty(dbName)) {
            jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId, dtUicUserId, eJobType, dbName, sql);
        }
    }

    /**
     * 直连jdbc执行sql
     * @param executeContent
     * @param dtuicTenantId
     * @param parseResult
     * @param result
     * @param projectDb
     * @param dataSourceType
     */
    protected void exeSqlDirect(ExecuteContent executeContent, Long dtuicTenantId, ParseResult parseResult, ExecuteResultVO<List<Object>> result, ProjectEngine projectDb, DataSourceType dataSourceType) {
        try {
            if (SqlType.getShowType().contains(parseResult.getSqlType())
                    && !parseResult.getStandardSql().matches(INSERT_REGEX)) {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), projectDb.getEngineIdentity(), parseResult.getStandardSql());
                if (SqlType.SHOW_TABLES.equals(parseResult.getSqlType())) {
                    executeResult = batchTableFilterService.filterTable(parseResult.getCurrentDb(), executeResult, executeContent.getTenantId());
                }
                batchSqlExeService.dealResultDoubleList(executeResult);
                result.setResult(executeResult);
            } else {
                jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(dataSourceType.getVal()), projectDb.getEngineIdentity(), parseResult.getStandardSql());
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
     * sql逐条血缘解析
     * @param sqls
     * @param tenantId
     * @param projectId
     * @param dbName
     * @param dtUicTenantId
     * @param dataSourceType
     * @return
     */
    protected List<ParseResult> parseLineageFromSqls(List<String> sqls, Long tenantId, Long projectId, String dbName, Long dtUicTenantId, DataSourceType dataSourceType) {
        List<ParseResult> parseResultList = new ArrayList<>();
        for (String sql : sqls) {
            try {
                ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
                parseColumnLineageParam.setSql(sql);
                parseColumnLineageParam.setAppType(AppType.RDOS.getType());
                parseColumnLineageParam.setDefaultDb(dbName);
                parseColumnLineageParam.setDataSourceType(dataSourceType.getVal());
                parseColumnLineageParam.setTableColumnsMap(Maps.newHashMap());
                ColumnLineageParseInfo columnLineageParseInfo = lineageService.parseColumnLineage(parseColumnLineageParam).getData();
                ParseResult parseResult = ParseResultUtils.convertParseResult(columnLineageParseInfo);
                parseResultList.add(parseResult);
            } catch (Exception e) {
                log.error("parse sql [{}] error", sql, e);
            }
        }
        return parseResultList;
    }

    /**
     * 执行create语句
     * @param parseResult
     * @param dtUicTenantId
     * @param db
     * @param eJobType
     */
    protected void executeCreateTableSql(ParseResult parseResult, Long dtUicTenantId, String db, EJobType eJobType) {
        Connection connection = null;
        try {
            connection = jdbcServiceImpl.getConnection(dtUicTenantId, null, eJobType, db);
            jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId, null, eJobType, db, String.format("set hive.default.fileformat=%s", environmentContext.getCreateTableType()), connection);
            parseResult.getMainTable().setStoreType(environmentContext.getCreateTableType());
            jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId, null, eJobType, db, parseResult.getStandardSql(), connection);
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

    protected void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam, EJobType eJobType) {
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
            List<String> functionVariables = batchFunctionService.buildContainFunctions(sql, projectId);
            variables.addAll(functionVariables);
            List<List<Object>> result = jdbcServiceImpl.executeQueryWithVariables(dtuicTenantId, null, eJobType, db, explainSql, variables, taskParam);
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
            throw new RdosDefineException("血缘解析异常，结果为空");
        }
        Long dtuicTenantId = executeContent.getDtuicTenantId();
        Long tenantId = executeContent.getTenantId();
        Long projectId = executeContent.getProjectId();
        Long userId = executeContent.getUserId();
        Long relationId = executeContent.getRelationId();
        String preJobId = executeContent.getPreJobId();
        Integer relationType = executeContent.getRelationType();
        String currDb = executeContent.getParseResult().getCurrentDb();
        ParseResult parseResult = executeContent.getParseResult();
        boolean useFunction = batchFunctionService.validContainSelfFunction(executeContent.getSql(), projectId, null);

        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        if (Objects.nonNull(parseResult) && Objects.nonNull(parseResult.getStandardSql()) && isSimpleQuery(parseResult.getStandardSql()) && !useFunction) {
            result = simpleQuery(dtuicTenantId, parseResult, currDb, tenantId, userId, executeContent.getEngineType(), eJobType);
            if (!result.getIsContinue()) {
                return result;
            }
        }

        DataSourceType dataSourceType = eJobType == EJobType.SPARK_SQL ? DataSourceType.Spark : DataSourceType.HIVE;
        if (SqlType.CREATE_AS.equals(parseResult.getSqlType())) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(dtuicTenantId, parseResult, tenantId, projectId, userId,
                    currDb.toLowerCase(), true, relationId, relationType, preJobId);
            result.setJobId(jobId);
            result.setIsContinue(false);
            return result;
        } else if (SqlType.INSERT.equals(parseResult.getSqlType())
                || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())
                || SqlType.QUERY.equals(parseResult.getSqlType())
                || useFunction) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(dtuicTenantId, parseResult, tenantId, projectId,
                    userId, currDb.toLowerCase(), relationId, relationType, preJobId);
            result.setJobId(jobId);
        } else {
            if (!executeContent.isExecuteSqlLater()) {
                ProjectEngine projectDb = projectEngineService.getProjectDb(executeContent.getProjectId(), MultiEngineType.HADOOP.getType());
                Preconditions.checkNotNull(projectDb, "引擎不能为空");
                if (SqlType.CREATE.equals(parseResult.getSqlType())
                        || SqlType.CREATE_LIKE.equals(parseResult.getSqlType())) {
                    executeCreateTableSql(parseResult, dtuicTenantId, projectDb.getEngineIdentity().toLowerCase(), eJobType);
                } else {
                    this.exeSqlDirect(executeContent, dtuicTenantId, parseResult, result, projectDb, dataSourceType);
                }
            }
        }
        result.setIsContinue(true);
        return result;
    }

    /**
     * 简单查询结果
     * @param dtuicTenantId
     * @param parseResult
     * @param currentDb
     * @param tenantId
     * @param userId
     * @param engineType
     * @param eJobType
     * @return
     */
    protected ExecuteResultVO<List<Object>> simpleQuery(Long dtuicTenantId, ParseResult parseResult, String currentDb, Long tenantId, Long userId, Integer engineType, EJobType eJobType) {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(parseResult.getStandardSql());
        if (matcher.find()) {
            String db = StringUtils.isEmpty(parseResult.getMainTable().getDb()) ? currentDb : parseResult.getMainTable().getDb();
            String tableName = parseResult.getMainTable().getName();
            try {
                if(tableServiceImpl.isView(dtuicTenantId, null, db, ETableType.HIVE, tableName)){
                    result.setIsContinue(true);
                    return result;
                }
            } catch (Exception e){
                log.error("", e);
                result.setIsContinue(false);
                result.setStatus(TaskStatus.FAILED.getStatus());
                result.setMsg(e.getMessage());
                return result;
            }

            //这里增加一条记录，保证简单查询sql也能下载数据
            String jobId = UUID.randomUUID().toString();
            Project project = getProjectByDbName(db, currentDb, engineType, tenantId);

            ETableType tableTypeByEngineType = TableTypeEngineTypeMapping.getTableTypeByEngineType(engineType);
            String parseColumnsString = "{}";
            if (null != project && null != tableTypeByEngineType) {
                BatchTableInfo queryTable = batchTableInfoService.getTableInfoByTableName(tableName, project.getTenantId(),project.getId(), tableTypeByEngineType.getType());
                if (null != queryTable && null != parseResult.getRoot()) {
                    Map<Long, Map<String, String>> cols = new HashMap<>();
                    Map<String,String> columns = new HashMap<>();
                    boolean isQueryAll = false;
                    for (SelectColumn column : parseResult.getRoot().getColumns()) {
                        if ("*".equals(column.getName())) {
                            isQueryAll = true;
                            break;
                        }
                        columns.put(column.getName(), column.getAlias());
                    }
                    if (!isQueryAll) {
                        cols.put(queryTable.getId(), columns);
                        parseColumnsString = JSONObject.toJSONString(cols);
                    }
                }
            }
            selectSqlService.addSelectSql(jobId, tableName, TempJobType.SIMPLE_SELECT.getType(), tenantId, project.getId(), parseResult.getStandardSql(), userId, parseColumnsString, engineType);
            result.setJobId(jobId);
            result.setIsContinue(false);
        } else {
            try {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(dtuicTenantId, null, eJobType, currentDb.toLowerCase(), parseResult.getStandardSql());
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

    /**
     * 根据db查询项目信息
     * @param db
     * @param currentDb
     * @param engineType
     * @param tenantId
     * @return
     */
    private Project getProjectByDbName(String db, String currentDb, Integer engineType, Long tenantId) {
        Project project = null;
        if (StringUtils.isNotEmpty(db) && !db.equals(currentDb)) {
            //当前查询db 和 所属db 不一致
            project = projectEngineService.getProjectByDbName(db, engineType, null);
        } else {
            project = projectEngineService.getProjectByDbName(db, engineType, tenantId);
        }
        if (project == null) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_PROJECT);
        }
        return project;
    }

    /**
     * sql语法校验
     * @param dtuicTenantId
     * @param sqlText
     * @param userId
     * @param projectId
     * @param taskParam
     * @param eJobType
     * @return
     */
    protected List<ParseResult> checkMulitSqlSyntax(Long dtuicTenantId, String sqlText, Long userId, Long projectId, String taskParam, EJobType eJobType) {
        List<ParseResult> parseResults = new ArrayList<>();
        ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support hadoop engine.", projectId));
        User user = userService.getUser(userId);
        Long dtUicUserId = user == null ? null : user.getDtuicUserId();
        String database = projectEngine.getEngineIdentity();
        DataSourceType dataSourceType = eJobType == EJobType.SPARK_SQL ? DataSourceType.Spark : DataSourceType.HIVE;
        List<String> sqls = SqlFormatUtil.splitSqlText(sqlText);
        List<String> cacheTable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sqls)) {
            Connection connection = null;
            try {
                List<String> createTables = new ArrayList<>();
                connection =  jdbcServiceImpl.getConnection(dtuicTenantId, null, eJobType, database, taskParam);
                for (String sql : sqls) {
                    sql = sql.replace("\n", "").trim();
                    if (StringUtils.isEmpty(sql)) {
                        continue;
                    }

                    if (sql.toLowerCase().startsWith("use")||sql.toLowerCase().startsWith("set")
                            || BatchSqlExeService.CACHE_LAZY_SQL_PATTEN.matcher(sql.toLowerCase()).matches()) {
                        jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, dtUicUserId, eJobType, database, sql, connection);
                        continue;
                    }

                    if (BatchSqlExeService.CACHE_LAZY_SQL_PATTEN.matcher(sql.toLowerCase().trim()).matches()){
                        //cache table 替换成create table
                        sql = sql.trim().replaceAll("(?i)^cache\\s+(lazy\\s+)?table","create table ");
                        SqlParseInfo sqlParseInfo = lineageService.parseSqlInfo(sql, projectEngine.getEngineIdentity(), dataSourceType.getVal()).getData();
                        cacheTable.add(String.format("%s.%s", sqlParseInfo.getMainTable().getDb(), sqlParseInfo.getMainTable().getName()));
                        continue;
                    }
                    //改为前置解析 如果解析出的表包含cache表  跳过explain解析
                    List<Table> tables = lineageService.parseTables(sql, projectEngine.getEngineIdentity(), dataSourceType.getVal()).getData();
                    if (CollectionUtils.isNotEmpty(tables)){
                        List<String> allTable = tables.stream().map(bean -> String.format("%s.%s", bean.getDb(), bean.getName())).collect(Collectors.toList());
                        boolean flag = false;
                        for (String tableName : cacheTable) {
                            if (allTable.contains(tableName)) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            continue;
                        }
                    }

                    // 如果存在自定义函数创建，需要优先执行
                    if (sql.trim().toLowerCase().startsWith(CREATE_TEMP_FUNCTION)) {
                        jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, dtUicUserId, eJobType, database, sql, connection);
                        continue;
                    }

                    String explainSql = "explain " + sql;
                    log.info("sql:{}", explainSql);
                    List<List<Object>> lists = jdbcServiceImpl.executeQueryWithVariables(dtuicTenantId, dtUicUserId, eJobType, database, explainSql, Collections.EMPTY_LIST, connection);
                    if (CollectionUtils.isNotEmpty(lists) && lists.size() > 1) {
                        for (int i = 1; i < lists.size(); i++) {
                            List<Object> list = lists.get(i);
                            String result = list.get(0).toString();
                            if (StringUtils.isNotEmpty(result) && result.matches(SQL_EXCEPTION_REDEX)) {
                                if (result.contains(TABLE_NOT_FOUND)) {
                                    Matcher matcher = TABLE_NOT_FOUND_PATTERN.matcher(result);
                                    if (matcher.find()) {
                                        String table = matcher.group("table");
                                        if (CollectionUtils.isEmpty(createTables) || !createTables.stream().anyMatch(s -> table.contains(s))) {
                                            throw new RdosDefineException(result);
                                        }
                                    }
                                } else {
                                    throw new RdosDefineException(result);
                                }
                            }
                        }
                    }
                    ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
                    parseColumnLineageParam.setSql(sql);
                    parseColumnLineageParam.setDefaultDb(projectEngine.getEngineIdentity());
                    parseColumnLineageParam.setDataSourceType(dataSourceType.getVal());
                    parseColumnLineageParam.setTableColumnsMap(Maps.newHashMap());
                    ColumnLineageParseInfo columnLineageParseInfo = lineageService.parseColumnLineage(parseColumnLineageParam).getData();
                    if (CreateTableTypeList.contains(columnLineageParseInfo.getSqlType())) {
                        createTables.add(columnLineageParseInfo.getMainTable().getName());
                    }
                    ParseResult parseResult = ParseResultUtils.convertParseResult(columnLineageParseInfo);
                    parseResults.add(parseResult);
                }
            } catch (RdosDefineException e) {
                log.error("", e);
                throw e;
            } catch (Exception e) {
                log.error("", e);
                throw new RdosDefineException("校验sql失败: " + e.getMessage(), ErrorCode.SQL_FORMAT_ERROR, e);
            } finally {
                DBUtil.closeDBResources(null, null, connection);
            }
        }
        return parseResults;
    }

}
