package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author daojin
 * @program: dt-centet-datasync
 * @description: es
 * @date 2021-11-11 15:39:03
 */
@Component
public class EsDbBuilder implements DbBuilder{

    @Override
    public IClient getClient() {
        return ClientCache.getClient(getDataSourceType().getVal());
    }


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.ES7;
    }

    @Override
    public JSONObject pollPreview(String tableName, ISourceDTO sourceDTO) {
        return null;
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = new ArrayList<>();
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableName(tableName).build();
        List<ColumnMetaDTO> columnMetaDTOList = getClient().getColumnMetaData(sourceDTO, sqlQueryDTO);
        if (CollectionUtils.isNotEmpty(columnMetaDTOList)) {
            for (ColumnMetaDTO columnMetaDTO : columnMetaDTOList) {
                columns.add(JSON.parseObject(JSON.toJSONString(columnMetaDTO)));
            }
        }
        return columns;
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
