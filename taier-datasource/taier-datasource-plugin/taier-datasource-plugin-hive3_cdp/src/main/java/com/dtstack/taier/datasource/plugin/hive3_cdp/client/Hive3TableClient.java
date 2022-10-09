package com.dtstack.taier.datasource.plugin.hive3_cdp.client;

import com.dtstack.taier.datasource.plugin.hive3_cdp.HiveConnFactory;
import com.dtstack.taier.datasource.plugin.rdbms.AbsTableClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * hive表操作相关接口
 *
 * @company: www.dtstack.com
 * @Author ：qianyi
 * @Date ：Created in 14:03 2021/05/13
 * @Description：Hive3
 */
@Slf4j
public class Hive3TableClient extends AbsTableClient {

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.HIVE;
    }

    private static final String TABLE_IS_VIEW_SQL = "desc formatted %s";

    private static final String ADD_COLUMN_SQL = "alter table %s add columns(%s %s comment '%s')";

    @Override
    public Boolean isView(ISourceDTO source, String schema, String tableName) {
        checkParamAndSetSchema(source, schema, tableName);
        String sql = String.format(TABLE_IS_VIEW_SQL, tableName);
        List<Map<String, Object>> result = executeQuery(source, sql);
        if (CollectionUtils.isEmpty(result)) {
            throw new SourceException(String.format("Execute to determine whether the table is a view sql result is empty，sql：%s", sql));
        }
        String tableType = "";
        for (Map<String, Object> row : result) {
            String colName = MapUtils.getString(row, "col_name");
            if (StringUtils.containsIgnoreCase(colName, "Table Type")) {
                tableType = MapUtils.getString(row, "data_type");
                break;
            }
        }
        return StringUtils.containsIgnoreCase(tableType, "VIEW");
    }

    /**
     * 添加表字段
     *
     * @param source
     * @param columnMetaDTO
     * @return
     */
    protected Boolean addTableColumn(ISourceDTO source, UpsertColumnMetaDTO columnMetaDTO) {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        String schema = StringUtils.isNotBlank(columnMetaDTO.getSchema()) ? columnMetaDTO.getSchema() : rdbmsSourceDTO.getSchema();
        String comment = StringUtils.isNotBlank(columnMetaDTO.getColumnComment()) ? columnMetaDTO.getColumnComment() : "";
        String sql = String.format(ADD_COLUMN_SQL, transferSchemaAndTableName(schema, columnMetaDTO.getTableName()), columnMetaDTO.getColumnName(), columnMetaDTO.getColumnType(), comment);
        return executeSqlWithoutResultSet(source, sql);
    }
}
