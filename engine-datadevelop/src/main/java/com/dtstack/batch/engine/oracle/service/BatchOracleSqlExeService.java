package com.dtstack.batch.engine.oracle.service;

import com.alibaba.fastjson.JSON;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchHiveSelectSqlDao;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.domain.User;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.impl.TenantService;
import com.dtstack.batch.service.impl.UserService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.engine.api.service.ActionService;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author shixi
 * @date 2020-02-20
 */
@Service
public class BatchOracleSqlExeService implements ISqlExeService {

    public static Logger LOG = LoggerFactory.getLogger(BatchOracleSqlExeService.class);

    private String limitSQL = "SELECT * FROM (%s) t where  rownum<= %s";

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserService userService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private BatchHiveSelectSqlDao batchHiveSelectSqlDao;

    @Autowired
    ActionService actionService;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Override
    public void directExecutionSql(Long dtUicTenantId,Long dtUicUserId, String dbName, String sql) {
        if (!StringUtils.isEmpty(dbName)) {
            jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId,dtUicUserId, EJobType.ORACLE_SQL,dbName,sql);
        }
    }

    @Override
    public ExecuteResultVO executeSql(ExecuteContent content) {
        Long projectId = content.getProjectId();
        Long tenantId = content.getTenantId();
        Long userId = content.getUserId();
        ParseResult parseResult = content.getParseResult();
        String schema = content.getDatabase();
        if (StringUtils.isBlank(schema)) {
            ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.ORACLE.getType());
            if (Objects.isNull(projectDb)) {
                throw new RdosDefineException("oracle引擎不能为空");
            }
            schema = projectDb.getEngineIdentity();
        }

        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();

        Long dtuicTenantId = content.getDtuicTenantId();
        if (Objects.isNull(dtuicTenantId)) {
            dtuicTenantId = tenantService.getDtuicTenantId(tenantId);
        }
        Long dtuicUserId = null;
        User user = userService.getUser(content.getUserId());
        if (Objects.nonNull(user)) {
            dtuicUserId = user.getDtuicUserId();
        }

        //删除语句统一在外层处理
        if (content.isExecuteSqlLater()) {
            result.setIsContinue(true);
            return result;
        }

        SqlType sqlType = parseResult.getSqlType();
        // 判断sql查询类别是否为有返回值的查询sql
        if(!SqlType.QUERY.equals(sqlType)){
            try {
                jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, dtuicUserId, EJobType.ORACLE_SQL, schema, content.getSql());
                result.setIsContinue(true);
                result.setStatus(TaskStatus.FINISHED.getStatus());
            } catch (Exception e) {
                result.setMsg(e.getMessage());
                result.setIsContinue(false);
                result.setStatus(TaskStatus.FAILED.getStatus());
            }
            return result;
        }

        try {
            //查询结果
            LOG.info("isEnd = {}", content.getIsEnd());
            List<List<Object>> lists;
            if (SqlType.QUERY.equals(sqlType)) {
                //预览仅显示1000条，“下载”获取完整结果
                String sql = SqlFormatUtil.formatSql(content.getSql());
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, dtuicUserId, EJobType.ORACLE_SQL, schema, sql, 1000);
                if (CollectionUtils.isNotEmpty(lists)) {
                    Map<Long, Map<String, String>> cols = buildColumnTitle(lists);
                    result.setJobId(addDownLoadJob(sql, userId, tenantId, projectId, cols));
                }
            } else {
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, dtuicUserId, EJobType.ORACLE_SQL, schema, SqlFormatUtil.formatSql(content.getSql()));
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

    /**
     * 添加一个DownLoad任务信息
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
        batchHiveSelectSql.setJobId(actionService.generateUniqueSign().getData());
        batchHiveSelectSql.setSqlText(sql);
        batchHiveSelectSql.setUserId(userId);
        batchHiveSelectSql.setEngineType(MultiEngineType.ORACLE.getType());
        batchHiveSelectSql.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        batchHiveSelectSql.setProjectId(projectId);
        batchHiveSelectSql.setTenantId(tenantId);
        batchHiveSelectSql.setTempTableName("");

        //下载时候 列表匹配
        batchHiveSelectSql.setParsedColumns(JSON.toJSONString(cols));
        batchHiveSelectSqlDao.insert(batchHiveSelectSql);
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
                .replace("\r",  " ").trim();
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        return sqlText;
    }

    @Override
    public List<ParseResult> checkMulitSqlSyntax(Long dtuicTenantId, String sqlText, Long userId, Long projectId, String taskParam) {
        return new ArrayList<>();
    }

    @Override
    public List<ParseResult> parseLineageFromSqls(List<String> sqls, Long tenantId, Long projectId, String dbName, Long dtUicTenantId) {
        return new ArrayList<>();
    }
}
