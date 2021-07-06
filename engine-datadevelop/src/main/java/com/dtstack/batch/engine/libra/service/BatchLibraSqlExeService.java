package com.dtstack.batch.engine.libra.service;

import com.alibaba.fastjson.JSON;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchHiveSelectSqlDao;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.utils.ParseResultUtils;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.AppType;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.ColumnLineage;
import com.dtstack.engine.api.service.ActionService;
import com.dtstack.engine.api.service.LineageService;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.dtstack.sqlparser.common.utils.SqlRegexUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuebai
 * @date 2019-06-06
 */
@Service
public class BatchLibraSqlExeService implements ISqlExeService {

    public static Logger LOG = LoggerFactory.getLogger(BatchLibraSqlExeService.class);

    @Resource
    private TenantService tenantService;

    @Resource
    private ProjectEngineService projectEngineService;

    @Resource
    private IJdbcService jdbcServiceImpl;

    @Resource
    private BatchHiveSelectSqlDao batchHiveSelectSqlDao;

    @Autowired
    ActionService actionService;

	@Autowired
    private BatchSqlExeService batchSqlExeService;

	@Autowired
    private LineageService lineageService;

    private static final String DESC_TABLE = "select * from %s limit 0";

    private static Pattern CREATE_TEMP_TABLE_PATTERN = Pattern.compile("(?i)create\\s+temp\\s+table\\s+(?<db>[a-zA-Z0-9_]+\\.)*(?<table>[a-zA-Z0-9_]+).*");

