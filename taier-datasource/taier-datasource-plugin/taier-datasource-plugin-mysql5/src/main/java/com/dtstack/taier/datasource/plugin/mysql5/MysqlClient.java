package com.dtstack.taier.datasource.plugin.mysql5;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:18 2020/1/3
 * @Description：Mysql 客户端
 */
@Slf4j
public class MysqlClient extends AbsRdbmsClient {

    // 获取正在使用数据库
    private static final String CURRENT_DB = "select database()";

    // 模糊查询数据库
    private static final String SHOW_DB_LIKE = "show databases like '%s'";

    private static final String DONT_EXIST = "doesn't exist";

    // 获取指定数据库下的表
    private static final String SHOW_TABLE_BY_SCHEMA_SQL = "select table_name from information_schema.tables where table_schema='%s' and table_type in (%s) %s";

    // 视图
    private static final String VIEW = "'VIEW'";

    // 普通表
    private static final String BASE_TABLE = "'BASE TABLE'";

    // 表名正则匹配模糊查询
    private static final String SEARCH_SQL = " AND table_name LIKE '%s' ";

    // 限制条数语句
    private static final String LIMIT_SQL = " limit %s ";

    // 创建数据库
    private static final String CREATE_SCHEMA_SQL_TMPL = "create schema %s ";

    // 判断table是否在schema中，根据lower_case_table_names参数值不同匹配不同的sql
    private static final String TABLE_IS_IN_SCHEMA_0 = "select table_name from information_schema.tables where table_schema='%s' and BINARY table_name = '%s'";
    private static final String TABLE_IS_IN_SCHEMA_1 = "select table_name from information_schema.tables where table_schema='%s' and table_name = LOWER('%s')";
    private static final String TABLE_IS_IN_SCHEMA_2 = "SELECT small_name FROM ( SELECT lower( table_name ) AS small_name FROM information_schema.TABLES WHERE table_schema = '%s' ) AS temp WHERE temp.small_name = lower('%s')";
    // 获取当前版本号
    private static final String SHOW_VERSION = "select version()";

    // 获取配置的大小写是否敏感
    private static final String SHOW_CASE_VARIABLES = "show variables like '%lower_case_table_names%'";

    @Override
    protected ConnFactory getConnFactory() {
        return new MysqlConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.MySQL;
    }

    @Override
    protected String transferTableName(String tableName) {
        return tableName.contains("`") ? tableName : String.format("`%s`", tableName);
    }

    @Override
    public List<String> getTableList(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        return getTableListBySchema(iSource, queryDTO);
    }

