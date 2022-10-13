package com.dtstack.taier.datasource.plugin.libra;

import com.dtstack.taier.datasource.api.dto.UpsertColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.plugin.rdbms.AbsTableClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Libra表操作相关接口
 *
 * @author ：wangchuan
 * date：Created in 10:57 上午 2020/12/3
 * company: www.dtstack.com
 */
@Slf4j
public class LibraTableClient extends AbsTableClient {

    // 获取表占用存储sql
    private static final String TABLE_SIZE_SQL = "SELECT pg_total_relation_size('\"' || table_schema || '\".\"' || table_name || '\"') AS table_size " +
            "FROM information_schema.tables where table_schema = '%s' and table_name = '%s'";

    // 判断表是不是视图表sql
    private static final String TABLE_IS_VIEW_SQL = "select viewname from pg_views where (schemaname ='public' or schemaname = '%s') and viewname = '%s'";

    private static final String ADD_COLUMN_SQL = "ALTER TABLE %s ADD COLUMN %s %s";

    private static final String ADD_COLUMN_COMMENT_SQL = "COMMENT ON COLUMN %s IS '%s'";

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.LIBRA;
    }

    @Override
    public List<String> showPartitions(ISourceDTO source, String tableName) {
        throw new SourceException("Libra not support get partition operation！");
    }

    @Override
    protected String getDropTableSql(String tableName) {
        return String.format("drop table if exists %s", tableName);
    }

    /**
     * 更改表相关参数，暂时只支持更改表注释
     *
     * @param source    数据源信息
     * @param tableName 表名
     * @param params    修改的参数，map集合
     * @return 执行结果
     */
    @Override
    public Boolean alterTableParams(ISourceDTO source, String tableName, Map<String, String> params) {
        String comment = params.get("comment");
        log.info("libra update comment，comment：{}！", comment);
        if (StringUtils.isEmpty(comment)) {
            return true;
        }
        String alterTableParamsSql = String.format("COMMENT ON TABLE %s IS '%s'", tableName, comment);
        return executeSqlWithoutResultSet(source, alterTableParamsSql);
    }

    @Override
    protected String getTableSizeSql(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
            throw new SourceException("schema is not empty");
        }
        return String.format(TABLE_SIZE_SQL, schema, tableName);
    }

    @Override
    public Boolean isView(ISourceDTO source, String schema, String tableName) {
        checkParamAndSetSchema(source, schema, tableName);
        schema = StringUtils.isNotBlank(schema) ? schema : "public";
        String sql = String.format(TABLE_IS_VIEW_SQL, schema, tableName);
        return CollectionUtils.isNotEmpty(executeQuery(source, sql));
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
