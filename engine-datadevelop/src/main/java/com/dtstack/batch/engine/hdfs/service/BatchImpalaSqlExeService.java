package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.enums.SelectSqlTypeEnum;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.service.table.impl.BatchHiveSelectSqlService;
import com.dtstack.batch.utils.ParseResultUtils;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.service.ActionService;
import com.dtstack.engine.api.service.LineageService;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 12:49 下午 2019/10/18
 * @Description：IMPALA 执行器
 */
@Service
public class BatchImpalaSqlExeService implements ISqlExeService {

    private static final Logger logger = LoggerFactory.getLogger(BatchImpalaSqlExeService.class);

    @Resource
    private TenantService tenantService;

    @Resource
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private BatchHiveSelectSqlService batchHiveSelectSqlService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private LineageService lineageService;

    @Override
    public void directExecutionSql(Long dtUicTenantId, Long dtUicUserId, String dbName, String sql) {
    }

    /**
     * 执行sql
     *
     * @param content
     * @return
     * @throws Exception
     */
    @Override
    public ExecuteResultVO executeSql(ExecuteContent content) {
        Long projectId = content.getProjectId();
        Long tenantId = content.getTenantId();
        Long userId = content.getUserId();
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        result.setSqlText(content.getSql());

        Long dtuicTenantId = tenantService.getDtuicTenantId(tenantId);

        // 进行sql解析
        beforeExecuteSql(content);

        //删除语句统一在外面处理
        if (content.isExecuteSqlLater()) {
            result.setIsContinue(true);
            return result;
        }

        ParseResult parseResult = content.getParseResult();
        // 判断sql查询类别是否为有返回值的查询sql
        if (!SqlType.getShowType().contains(parseResult.getSqlType())) {
            try {
                jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, EJobType.IMPALA_SQL, content.getDatabase(), content.getSql());
                result.setIsContinue(true);
                result.setStatus(TaskStatus.FINISHED.getStatus());
            } catch (Exception e) {
                logger.error(String.format("execute sql error，sql: %s", content.getSql()), e);
                result.setMsg(e.getMessage());
                result.setIsContinue(false);
                result.setStatus(TaskStatus.FAILED.getStatus());
            }
            return result;
        }

        try {
            List<List<Object>> lists ;
            // 判断是否是select Sql类型
            if (SqlType.QUERY.equals(parseResult.getSqlType())) {
                //预览仅显示1000条，“下载”获取完整结果
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.IMPALA_SQL, content.getDatabase(), SqlFormatUtil.formatSql(content.getSql()), 1000);
                if (CollectionUtils.isNotEmpty(lists)) {
                    Map<Long, Map<String, String>> cols = buildColumnTitle(lists);
                    result.setJobId(this.addDownLoadJob(SqlFormatUtil.formatSql(content.getSql()), userId, tenantId, projectId, cols));
                }
            } else {
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, null, EJobType.IMPALA_SQL, content.getDatabase(), SqlFormatUtil.formatSql(content.getSql()));
            }
            result.setStatus(TaskStatus.FINISHED.getStatus());
            result.setResult(lists);
            result.setIsContinue(true);
        } catch (Exception e) {
            logger.error(String.format("execute sql error，sql: %s", content.getSql()), e);
            result.setStatus(TaskStatus.FAILED.getStatus());
            result.setMsg(e.getMessage());
            result.setIsContinue(false);
        }
        return result;
    }

    /**
     * 执行sql前置处理
     *
     * @param content
     */
    private void beforeExecuteSql(ExecuteContent content) {
        ParseResult parseResult;
        try {
            ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
            parseColumnLineageParam.setSql(content.getSql());
            parseColumnLineageParam.setDefaultDb(content.getDatabase());
            parseColumnLineageParam.setDataSourceType(DataSourceType.IMPALA.getVal());
            parseColumnLineageParam.setTableColumnsMap(Maps.newHashMap());
            ColumnLineageParseInfo columnLineageParseInfo = lineageService.parseColumnLineage(parseColumnLineageParam).getData();
            parseResult = ParseResultUtils.convertParseResult(columnLineageParseInfo);
        } catch (Exception e) {
            logger.error(String.format("sql parse error，sql ：%s", content.getSql()), e);
            //防止空指针 先塞入默认值
            parseResult = new ParseResult();
            parseResult.setSqlType(SqlType.QUERY);
            parseResult.setFailedMsg(ExceptionUtils.getStackTrace(e));
            parseResult.setStandardSql(SqlFormatUtil.getStandardSql(content.getSql()));
        }
        content.setParseResult(parseResult);
    }


    @Override
    public ExecuteSqlParseVO batchExecuteSql(ExecuteContent content) {
        return null;
    }

    /**
     * 添加job信息，便于进行查询结果下载问题
     *
     * @param sql
     * @param userId
     * @param tenantId
     * @param projectId
     * @param cols
     * @return
     */
    private String addDownLoadJob(String sql, Long userId, Long tenantId, Long projectId, Map<Long, Map<String, String>> cols) {
        BatchHiveSelectSql batchHiveSelectSql = new BatchHiveSelectSql();
        batchHiveSelectSql.setIsSelectSql(TempJobType.SELECT.getType());
        batchHiveSelectSql.setSqlText(sql);
        batchHiveSelectSql.setUserId(userId);
        batchHiveSelectSql.setEngineType(MultiEngineType.HADOOP.getType());
        batchHiveSelectSql.setProjectId(projectId);
        batchHiveSelectSql.setTenantId(tenantId);
        batchHiveSelectSql.setJobId(actionService.generateUniqueSign().getData());
        batchHiveSelectSql.setTempTableName("IMPALA_NONE_TEMP");
        batchHiveSelectSql.setOtherType(SelectSqlTypeEnum.IMPALA.getType());

        //下载时候 列表匹配
        batchHiveSelectSql.setParsedColumns(JSON.toJSONString(cols));
        batchHiveSelectSqlService.insert(batchHiveSelectSql);
        return batchHiveSelectSql.getJobId();
    }

    /**
     * 构建结果下载时excel的title
     *
     * @param lists
     * @return
     */
    private Map<Long, Map<String, String>> buildColumnTitle(List<List<Object>> lists) {
        Map<Long, Map<String, String>> columns = new HashMap<>();
        if (CollectionUtils.isNotEmpty(lists)) {
            Map<String, String> c = new HashMap<>();
            for (Object column : lists.get(0)) {
                c.put(column.toString(), column.toString());
            }
            columns.put(0L, c);
        }
        return columns;
    }

    @Override
    public void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam) {

    }

    @Override
    public String process(String sqlText, String database) {
        sqlText = batchSqlExeService.removeComment(sqlText);
        sqlText = sqlText.replace("\n", " ")
                .replace("\r", " ").trim();
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        return sqlText;
    }

    @Override
    public List<ParseResult> checkMulitSqlSyntax(Long dtuicTenantId, String sqlText, Long userId, Long projectId, String taskParam) {
        return ListUtils.EMPTY_LIST;
    }

    @Override
    public List<ParseResult> parseLineageFromSqls(List<String> sqls, Long tenantId, Long projectId, String dbName, Long dtUicTenantId) {
        return ListUtils.EMPTY_LIST;
    }
}