    @Override
    public void directExecutionSql(Long dtUicTenantId, Long dtUicUserId,String dbName, String sql) {
        if (!StringUtils.isEmpty(dbName)) {
            jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId, null, EJobType.LIBRA_SQL, dbName, sql);
        }
    }


    /**
     * TODO jiangbo 参照hive实现完善
     */
    @Override
    public ExecuteResultVO executeSql(ExecuteContent content) {
        Long projectId = content.getProjectId();
        Long tenantId = content.getTenantId();
        Long userId = content.getUserId();
        ParseResult parseResult = content.getParseResult();

        ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.LIBRA.getType());
        if (projectDb == null) {
            throw new RdosDefineException("libra引擎不能为空");
        }

        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();

        //删除语句统一在外面处理
        if (content.isExecuteSqlLater()) {
            result.setIsContinue(true);
            return result;
        }

        String schema = projectDb.getEngineIdentity();
        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);

        SqlType sqlType = parseResult.getSqlType();
        // 判断sql查询类别是否为有返回值的查询sql
        if (!SqlType.getShowType().contains(sqlType)) {
            try {
                jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, EJobType.LIBRA_SQL, schema, content.getSql());

                result.setIsContinue(true);
                result.setStatus(TaskStatus.FINISHED.getStatus());
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                result.setMsg(e.getMessage());
                result.setIsContinue(false);
                result.setStatus(TaskStatus.FAILED.getStatus());
            }

            return result;
        }

        try {
            //查询结果
            LOG.info("isEnd = {}", content.getIsEnd());
            List<List<Object>> lists ;
            if (SqlType.QUERY.equals(sqlType)) {
                //预览仅显示1000条，“下载”获取完整结果
                result.setJobId(this.addDownLoadJob(projectId, tenantId, userId, parseResult, schema, dtuicTenantId));
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.LIBRA_SQL, schema, SqlFormatUtil.formatSql(content.getSql()), 1000);

            } else {
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.LIBRA_SQL, schema, SqlFormatUtil.formatSql(content.getSql()));
            }
            result.setStatus(TaskStatus.FINISHED.getStatus());
            batchSqlExeService.dealResultDoubleList(lists);
            result.setResult(lists);
            result.setIsContinue(true);
            if (SqlType.INSERT.equals(sqlType) || SqlType.DELETE.equals(sqlType)) {
                //插入语句 和 删除 没有结果
                result.setResult(null);
            }
            if (null != result.getResult() && result.getResult().size() == 0) {
                if (!SqlType.QUERY.equals(sqlType) && !SqlType.QUERY_NO_FROM.equals(sqlType)) {
                    result.setResult(null);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            result.setStatus(TaskStatus.FAILED.getStatus());
            result.setMsg(e.getMessage());
            result.setIsContinue(false);
        }
        return result;
    }

    @Override
    public ExecuteSqlParseVO batchExecuteSql(ExecuteContent content) {
        return null;
    }

    private String addDownLoadJob(Long projectId, Long tenantId, Long userId, ParseResult parseResult, String schema, Long dtuicTenantId) {
        BatchHiveSelectSql batchHiveSelectSql = new BatchHiveSelectSql();
        batchHiveSelectSql.setIsSelectSql(TempJobType.SELECT.getType());
		batchHiveSelectSql.setJobId(actionService.generateUniqueSign().getData());
        batchHiveSelectSql.setSqlText(SqlFormatUtil.formatSql(parseResult.getOriginSql()));
        batchHiveSelectSql.setUserId(userId);
        batchHiveSelectSql.setEngineType(MultiEngineType.LIBRA.getType());
        batchHiveSelectSql.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        batchHiveSelectSql.setProjectId(projectId);
        batchHiveSelectSql.setTenantId(tenantId);
        batchHiveSelectSql.setTempTableName(parseResult.getMainTable() == null || null == parseResult.getMainTable().getName() ? "" : parseResult.getMainTable().getName());
        Map<Long, Map<String, String>> cols = null;
        try {
            cols = this.getColumns(schema, dtuicTenantId, parseResult);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        //下载时候 列表匹配
        batchHiveSelectSql.setParsedColumns(JSON.toJSONString(cols));
        batchHiveSelectSqlDao.insert(batchHiveSelectSql);
        return batchHiveSelectSql.getJobId();
    }

    @Override
    public void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam) {
        // TODO jiangbo
    }

    private Map<Long, Map<String, String>> getColumns(String schema, Long dtuicTenantId, ParseResult parserResult) {
        Map<Long, Map<String, String>> cols = Maps.newHashMap();

        List<List<Object>> lists = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.LIBRA_SQL, schema, SqlFormatUtil.formatSql(parserResult.getOriginSql()), 1);
        if (CollectionUtils.isNotEmpty(lists)){
            Map<String, String> columns = new HashMap<>();
            for (Object column : lists.get(0)){
                columns.put(column.toString(), column.toString());
            }
            cols.put(-1L,columns);
        }
        return cols;
    }

    @Override
    public String process(String sqlText, String database) {
        sqlText = batchSqlExeService.removeComment(sqlText);
        sqlText = sqlText.replace("\n", " ")
                .replace("\r",  " ").trim();
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        return sqlText;
    }

    @Override
    public List<ParseResult> checkMulitSqlSyntax(Long dtuicTenantId, String sqlText, Long userId, Long projectId, String taskParam) {
        // TODO
        return ListUtils.EMPTY_LIST;
    }

    @Override
    public List<ParseResult> parseLineageFromSqls(List<String> sqls, Long tenantId, Long projectId, String dbName, Long dtUicTenantId) {
        List<ParseResult> parseResultList = new ArrayList<>();
        Map<String, List<Column>> tempTableColumns = execTempSqlAndQueryTempTableCol(sqls, dbName, dtUicTenantId);
        for (String sql : sqls) {
            ParseResult parseResult = parseSingleSql(sql, dbName, tempTableColumns);
            if (parseResult != null) {
                parseResultList.add(parseResult);
            }
        }

        dealTempTableTag(parseResultList, tempTableColumns);
        return parseResultList;
    }

    private void dealTempTableTag(List<ParseResult> parseResultList, Map<String, List<Column>> tempTableColumns) {
        Set<String> tempTables = new HashSet<>();
        for (String tempTable : tempTableColumns.keySet()) {
            tempTable = tempTable.substring(tempTable.lastIndexOf(".") + 1);
            tempTables.add(tempTable);
        }

        for (ParseResult parseResult : parseResultList) {
            if (CollectionUtils.isNotEmpty(parseResult.getColumnLineages())) {
                for (ColumnLineage columnLineage : parseResult.getColumnLineages()) {
                    if (tempTables.contains(columnLineage.getToTable())) {
                        columnLineage.setToTempTable(true);
                    }

                    if (tempTables.contains(columnLineage.getFromTable())) {
                        columnLineage.setFromTempTable(true);
                    }
                }
            }
        }
    }

    private ParseResult parseSingleSql(String sql, String dbName, Map<String, List<Column>> tempTableColumns) {
        try {
            ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
            parseColumnLineageParam.setSql(sql);
            parseColumnLineageParam.setAppType(AppType.RDOS.getType());
            parseColumnLineageParam.setDefaultDb(dbName);
            parseColumnLineageParam.setDataSourceType(DataSourceType.LIBRA.getVal());
            parseColumnLineageParam.setTableColumnsMap(Maps.newHashMap());
            ColumnLineageParseInfo columnLineageParseInfo = lineageService.parseColumnLineage(parseColumnLineageParam).getData();
            ParseResult parseResult = ParseResultUtils.convertParseResult(columnLineageParseInfo);
            return ParseResultUtils.convertParseResult(parseResult);
        } catch (Exception e) {
            LOG.error("Parse sql [{}] error", sql, e);
        }
        return null;
    }

    private Map<String, List<Column>> execTempSqlAndQueryTempTableCol(List<String> sqls, String dbName, Long dtUicTenantId) {
        Map<String, List<Column>> tempTableColumns = new HashMap<>();
        try (Connection connection = jdbcServiceImpl.getConnection(dtUicTenantId, null, EJobType.LIBRA_SQL, dbName);) {
            for (String sql : sqls) {
                if (SqlRegexUtil.isCreateTemp(sql)) {
                    jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId, null, EJobType.LIBRA_SQL, dbName, sql, connection);
                    tempTableColumns.putAll(getTempTableColumn(dtUicTenantId, sql, dbName));
                }
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
        return tempTableColumns;
    }

    private Map<String, List<Column>> getTempTableColumn(Long dtUicTenantId, String sql, String db) {
        Map<String, List<Column>> tableColumns = new HashMap<>();
        List<String> dbAndTable = parseTableAndDbFromSql(sql, db);
        db = dbAndTable.get(0);
        String table = dbAndTable.get(1);
        String execSql = String.format(DESC_TABLE, table);
        try {
            List<Column> columns = new ArrayList<>();
            List<List<Object>> lists = jdbcServiceImpl.executeQuery(dtUicTenantId, null, EJobType.LIBRA_SQL, db, execSql);
            if (CollectionUtils.isNotEmpty(lists)) {
                for (int i = 0; i < lists.get(0).size(); i++) {
                    columns.add(new Column(lists.get(0).get(i).toString(), i));
                }
            }
            tableColumns.put(String.format("%s.%s", db, table), columns);
        } catch (Exception e) {
            LOG.error("Execute sql [{}] error", execSql, e);
        }
        return tableColumns;
    }

    private List<String> parseTableAndDbFromSql(String originSql, String db) {
        String sql = originSql.replace("\r", " ")
                .replace("\n", " ")
                .replace("\t", " ");

        String table = null;
        Matcher matcher = CREATE_TEMP_TABLE_PATTERN.matcher(sql);
        if (matcher.find()) {
            String groupDb = matcher.group("db");
            if (StringUtils.isNotEmpty(groupDb)) {
                db = groupDb;
            }
            table = matcher.group("table");
        }

        List<String> dbAndTable = new ArrayList<>();
        dbAndTable.add(db);
        dbAndTable.add(table);

        return dbAndTable;
    }
}
