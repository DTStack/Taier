package com.dtstack.batch.engine.hive.service;

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.engine.hdfs.service.BatchSparkHiveSqlExeService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author sanyue
 * @date 2019/10/30
 */
@Service
public class BatchHiveSqlExeService extends BatchSparkHiveSqlExeService implements ISqlExeService {

    @Override
    public ExecuteResultVO executeSql(ExecuteContent executeContent) {
        return executeSql(executeContent, EJobType.HIVE_SQL);
    }

    @Override
    public ExecuteSqlParseVO batchExecuteSql(ExecuteContent content) {
        return null;
    }

    @Override
    public void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam) {
        checkSingleSqlSyntax(projectId, dtuicTenantId, sql, db, taskParam, EJobType.HIVE_SQL);
    }

    @Override
    public String process(String sqlText, String database) {
        return processSql(sqlText, database);
    }

}