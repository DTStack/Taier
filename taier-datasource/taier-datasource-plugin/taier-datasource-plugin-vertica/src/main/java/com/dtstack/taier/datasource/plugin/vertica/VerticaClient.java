package com.dtstack.taier.datasource.plugin.vertica;

import com.dtstack.taier.datasource.plugin.common.base.InsideTable;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SchemaUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * vertica client impl
 *
 * @author ：wangchuan
 * date：Created in 下午8:19 2020/12/8
 * company: www.dtstack.com
 */
@Slf4j
public class VerticaClient extends AbsRdbmsClient {

    /**
     * 展示特定 SCHEMA 的表
     */
    private static final String SHOW_TABLE_BY_SCHEMA_SQL = " SELECT table_name FROM tables WHERE table_schema = '%s' %s ";

    /**
     * 展示特定 SCHEMA 的视图
     */
    private static final String SHOW_VIEW_BY_SCHEMA_SQL =  " UNION SELECT table_name FROM views WHERE table_schema = '%s' %s ";

    /**
     * 获取所有的 schema 下所有表
     */
    private static final String SHOW_ALL_TABLE_SQL = "SELECT '\"'||table_schema||'\"'||'.'||'\"'||table_name||'\"' AS table_name FROM tables WHERE is_system_table = false %s ";

    /**
     * 获取所有的 schema 下所有视图
     */
    private static final String SHOW_ALL_VIEW_SQL = " UNION SELECT '\"'||table_schema||'\"'||'.'||'\"'||table_name||'\"' AS table_name FROM views WHERE is_system_view = false %s ";

    /**
     * 表查询基础 sql
     */
    private static final String TABLE_BASE_SQL = "SELECT table_name FROM (%s) AS tmp WHERE 1 = 1 %s ";

    /**
     * 表名正则匹配模糊查询，忽略大小写
     */
    private static final String TABLE_SEARCH_SQL = " AND table_name LIKE '%s' ";

    /**
     * 限制条数语句
     */
    private static final String LIMIT_SQL = " LIMIT %s ";

    /**
     * 查找 schema
     */
    private static final String SHOW_SCHEMA = "select schema_name from schemata where is_system_schema = false";

    /**
     * 查找表注释
     */
    private static final String SHOW_TABLE_COMMENT = "select comment from comments where object_name= '%s'";

    /**
     * 查找表注释
     */
    private static final String SHOW_TABLE_COMMENT_WITH_SCHEMA = "select comment from comments where object_name= '%s' and object_schema = '%s'";

    /**
     * 获取建表语句
     */
    private static final String CREATE_TABLE_SQL = "select export_objects('', '%s')";

    /**
     * 获取当前版本号
     */
    private static final String SHOW_VERSION = "select version()";

    @Override
    public String getCreateTableSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        String tableName = transferSchemaAndTableName(sourceDTO, queryDTO);
        queryDTO.setSql(String.format(CREATE_TABLE_SQL, tableName));
        return super.getCreateTableSql(sourceDTO, queryDTO);
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        return getTableListBySchema(sourceDTO, queryDTO);
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        try {
            InsideTable tableInfo = SchemaUtil.getTableInfo(sourceDTO, queryDTO, getSpecialSign(), this::transferSchemaAndTableName);
            String querySql;
            // 如果表传进来存在 schema 信息，需要过滤条件增加 schema 信息
            if (StringUtils.isNotBlank(tableInfo.getSchema())) {
                querySql = String.format(SHOW_TABLE_COMMENT_WITH_SCHEMA, tableInfo.getTable(), tableInfo.getSchema());
            } else {
                querySql = String.format(SHOW_TABLE_COMMENT, tableInfo.getTable());
            }
            Map<String, Object> commentMap = DBUtil.executeSql(connection, querySql).get(0);
            return MapUtils.getString(commentMap, "comment");
        } catch (Exception e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    queryDTO.getTableName()), e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        // 构造表名模糊查询和条数限制sql
        String tableConstr = buildSearchSql(TABLE_SEARCH_SQL, queryDTO);
        String schema = queryDTO.getSchema();
        // schema若为空，则查询所有schema下的表
        String searchSql;
        if (StringUtils.isBlank(schema)) {
            log.info("schema is null，get all table！");
            searchSql = queryDTO.getView() ? String.format(SHOW_ALL_TABLE_SQL + SHOW_ALL_VIEW_SQL, tableConstr, tableConstr) : String.format(SHOW_ALL_TABLE_SQL, tableConstr);
        } else {
            searchSql = queryDTO.getView() ? String.format(SHOW_TABLE_BY_SCHEMA_SQL + SHOW_VIEW_BY_SCHEMA_SQL, schema, tableConstr, schema, tableConstr) : String.format(SHOW_TABLE_BY_SCHEMA_SQL, schema, tableConstr);
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            searchSql = searchSql + String.format(LIMIT_SQL, queryDTO.getLimit());
        }
        log.info("current used schema：{}", schema);
        return String.format(TABLE_BASE_SQL, searchSql, tableConstr);
    }

    @Override
    public List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        List<ColumnMetaDTO> columnMetaData = getColumnMetaData(sourceDTO, queryDTO);
        for (ColumnMetaDTO columnMetaDatum : columnMetaData) {
            columnMetaDatum.setType(VerticaAdapter.mapColumnType2Flink(columnMetaDatum.getType()));
        }
        return columnMetaData;
    }

    /**
     * 构造模糊查询、条数限制sql
     *
     * @param tableSearchSql 查询 sql
     * @param queryDTO       查询条件
     * @return sql
     */
    private String buildSearchSql(String tableSearchSql, SqlQueryDTO queryDTO) {
        StringBuilder constr = new StringBuilder();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(tableSearchSql, addFuzzySign(queryDTO)));
        }
        return constr.toString();
    }

    @Override
    public String getShowDbSql() {
        return SHOW_SCHEMA;
    }

    @Override
    protected ConnFactory getConnFactory() {
        return new VerticaConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.VERTICA;
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }

    /**
     * 处理 schema和tableName，适配schema和tableName中有.的情况
     *
     * @param schema    schema
     * @param tableName table name
     * @return 处理后的 schema + table name
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (!tableName.startsWith("\"") || !tableName.endsWith("\"")) {
            tableName = String.format("\"%s\"", tableName);
        }
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        if (!schema.startsWith("\"") || !schema.endsWith("\"")) {
            schema = String.format("\"%s\"", schema);
        }
        return String.format("%s.%s", schema, tableName);
    }
}
