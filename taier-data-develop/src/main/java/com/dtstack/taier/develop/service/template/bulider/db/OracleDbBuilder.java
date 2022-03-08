package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.dto.source.OracleSourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class OracleDbBuilder extends AbsRdbmsDbBuilder {

    //过滤表
    private static Pattern sys = Pattern.compile("^SYS$|^SYSTEM$|^APEX_");
    //过滤列
    private static Pattern pollColumn = Pattern.compile("^DATE.*$|^VARCHAR.*$|^TIMESTAMP.*$|^NUMBER.*$");


    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.Oracle;
    }



    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        List<JSONObject> columns = super.listPollTableColumn(sourceDTO, tableName);
        return getByColumn(columns, pollColumn);
    }


    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        // 设置 pdb
        oracleSourceDTO.setPdb(db);
        List<String> schemaList = getClient().getAllDatabases(oracleSourceDTO, SqlQueryDTO.builder().build());
        return getSchemaList(schemaList, sys);
    }

    @Override
    public List<String> listTablesBySchema(String schema, String tableNamePattern, ISourceDTO sourceDTO, String db) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        // 设置 pdb
        oracleSourceDTO.setPdb(db);
        return getClient().getTableListBySchema(oracleSourceDTO, SqlQueryDTO.builder().schema(schema).tableNamePattern(tableNamePattern).limit(LIMIT_COUNT).build());
    }
}
