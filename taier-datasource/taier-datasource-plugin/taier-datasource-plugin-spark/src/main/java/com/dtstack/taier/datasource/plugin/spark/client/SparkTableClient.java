package com.dtstack.taier.datasource.plugin.spark.client;

import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.plugin.rdbms.AbsTableClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * spark表操作相关接口
 *
 * @author ：wangchuan
 * date：Created in 10:57 上午 2020/12/3
 * company: www.dtstack.com
 */
@Slf4j
public class SparkTableClient extends AbsTableClient {

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Spark;
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
        Collections.reverse(result);
        String tableType = "";
        for (Map<String, Object> row : result) {
            String colName = MapUtils.getString(row, "col_name");
            if (StringUtils.isNotBlank(colName) && StringUtils.startsWithIgnoreCase(colName.trim(), "Table Type")) {
                tableType = MapUtils.getString(row, "data_type");
                break;
            }
        }
        if (StringUtils.isEmpty(tableType)) {
            for (Map<String, Object> row : result) {
                String colName = MapUtils.getString(row, "col_name");
                if (StringUtils.isNotBlank(colName) && StringUtils.startsWithIgnoreCase(colName.trim(), "Type")) {
                    tableType = MapUtils.getString(row, "data_type");
                    break;
                }
            }
        }
        log.info("table schema :{},table name:{}, type:{}", schema, tableName, tableType);
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
