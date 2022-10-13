package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: es
 * @date 2021-11-11 15:39:03
 */
@Component
public class MongoDbBuilder implements DbBuilder {

    @Override
    public IClient getClient() {
        return ClientCache.getClient(getDataSourceType().getVal());
    }


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.MONGODB;
    }

    @Override
    public JSONObject pollPreview(String tableName, ISourceDTO sourceDTO) {
        return null;
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        throw new RdosDefineException("暂不支持的数据源类型");
    }

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        return null;
    }

    @Override
    public List<String> listTablesBySchema(String schema, String tableNamePattern, ISourceDTO sourceDTO, String db) {
        return null;
    }

    @Override
    public String buildConnMsgForSA(JSONObject dataJson) {
        return null;
    }

}
