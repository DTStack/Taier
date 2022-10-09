package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class SqlServerDbBuilder extends AbsRdbmsDbBuilder {

    private static final Pattern SYS = Pattern.compile("^cdc$");

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.SQLSERVER_2017_LATER;
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        return super.listPollTableColumn(sourceDTO, tableName);
    }

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        List<String> allDatabases = super.listSchemas(sourceDTO, db);
        return getSchemaList(allDatabases, SYS);
    }
}
