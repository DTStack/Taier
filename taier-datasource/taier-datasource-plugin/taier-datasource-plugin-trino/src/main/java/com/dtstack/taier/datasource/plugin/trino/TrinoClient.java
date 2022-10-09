package com.dtstack.taier.datasource.plugin.trino;

import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.plugin.trino.download.TrinoDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TrinoSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * trino 数据源客户端，目前 catalog 信息必须要在url指定，schema信息可以不指定
 *
 * @author ：wangchuan
 * date：Created in 下午2:21 2021/9/9
 * company: www.dtstack.com
 */
@Slf4j
public class TrinoClient extends AbsRdbmsClient {

    // 模糊查询schema
    private static final String SHOW_DB_LIKE = "SHOW SCHEMAS LIKE '%s'";

    // 创建当前连接catalog下的schema
    private static final String CREATE_SCHEMA = "CREATE SCHEMA \"%s\"";

    // 创建指定catalog下的schema
    private static final String CREATE_SCHEMA_IN_CATALOG = "CREATE SCHEMA \"%s\".\"%s\"";

    // 判断table是否在schema中 ，不过滤视图
    private static final String TABLE_IS_IN_SCHEMA = "SELECT table_name FROM information_schema.tables WHERE table_schema='%s' AND table_name = '%s'";

    // 获取trino 数据源列表
    private static final String SHOW_CATALOG_SQL = "SHOW CATALOGS";

    // 获取当前数据源指定catalog的schema
    private static final String SHOW_SCHEMA_SQL = "SHOW SCHEMAS IN \"%s\"";

    // 获取当前数据源的schema
    private static final String SHOW_SCHEMA_DEFAULT_SQL = "SHOW SCHEMAS";

    // 获取当前数据源下的表
    private static final String SHOW_TABLE_SQL = "SHOW TABLES";

    // 获取当前数据源下的表，支持模糊查询
    private static final String SHOW_TABLE_LIKE_SQL = "show tables like '%s'";

    // 表查询基本 sql
    private static final String TABLE_SEARCH_BASE_SQL = "SELECT %s as schema_table FROM information_schema.tables WHERE  1 = 1 ";

    // 查询所有表名，表名前拼接 schema 并增加双引号处理
    private static final String SCHEMA_TABLE_NAME = " '\"'||table_schema||'\".\"'||table_name||'\"' ";

    // 查询指定 schema 下的表名，表名前不拼接 schema
    private static final String TABLE_NAME = " table_name ";

    // 指定 schema 查询
    private static final String SCHEMA_LIKE_SQL = " and table_schema = '%s' ";

    // 表名模糊查询
    private static final String TABLE_NAME_LIKE_SQL = " and table_name like '%s' ";

    // 不包括视图
    private static final String TABLE_NO_VIEW_SQL = " and table_type = 'BASE TABLE' ";

    // 排序
    private static final String ORDER_BY_SQL = " order by schema_table ";

    // 条数限制
    private static final String LIMIT_SQL = " limit %s ";

    // 获取建表语句
    private static final String SHOW_CREATE_TABLE_SQL = "SHOW CREATE TABLE %s";

    // 表注释信息
    private static final String SHOW_TABLE_COLUMN_COMMENT = "DESCRIBE %s";

    // DESCRIBE 查出表字段的key
    private static final String DESCRIBE_COLUMN = "Column";

    // DESCRIBE 查出表注释的key
    private static final String DESCRIBE_COMMENT = "Comment";

    // 未设置schema读取表报错信息
    private static final String SCHEMA_MUST_BE_SET = "Schema must be specified";

