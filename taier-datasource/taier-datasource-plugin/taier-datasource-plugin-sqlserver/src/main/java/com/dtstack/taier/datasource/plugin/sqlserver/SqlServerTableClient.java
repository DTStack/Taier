package com.dtstack.taier.datasource.plugin.sqlserver;

import com.dtstack.taier.datasource.plugin.rdbms.AbsTableClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SqlServerTableClient extends AbsTableClient {

    private static final String ADD_COLUMN_COMMENT_SQL = "exec sp_addextendedproperty N'MS_Description', N'%s', N'SCHEMA', N'%s', N'TABLE', N'%s', N'COLUMN', N'%s'";

    private static final String ADD_COLUMN_SQL = "ALTER TABLE %s ADD %s %s";

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.SQLServer;
    }

    @Override
    public List<String> showPartitions(ISourceDTO source, String tableName) {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Boolean alterTableParams(ISourceDTO source, String tableName, Map<String, String> params) {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Boolean renameTable(ISourceDTO source, String oldTableName, String newTableName) {
        throw new SourceException("The method is not supported");
    }

    @Override
    public Boolean dropTable(ISourceDTO source, String tableName) {
        throw new SourceException("The method is not supported");
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
            String commentSql = String.format(ADD_COLUMN_COMMENT_SQL, schema, columnMetaDTO.getTableName(), columnMetaDTO.getColumnName(), columnMetaDTO.getColumnComment());
            executeSqlWithoutResultSet(source, commentSql);
        }
        return true;
    }

}
