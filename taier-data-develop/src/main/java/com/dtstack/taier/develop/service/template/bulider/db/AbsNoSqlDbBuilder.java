package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.exception.DtCenterDefException;

import java.util.List;

public abstract class AbsNoSqlDbBuilder implements DbBuilder {

    @Override
    public IClient getClient() {
        return ClientCache.getClient(getDataSourceType().getVal());
    }

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        throw new DtCenterDefException("暂不支持的数据源类型");
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        throw new DtCenterDefException("暂不支持的数据源类型");
    }

    @Override
    public List<String> listTablesBySchema(String schema, String tableNamePattern, ISourceDTO sourceDTO, String db) {
        throw new DtCenterDefException("暂不支持的数据源类型");
    }

}