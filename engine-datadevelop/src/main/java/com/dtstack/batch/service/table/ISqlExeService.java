package com.dtstack.batch.service.table;

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;

import java.util.List;

/**
 * Reason:
 * Date: 2019/5/13
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ISqlExeService {

    /**
     * 执行sql,插件内部逻辑，需要根据sql类型做处理
     */
    ExecuteResultVO executeSql(ExecuteContent content) throws Exception;

    /**
     * 批量执行sparkSql；解决高级运行不允许sparkSql直连spark ThirftServer问题
     * @param content
     */
    ExecuteSqlParseVO batchExecuteSql(ExecuteContent content) throws Exception;

    void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam);

    /**
     * sql 语句整理
     * 去掉注释
     * 拼接上对应的schema
     * @param sqlText
     * @param database
     * @return
     */
    String process(String sqlText, String database);

}