    @Override
    protected String doDealType(ResultSetMetaData rsMetaData, Integer los) throws SQLException {
        int columnType = rsMetaData.getColumnType(los + 1);
        // text,mediumtext,longtext的jdbc类型名都是varchar，需要区分。不同的编码下，最大存储长度也不同。考虑1，2，3，4字节的编码

        if (columnType != Types.LONGVARCHAR) {
            return super.doDealType(rsMetaData, los);
        }

        int precision = rsMetaData.getPrecision(los + 1);
        if (precision >= 16383 && precision <= 65535) {
            return "TEXT";
        }

        if (precision >= 4194303 && precision <= 16777215) {
            return "MEDIUMTEXT";
        }

        if (precision >= 536870911 && precision <= 2147483647) {
            return "LONGTEXT";
        }

        return super.doDealType(rsMetaData, los);
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("show table status");
            while (resultSet.next()) {
                String dbTableName = resultSet.getString(1);

                if (dbTableName.equalsIgnoreCase(queryDTO.getTableName())) {
                    return resultSet.getString(DtClassConsistent.PublicConsistent.COMMENT);
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    queryDTO.getTableName()), e);
        } finally {
            DBUtil.closeDBResources(resultSet, statement, connection);
        }
        return "";
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        Mysql5SourceDTO mysql5SourceDTO = (Mysql5SourceDTO) source;
        MysqlDownloader mysqlDownloader = new MysqlDownloader(getCon(source), queryDTO.getSql(), mysql5SourceDTO.getSchema());
        mysqlDownloader.configure();
        return mysqlDownloader;
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException("Not Support");
    }

    @Override
    protected Map<String, String> getColumnComments(RdbmsSourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet rs = null;
        Map<String, String> columnComments = new HashMap<>();
        try {
            statement = connection.createStatement();
            String queryColumnCommentSql =
                    "show full columns from " + transferSchemaAndTableName(sourceDTO, queryDTO);
            rs = statement.executeQuery(queryColumnCommentSql);
            while (rs.next()) {
                String columnName = rs.getString("Field");
                String columnComment = rs.getString("Comment");
                columnComments.put(columnName, columnComment);
            }

        } catch (Exception e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(String.format(queryDTO.getTableName() + "table not exist,%s", e.getMessage()), e);
            } else {
                throw new SourceException(String.format("Failed to get the comment information of the field of the table: %s. Please contact the DBA to check the database and table information.",
                        queryDTO.getTableName()), e);
            }
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return columnComments;
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    @Override
    protected String getCreateDatabaseSql(String dbName, String comment) {
        return String.format(CREATE_SCHEMA_SQL_TMPL, dbName);
    }

    @Override
    public Boolean isDatabaseExists(ISourceDTO source, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name is not empty");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(SHOW_DB_LIKE, dbName)).build()));
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name is not empty");
        }

        //先查询大小写是否敏感,mysql默认情况下为0
        List<Map<String, Object>> mapList = executeQuery(source, SqlQueryDTO.builder().sql(SHOW_CASE_VARIABLES).build());
        if (CollectionUtils.isEmpty(mapList)) {
            throw new SourceException("mysql lower case setting is null");
        }
        Integer caseCode = MapUtils.getInteger(mapList.get(0), "Value", 0);

        String sql = "";
        // 0: sql加binary ， 1：tableName.toLowerCase() , 2:select lower(table_name) from
        if (Objects.equals(caseCode, 0)) {
            sql = String.format(TABLE_IS_IN_SCHEMA_0, dbName, tableName);
        } else if (Objects.equals(caseCode, 1)) {
            sql = String.format(TABLE_IS_IN_SCHEMA_1, dbName, tableName);
        } else if (Objects.equals(caseCode, 2)) {
            sql = String.format(TABLE_IS_IN_SCHEMA_2, dbName, tableName);
        }

        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(sql).build()));
    }

    /**
     * 获取指定schema下的表，如果没有填schema，默认使用当前schema。支持正则匹配查询、条数限制
     *
     * @param sourceDTO 数据源信息
     * @param queryDTO  查询条件
     * @return
     */
    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        String schema = queryDTO.getSchema();
        // 如果不传scheme，默认使用当前连接使用的schema
        if (StringUtils.isBlank(schema)) {
            log.info("schema is empty，get current used schema!");
            // 获取当前数据库
            try {
                schema = getCurrentDatabase(sourceDTO);
            } catch (Exception e) {
                throw new SourceException(String.format("get current used database error！,%s", e.getMessage()), e);
            }

        }
        log.info("current used schema：{}", schema);
        StringBuilder constr = new StringBuilder();
        if (StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(SEARCH_SQL, addFuzzySign(queryDTO)));
        }
        if (Objects.nonNull(queryDTO.getLimit())) {
            constr.append(String.format(LIMIT_SQL, queryDTO.getLimit()));
        }
        List<String> tableType = Lists.newArrayList(BASE_TABLE);
        // 获取视图
        if (BooleanUtils.isTrue(queryDTO.getView())) {
            tableType.add(VIEW);
        }
        return String.format(SHOW_TABLE_BY_SCHEMA_SQL, schema, String.join(",", tableType), constr.toString());
    }

    /**
     * 处理 schema和tableName，适配schema和tableName中有.的情况
     *
     * @param schema
     * @param tableName
     * @return
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (!tableName.startsWith("`") || !tableName.endsWith("`")) {
            tableName = String.format("`%s`", tableName);
        }
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        if (!schema.startsWith("`") || !schema.endsWith("`")) {
            schema = String.format("`%s`", schema);
        }
        return String.format("%s.%s", schema, tableName);
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }

    @Override
    protected Pair<Character, Character> getSpecialSign() {
        return Pair.of('`', '`');
    }
}
