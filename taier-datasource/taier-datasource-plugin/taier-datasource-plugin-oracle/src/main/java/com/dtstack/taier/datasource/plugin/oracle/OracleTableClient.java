package com.dtstack.taier.datasource.plugin.oracle;

import com.dtstack.taier.datasource.plugin.rdbms.AbsTableClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * oracle表操作相关接口
 *
 * @author ：wangchuan
 * date：Created in 10:57 上午 2020/12/3
 * company: www.dtstack.com
 */
@Slf4j
public class OracleTableClient extends AbsTableClient {

    // 获取表占用存储sql
    private static final String TABLE_SIZE_SQL = "select bytes AS table_size from user_segments where segment_name='%s' ";

    private static final String ADD_SCHEMA = " and TABLESPACE_NAME = upper('%s')";

    private static final String ADD_COLUMN_SQL = "ALTER TABLE %s ADD COLUMN %s %s";

    private static final String ADD_COLUMN_COMMENT_SQL = "COMMENT ON COLUMN %s IS '%s'";

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Oracle;
    }

    @Override
    public List<String> showPartitions(ISourceDTO source, String tableName) {
        throw new SourceException("The data source does not support get partition operation！");
    }

    @Override
    protected String getDropTableSql(String tableName) {
        return String.format("drop table if exists %s", tableName);
    }

    /**
     * 更改表相关参数，暂时只支持更改表注释
     * @param source 数据源信息
     * @param tableName 表名
     * @param params 修改的参数，map集合
     * @return 执行结果
     */
    @Override
    public Boolean alterTableParams(ISourceDTO source, String tableName, Map<String, String> params) {
        String comment = params.get("comment");
        log.info("update table comment，comment：{}！", comment);
        if (StringUtils.isEmpty(comment)) {
            return true;
        }
        String alterTableParamsSql = String.format("comment on table %s is '%s'", tableName, comment);
        return executeSqlWithoutResultSet(source, alterTableParamsSql);
    }

    @Override
    protected String getTableSizeSql(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
            return String.format(TABLE_SIZE_SQL, tableName);
        }

        return String.format(TABLE_SIZE_SQL, schema) + ADD_SCHEMA;
    }


    /**
     * 添加表列名
     *
     * @param source
     * @param columnMetaDTO
     * @return
     */
    protected Boolean addTableColumn(ISourceDTO source, UpsertColumnMetaDTO columnMetaDTO) {
        RdbmsSourceDTO rdbmsSourceDTO = (RdbmsSourceDTO) source;
        String schema = StringUtils.isNotBlank(columnMetaDTO.getSchema()) ? columnMetaDTO.getSchema() : rdbmsSourceDTO.getSchema();
        String tableName = transferSchemaAndTableName(schema, columnMetaDTO.getTableName());
        String sql = String.format(ADD_COLUMN_SQL, tableName, columnMetaDTO.getColumnName(), columnMetaDTO.getColumnType());
        executeSqlWithoutResultSet(source, sql);
        if (StringUtils.isNotEmpty(columnMetaDTO.getColumnComment())) {
            String commentSql = String.format(ADD_COLUMN_COMMENT_SQL, transformTableColumn(tableName, columnMetaDTO.getColumnName()), columnMetaDTO.getColumnComment());
            executeSqlWithoutResultSet(source, commentSql);
        }
        return true;
    }

}
