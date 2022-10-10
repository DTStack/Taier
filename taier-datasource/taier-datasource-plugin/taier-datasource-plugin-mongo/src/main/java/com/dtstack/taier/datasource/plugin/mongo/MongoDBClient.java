package com.dtstack.taier.datasource.plugin.mongo;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:24 2020/2/5
 * @Description：MongoDB 客户端
 */
@Slf4j
public class MongoDBClient extends AbsNoSqlClient {

    private static MongoExecutor mongoExecutor = MongoExecutor.getInstance();

    @Override
    public Boolean testCon(ISourceDTO iSource) {
        return MongoDBUtils.checkConnection(iSource);
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        return MongoDBUtils.getTableList(sourceDTO, queryDTO);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        return MongoDBUtils.getPreview(source, queryDTO);
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        return MongoDBUtils.getDatabaseList(iSource);
    }

    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO source, SqlQueryDTO queryDTO) {
        return mongoExecutor.execute(source, queryDTO);
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        return MongoDBUtils.isTableExistsInDatabase(source, tableName, dbName);
    }
}