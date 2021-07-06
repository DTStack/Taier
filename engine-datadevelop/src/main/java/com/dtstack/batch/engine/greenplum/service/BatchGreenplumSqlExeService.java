package com.dtstack.batch.engine.greenplum.service;

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.exception.RdosDefineException;
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
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author chener
 * @Classname BatchGreenplumSqlExeService
 * @Description TODO
 * @Date 2020/5/20 19:17
 * @Created chener@dtstack.com
 */
@Service
public class BatchGreenplumSqlExeService implements ISqlExeService {

    public static Logger LOG = LoggerFactory.getLogger(BatchGreenplumSqlExeService.class);

    private String limitSQL = "SELECT * FROM (%s) t LIMIT %s";

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private UserService userService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Override
    public void directExecutionSql(Long dtUicTenantId, Long dtUicUserId, String dbName, String sql) throws Exception {
        try {
            jdbcServiceImpl.executeQueryWithoutResult(dtUicTenantId, dtUicUserId, EJobType.GREENPLUM_SQL, dbName, sql);
        } catch (Exception e) {
            throw new RdosDefineException(String.format("执行sql出现异常，原因是：%s", e.getMessage()), e);
        }
    }

    @Override
    public ExecuteResultVO executeSql(ExecuteContent content) {
        Long projectId = content.getProjectId();
        Long tenantId = content.getTenantId();
        ParseResult parseResult = content.getParseResult();
        String schema = content.getDatabase();
        if (StringUtils.isBlank(schema)) {
            ProjectEngine projectDb = projectEngineService.getProjectDb(projectId, MultiEngineType.GREENPLUM.getType());
            if (Objects.isNull(projectDb)) {
                throw new RdosDefineException("Greenplum引擎不能为空");
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

        //删除语句统一在外面处理
        if (content.isExecuteSqlLater()) {
            result.setIsContinue(true);
            return result;
        }

        SqlType sqlType = parseResult.getSqlType();
        // 判断sql查询类别是否为有返回值的查询sql
        if (!SqlType.getShowType().contains(sqlType)) {
            try {
                jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, dtuicUserId, EJobType.GREENPLUM_SQL, schema, content.getSql());
                result.setIsContinue(true);
                result.setStatus(TaskStatus.FINISHED.getStatus());
            } catch (Exception e) {
                LOG.error("", e);
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
                //预览仅显示1000条
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, dtuicUserId, EJobType.GREENPLUM_SQL, schema, SqlFormatUtil.formatSql(content.getSql()), 1000);

            } else {
                lists = jdbcServiceImpl.executeQuery(dtuicTenantId, dtuicUserId, EJobType.GREENPLUM_SQL, schema, SqlFormatUtil.formatSql(content.getSql()));
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
        throw new RdosDefineException("greenplum 不支持高级运行");
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