    @Override
    protected ConnFactory getConnFactory() {
        return new TrinoConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.TRINO;
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        TrinoSourceDTO trinoSourceDTO = (TrinoSourceDTO) sourceDTO;
        // schema 先从queryDTO中获取
        String schema = StringUtils.isBlank(queryDTO.getSchema()) ? trinoSourceDTO.getSchema() : queryDTO.getSchema();
        if (StringUtils.isNotBlank(schema)) {
            return getTableListBySchema(sourceDTO, queryDTO);
        }
        try {
            String sql;
            if (StringUtils.isNotEmpty(queryDTO.getTableNamePattern())) {
                // 模糊查询
                sql = String.format(SHOW_TABLE_LIKE_SQL, addFuzzySign(queryDTO));
            } else {
                sql = SHOW_TABLE_SQL;
            }
            return SearchUtil.handleSearchAndLimit(queryWithSingleColumn(sourceDTO, queryDTO, sql, 1, "get table exception according to schema...", queryDTO.getLimit()), queryDTO);
        } catch (Exception e) {
            // 如果url 中没有指定到 schema，则获取当前catalog下的所有表，并拼接形式如 "schema"."table"
            if (e.getMessage().contains(SCHEMA_MUST_BE_SET)) {
                return getTableListBySchema(sourceDTO, queryDTO);
            }
            throw new SourceException(e.getMessage(), e);
        }
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        // trino 不支持获取表注释
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    @Override
    protected Map<String, String> getColumnComments(RdbmsSourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        // 查询某表字段注释的sql
        String queryColumnCommentSql = String.format(SHOW_TABLE_COLUMN_COMMENT, transferSchemaAndTableName(sourceDTO, queryDTO));
        log.info("The SQL executed by method getColumnComments is:{}", queryColumnCommentSql);
        List<Map<String, Object>> result = executeQuery(sourceDTO, SqlQueryDTO.builder().sql(queryColumnCommentSql).build());
        Map<String, String> columnComments = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(result)) {
            result.stream().filter(MapUtils::isNotEmpty).forEach(row -> {
                String column = MapUtils.getString(row, DESCRIBE_COLUMN);
                if (StringUtils.isNotBlank(column)) {
                    columnComments.putIfAbsent(column, MapUtils.getString(row, DESCRIBE_COMMENT));
                }
            });
        }
        return columnComments;
    }

