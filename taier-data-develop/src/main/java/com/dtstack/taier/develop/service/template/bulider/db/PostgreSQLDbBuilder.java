package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author ：wangchuan
 * date：Created in 上午10:23 2020/8/14
 * company: www.dtstack.com
 */
@Component
public class PostgreSQLDbBuilder extends AbsRdbmsDbBuilder {

    private static Pattern sys = Pattern.compile("^pg_|gp_toolkit|information_schema");

    @Override
    public List<String> listSchemas(ISourceDTO sourceDTO, String db) {
        List<String> allDatabases = super.listSchemas(sourceDTO, db);
        return getSchemaList(allDatabases, sys);
    }

    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.PostgreSQL;
    }

    @Override
    public JSONObject pollPreview(String tableName, ISourceDTO sourceDTO) {
        throw new RdosDefineException("暂不支持的数据源类型");
    }

    @Override
    public List<JSONObject> listPollTableColumn(ISourceDTO sourceDTO, String tableName) {
        throw new RdosDefineException("暂不支持的数据源类型");
    }
}
