package com.dtstack.taier.datasource.plugin.redis;

import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;

import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:38 2020/2/4
 * @Description：Redis 客户端
 */
public class RedisClient extends AbsNoSqlClient {
    @Override
    public Boolean testCon(ISourceDTO iSource) {
        return RedisUtils.checkConnection(iSource);
    }

    @Override
    public List<List<Object>> getPreview(ISourceDTO source, SqlQueryDTO queryDTO) {
        return RedisUtils.getPreview(source, queryDTO);
    }
}