    @Override
    public Boolean isDatabaseExists(ISourceDTO source, String schema) {
        if (StringUtils.isBlank(schema)) {
            throw new SourceException("database schema not null");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(SHOW_DB_LIKE, schema)).build()));
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name is not empty");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(TABLE_IS_IN_SCHEMA, dbName, tableName)).build()));
    }

    /**
     * 获取指定schema下的表，如果没有填schema，则获取全部schema的表
     *
     * @param sourceDTO 数据源信息
     * @param queryDTO  查询条件
     * @return 查询sql
     */
    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        TrinoSourceDTO trinoSourceDTO = (TrinoSourceDTO) sourceDTO;
        // schema 先从queryDTO中获取
        String schema = StringUtils.isBlank(queryDTO.getSchema()) ? trinoSourceDTO.getSchema() : queryDTO.getSchema();
        StringBuilder tableSearchSql = new StringBuilder();
        String tName = StringUtils.isNotBlank(schema) ? TABLE_NAME : SCHEMA_TABLE_NAME;
        // 拼接查询表字段
        tableSearchSql.append(String.format(TABLE_SEARCH_BASE_SQL, tName));
        // 查询指定 schema
        if (StringUtils.isNotBlank(schema)) {
            tableSearchSql.append(String.format(SCHEMA_LIKE_SQL, schema));
        }

        // 表名模糊查询
        if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            tableSearchSql.append(String.format(TABLE_NAME_LIKE_SQL, addFuzzySign(queryDTO)));
        }

        // 不包括视图
        if (BooleanUtils.isFalse(queryDTO.getView())) {
            tableSearchSql.append(TABLE_NO_VIEW_SQL);
        }

        // 排序
        tableSearchSql.append(ORDER_BY_SQL);

        // 条数限制
        if (Objects.nonNull(queryDTO.getLimit())) {
            tableSearchSql.append(String.format(LIMIT_SQL, queryDTO.getLimit()));
        }
        return tableSearchSql.toString();
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        TrinoSourceDTO trinoSourceDTO = (TrinoSourceDTO) sourceDTO;
        // schema 先从queryDTO中获取
        String schema = StringUtils.isBlank(queryDTO.getSchema()) ? trinoSourceDTO.getSchema() : queryDTO.getSchema();
        try {
            return getColumnMetaData(connection, schema, queryDTO.getTableName());
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    private List<ColumnMetaDTO> getColumnMetaData(Connection conn, String schema, String tableName) {
        List<ColumnMetaDTO> columnList = new ArrayList<>();
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("DESCRIBE " + transferSchemaAndTableName(schema, tableName));
            while (resultSet.next()) {
                String colName = resultSet.getString("Column");
                String dataType = resultSet.getString("Type");
                String extra = resultSet.getString("Extra");
                String comment = resultSet.getString("Comment");
                ColumnMetaDTO metaDTO = new ColumnMetaDTO();
                metaDTO.setKey(colName);
                metaDTO.setType(dataType.trim());
                metaDTO.setComment(comment);
                if (StringUtils.isNotBlank(extra) && extra.contains("partition key")) {
                    metaDTO.setPart(true);
                }
                columnList.add(metaDTO);
            }
        } catch (SQLException e) {
            throw new SourceException(String.format("Failed to get meta information for the fields of table :%s. Please contact the DBA to check the database table information.",
                    transferSchemaAndTableName(schema, tableName)), e);
        } finally {
            DBUtil.closeDBResources(resultSet, stmt, null);
        }
        return columnList;
    }

    @Override
    public String getCreateTableSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        TrinoSourceDTO trinoSourceDTO = (TrinoSourceDTO) sourceDTO;
        // 对表名进行转换，schema如果传则不可以在表名中出现schema信息
        String tableName = transferSchemaAndTableName(trinoSourceDTO, queryDTO);
        List<String> result = queryWithSingleColumn(sourceDTO, null, String.format(SHOW_CREATE_TABLE_SQL, tableName), 1, "failed to get table create sql...");
        if (CollectionUtils.isEmpty(result)) {
            throw new SourceException("failed to get table create sql...");
        }
        return result.get(0);
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        TrinoSourceDTO trinoSource = (TrinoSourceDTO) source;

        String catalog = null;
        if (ReflectUtil.fieldExists(SqlQueryDTO.class, "catalog")
                && StringUtils.isNotBlank(queryDTO.getCatalog())) {
            catalog = queryDTO.getCatalog();
        } else if (ReflectUtil.fieldExists(TrinoSourceDTO.class, "catalog")) {
            catalog = trinoSource.getCatalog();
        }

        String sql = StringUtils.isBlank(catalog) ? SHOW_SCHEMA_DEFAULT_SQL : String.format(SHOW_SCHEMA_SQL, catalog);
        return queryWithSingleColumn(source, null, sql, 1, "get All database exception");
    }

    @Override
    protected String getCatalogSql() {
        return SHOW_CATALOG_SQL;
    }

    @Override
    public Boolean createDatabase(ISourceDTO source, String dbName, String comment) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database or schema cannot be empty");
        }

        TrinoSourceDTO trinoSource = (TrinoSourceDTO) source;
        String createSchemaSql;

        if (ReflectUtil.fieldExists(TrinoSourceDTO.class, "catalog")
                && StringUtils.isNotBlank(trinoSource.getCatalog())) {
            createSchemaSql = String.format(CREATE_SCHEMA_IN_CATALOG, trinoSource.getCatalog(), dbName);
        } else {
            createSchemaSql = String.format(CREATE_SCHEMA, dbName);
        }
        if (StringUtils.isEmpty(trinoSource.getSchema())) {
            trinoSource.setSchema(dbName);
        }
        return executeSqlWithoutResultSet(source, SqlQueryDTO.builder().sql(createSchemaSql).build());
    }

    /**
     * 处理 trino schema和tableName，不考虑表名和 schema 中有 . 的情况
     *
     * @param schema    schema名
     * @param tableName 表名
     * @return 处理后的数据
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (StringUtils.isBlank(schema) || tableName.contains(".")) {
            return tableName;
        }
        return String.format("\"%s\".\"%s\"", schema, tableName);
    }

    @Override
    public List<Map<String, Object>> executeQuery(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        try {
            return super.executeQuery(sourceDTO, queryDTO);
        } catch (SourceException e) {
            //当语句为预编译查询且catalog or schema or table名称以数字开头则不兼容
            if (e.getCause() instanceof SQLException
                    && e.getMessage().contains("Formatted query does not parse")) {
                throw new SourceException(
                        "Precompiled statements do not support starting with illegal characters such as numbers", e);
            }
            throw e;
        }
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        TrinoSourceDTO sourceDTO = (TrinoSourceDTO) source;
        if (StringUtils.isBlank(queryDTO.getSql())) {
            if (StringUtils.isBlank(queryDTO.getTableName())) {
                throw new SourceException("It must has tableName or sql when get downloader");
            }
            queryDTO.setSql(String.format("select * from \"%s\"", queryDTO.getTableName()));
        }
        TrinoDownloader downloader = new TrinoDownloader(getCon(sourceDTO), queryDTO.getSql());
        downloader.configure();
        return downloader;
    }

    @Override
    public Connection getCon(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        if (Objects.isNull(queryDTO)) {
            return super.getCon(sourceDTO, queryDTO);
        }
        TrinoSourceDTO trinoSourceDTO = (TrinoSourceDTO) sourceDTO;
        if (ReflectUtil.fieldExists(SqlQueryDTO.class, "catalog")
                && StringUtils.isNotBlank(queryDTO.getCatalog())) {
            trinoSourceDTO.setCatalog(queryDTO.getCatalog());
        }
        if (StringUtils.isNotBlank(queryDTO.getSchema())) {
            trinoSourceDTO.setSchema(queryDTO.getSchema());
        }
        return super.getCon(trinoSourceDTO, queryDTO);
    }
}
