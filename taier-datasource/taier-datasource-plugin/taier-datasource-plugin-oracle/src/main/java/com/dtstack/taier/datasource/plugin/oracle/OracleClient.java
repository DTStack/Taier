package com.dtstack.taier.datasource.plugin.oracle;

import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.plugin.common.base.InsideTable;
import com.dtstack.taier.datasource.plugin.common.utils.ColumnUtil;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SchemaUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.OracleResultSetMetaData;
import oracle.sql.BLOB;
import oracle.sql.CLOB;
import oracle.xdb.XMLType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 12:00 2020/1/6
 * @Description：Oracle 客户端
 */
@Slf4j
public class OracleClient extends AbsRdbmsClient {

    private static String ORACLE_NUMBER_TYPE = "NUMBER";
    private static String ORACLE_NUMBER_FORMAT = "NUMBER(%d,%d)";
    private static String ORACLE_COLUMN_NAME = "COLUMN_NAME";
    private static String ORACLE_COLUMN_COMMENT = "COMMENTS";
    private static final String DONT_EXIST = "doesn't exist";

    private static final String DATABASE_QUERY = "select USERNAME from ALL_USERS order by USERNAME";

    private static final String TABLE_CREATE_SQL = "select dbms_metadata.get_ddl('TABLE','%s','%s') from dual";

    // 获取oracle默认使用的schema
    private static final String CURRENT_DB = "select sys_context('USERENV', 'CURRENT_SCHEMA') as schema_name from dual";

    /* -------------------------------------获取表操作相关sql------------------------------------- */
    // oracle获取指定schema下的表
    private static final String SHOW_TABLE_BY_SCHEMA_SQL = " SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER = '%s' %s ";
    // oracle获取指定schema下的视图
    private static final String SHOW_VIEW_BY_SCHEMA_SQL = " UNION SELECT VIEW_NAME FROM ALL_VIEWS WHERE OWNER = '%s' %s ";
    // oracle获取所有的schema下所有表 ： 新sql
    private static final String SHOW_ALL_TABLE_SQL = "SELECT '\"'||OWNER||'\"'||'.'||'\"'||TABLE_NAME||'\"' AS TABLE_NAME FROM ALL_TABLES WHERE OWNER != 'SYS' %s ";
    // oracle获取所有的schema下所有视图 ： 新sql
    private static final String SHOW_ALL_VIEW_SQL = " UNION SELECT '\"'||OWNER||'\"'||'.'||'\"'||VIEW_NAME||'\"' AS TABLE_NAME FROM ALL_VIEWS WHERE OWNER != 'SYS' %s ";
    // 表查询基础sql
    private static final String TABLE_BASE_SQL = "SELECT TABLE_NAME FROM (%s) WHERE 1 = 1 %s ";
    // 表名正则匹配模糊查询，忽略大小写
    private static final String TABLE_SEARCH_SQL = " AND UPPER(TABLE_NAME) LIKE UPPER('%s') ";
    // 视图正则匹配模糊查询，忽略大小写
    private static final String VIEW_SEARCH_SQL = " AND UPPER(VIEW_NAME) LIKE UPPER('%s') ";
    // 限制条数语句
    private static final String LIMIT_SQL = " AND ROWNUM <= %s ";
    // 获取表注释
    private static final String COMMENTS_SQL = "SELECT COMMENTS FROM  all_tab_comments WHERE TABLE_NAME = '%s' ";
    // 表注释获取条件限制
    private static final String COMMENTS_CONDITION_SQL = " AND OWNER = '%s' ";
    // 表注释字段
    private static final String ORACLE_TABLE_COMMENT = "COMMENTS";
    /* ----------------------------------------------------------------------------------------- */

    // cdb root
    private static final String CDB_ROOT = "CDB$ROOT";

    // 获取 oracle PDB 列表
    private static final String LIST_PDB = "SELECT NAME FROM v$pdbs WHERE 1 = 1 %s";

    // 表名正则匹配模糊查询，忽略大小写
    private static final String PDB_SEARCH_SQL = " AND NAME LIKE '%s' ";

    // 获取当前版本号
    private static final String SHOW_VERSION = "select BANNER from v$version";

    @Override
    protected ConnFactory getConnFactory() {
        return new OracleConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.Oracle;
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        return getTableListBySchema(sourceDTO, queryDTO);
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        String schema = StringUtils.isNotBlank(queryDTO.getSchema()) ? queryDTO.getSchema() : oracleSourceDTO.getSchema();
        StringBuilder commentQuerySql = new StringBuilder();
        commentQuerySql.append(String.format(COMMENTS_SQL, queryDTO.getTableName()));
        if (StringUtils.isNotBlank(schema)) {
            commentQuerySql.append(String.format(COMMENTS_CONDITION_SQL, schema));
        }
        commentQuerySql.append(String.format(LIMIT_SQL, 1));
        List<Map<String, Object>> queryResult = executeQuery(oracleSourceDTO, SqlQueryDTO.builder().sql(commentQuerySql.toString()).build());
        if (CollectionUtils.isEmpty(queryResult) || MapUtils.isEmpty(queryResult.get(0))) {
            return "";
        }
        return MapUtils.getString(queryResult.get(0), ORACLE_TABLE_COMMENT, "");
    }

    @Override
    public List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) sourceDTO;
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet rs = null;
        List<ColumnMetaDTO> columns = new ArrayList<>();
        try {
            statement = connection.createStatement();
            String schemaTable = transferSchemaAndTableName(oracleSourceDTO, queryDTO);
            String queryColumnSql = "select " + ColumnUtil.listToStr(queryDTO.getColumns()) + " from " + schemaTable
                    + " where 1=2";
            rs = statement.executeQuery(queryColumnSql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                columnMetaDTO.setKey(rsMetaData.getColumnName(i + 1));
                String flinkSqlType = OracleDbAdapter.mapColumnTypeJdbc2Java(rsMetaData.getColumnType(i + 1), rsMetaData.getPrecision(i + 1), rsMetaData.getScale(i + 1));
                columnMetaDTO.setType(flinkSqlType);
                columnMetaDTO.setPart(false);
                // 获取字段精度
                if (columnMetaDTO.getType().equalsIgnoreCase("decimal")
                        || columnMetaDTO.getType().equalsIgnoreCase("float")
                        || columnMetaDTO.getType().equalsIgnoreCase("double")
                        || columnMetaDTO.getType().equalsIgnoreCase("numeric")) {
                    columnMetaDTO.setScale(rsMetaData.getScale(i + 1));
                    columnMetaDTO.setPrecision(rsMetaData.getPrecision(i + 1));
                }

                columns.add(columnMetaDTO);
            }

            //获取字段注释
            Map<String, String> columnComments = getColumnComments(oracleSourceDTO, queryDTO);
            if (Objects.isNull(columnComments)) {
                return columns;
            }
            for (ColumnMetaDTO columnMetaDTO : columns) {
                if (columnComments.containsKey(columnMetaDTO.getKey())) {
                    columnMetaDTO.setComment(columnComments.get(columnMetaDTO.getKey()));
                }
            }
            return columns;

        } catch (SQLException e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(String.format(queryDTO.getTableName() + "table not exist,%s", e.getMessage()), e);
            } else {
                throw new SourceException(String.format("Failed to get meta information for the fields of table :%s. Please contact the DBA to check the database table information.",
                        queryDTO.getTableName()), e);
            }
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
    }

    @Override
    protected Map<String, String> getColumnComments(RdbmsSourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        Statement statement = null;
        ResultSet rs = null;
        Map<String, String> columnComments = new HashMap<>();
        try {
            statement = connection.createStatement();
            InsideTable tableInfo = SchemaUtil.getTableInfo(sourceDTO, queryDTO, getSpecialSign(), this::transferSchemaAndTableName);
            String schemaSql = StringUtils.isNotBlank(tableInfo.getSchema()) ? " and OWNER= '" + tableInfo.getSchema() + "'" : "";
            String queryColumnCommentSql =
                    "select * from all_col_comments where Table_Name =" + addSingleQuotes(tableInfo.getTable()) + schemaSql;
            rs = statement.executeQuery(queryColumnCommentSql);
            while (rs.next()) {
                String columnName = rs.getString(ORACLE_COLUMN_NAME);
                String columnComment = rs.getString(ORACLE_COLUMN_COMMENT);
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

    /**
     * 添加单引号
     *
     * @param str
     * @return
     */
    private static String addSingleQuotes(String str) {
        str = str.contains("'") ? str : String.format("'%s'", str);
        return str;
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        InsideTable tableInfo = SchemaUtil.getTableInfo(
                sourceDTO,
                queryDTO,
                getSpecialSign(),
                this::transferSchemaAndTableName
        );
        List<ColumnMetaDTO> columns = Lists.newArrayList();
        try (Connection connection = getCon(sourceDTO, queryDTO);
             ResultSet rs = connection
                     .getMetaData()
                     .getColumns(null, tableInfo.getSchema(), tableInfo.getTable(), null)) {

            while (rs.next()) {
                ColumnMetaDTO cmDTO = new ColumnMetaDTO();
                cmDTO.setKey(rs.getString(4));
                cmDTO.setType(dealNumberType(rs.getString(6), rs));
                // 获取字段精度
                if (cmDTO.getType().equalsIgnoreCase("decimal")
                        || cmDTO.getType().equalsIgnoreCase("float")
                        || cmDTO.getType().equalsIgnoreCase("double")
                        || cmDTO.getType().equalsIgnoreCase("numeric")) {
                    cmDTO.setPrecision(rs.getInt(7));
                    cmDTO.setScale(rs.getInt(9));
                }
                cmDTO.setPart(false);
                columns.add(cmDTO);
            }
        } catch (Exception e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(String.format(
                        queryDTO.getTableName() + "table not exist,%s", e.getMessage()), e);
            } else {
                throw new SourceException(String.format(
                        "Failed to get the meta information of the fields of the table: %s. " +
                                "Please contact the DBA to check the database and table information: %s",
                        queryDTO.getTableName(), e.getMessage()), e);
            }
        }

        //获取字段注释
        Map<String, String> columnComments = getColumnComments((OracleSourceDTO) sourceDTO, queryDTO);
        if (Objects.isNull(columnComments)) {
            return columns;
        }
        for (ColumnMetaDTO columnMetaDTO : columns) {
            if (columnComments.containsKey(columnMetaDTO.getKey())) {
                columnMetaDTO.setComment(columnComments.get(columnMetaDTO.getKey()));
            }
        }
        return columns;
    }

    /**
     * 处理 number type
     *
     * @param type 字段类型
     * @param rs   resultSet
     */
    private String dealNumberType(String type, ResultSet rs) throws SQLException {
        if (!ORACLE_NUMBER_TYPE.equalsIgnoreCase(type)) {
            return type;
        }
        int precision = rs.getInt(7);
        int scale = rs.getInt(9);
        // 精度为 0 不处理, 直接返回
        if (precision == 0) {
            return type;
        }
        return String.format(ORACLE_NUMBER_FORMAT, precision, scale);
    }

    @Override
    protected String doDealType(ResultSetMetaData rsMetaData, Integer los) throws SQLException {
        String type = super.doDealType(rsMetaData, los);
        if (!(rsMetaData instanceof OracleResultSetMetaData) || !ORACLE_NUMBER_TYPE.equalsIgnoreCase(type)) {
            return type;
        }

        int precision = rsMetaData.getPrecision(los + 1);
        int scale = rsMetaData.getScale(los + 1);
        if (precision == 126 && scale == -127) {
            return "FLOAT";
        }
        if (precision == 0) {
            return ORACLE_NUMBER_TYPE;
        }

        return String.format(ORACLE_NUMBER_FORMAT, precision, scale);
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) source;
        OracleDownloader oracleDownloader = new OracleDownloader(getCon(oracleSourceDTO), queryDTO.getSql(), oracleSourceDTO.getSchema());
        oracleDownloader.configure();
        return oracleDownloader;
    }

    @Override
    protected String dealSql(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) iSourceDTO;
        return "select * from " + transferSchemaAndTableName(oracleSourceDTO, sqlQueryDTO) + " where rownum <=" + sqlQueryDTO.getPreviewNum();
    }

    @Override
    protected Object dealResult(Object result) {
        result = super.dealResult(result);
        if (result instanceof XMLType) {
            try {
                XMLType xmlResult = (XMLType) result;
                return xmlResult.getString();
            } catch (Exception e) {
                log.error("oracle xml format transform string exception！", e);
                return "";
            }
        }

        // 处理 Blob 字段
        if (result instanceof BLOB) {
            try {
                BLOB blobResult = (BLOB) result;
                return blobResult.toString();
            } catch (Exception e) {
                log.error("oracle Blob format transform String exception！", e);
                return "";
            }
        }

        // 处理 Clob 字段
        if (result instanceof CLOB) {
            CLOB clobResult = (CLOB) result;
            try {
                return clobResult.toSQLXML().getString();
            } catch (Exception e) {
                log.error("oracle Clob format transform String exception！", e);
                return "";
            }
        }
        return result;
    }

    /**
     * 查询指定schema下的表，如果没有填schema，默认使用当前schema：支持条数限制、正则匹配
     *
     * @param sourceDTO 数据源信息
     * @param queryDTO  查询条件
     * @return 对应的sql语句
     */
    @Override
    protected String getTableBySchemaSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        // 构造表名模糊查询和条数限制sql
        String tableConstr = buildSearchSql(TABLE_SEARCH_SQL, queryDTO, queryDTO.getLimit());
        // 构造视图模糊查询和条数限制sql
        String viewConstr = buildSearchSql(VIEW_SEARCH_SQL, queryDTO, queryDTO.getLimit());
        String schema = queryDTO.getSchema();
        // schema若为空，则查询所有schema下的表
        String searchSql;
        if (StringUtils.isBlank(schema)) {
            log.info("schema is null，get all table！");
            searchSql = queryDTO.getView() ? String.format(SHOW_ALL_TABLE_SQL + SHOW_ALL_VIEW_SQL, tableConstr, viewConstr) : String.format(SHOW_ALL_TABLE_SQL, tableConstr);
        } else {
            searchSql = queryDTO.getView() ? String.format(SHOW_TABLE_BY_SCHEMA_SQL + SHOW_VIEW_BY_SCHEMA_SQL, schema, tableConstr, schema, viewConstr) : String.format(SHOW_TABLE_BY_SCHEMA_SQL, schema, tableConstr);
        }
        log.info("current used schema：{}", schema);

        return String.format(TABLE_BASE_SQL, searchSql, tableConstr);
    }

    /**
     * 构造模糊查询、条数限制sql
     *
     * @param tableSearchSql
     * @param queryDTO
     * @param limit
     * @return
     */
    private String buildSearchSql(String tableSearchSql, SqlQueryDTO queryDTO, Integer limit) {
        StringBuilder constr = new StringBuilder();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(tableSearchSql, addFuzzySign(queryDTO)));
        }
        if (Objects.nonNull(limit)) {
            constr.append(String.format(LIMIT_SQL, limit));
        }
        return constr.toString();
    }

    @Override
    public String getCreateTableSql(ISourceDTO source, SqlQueryDTO queryDTO) {
        OracleSourceDTO oracleSourceDTO = (OracleSourceDTO) source;
        String createTableSql = String.format(TABLE_CREATE_SQL, queryDTO.getTableName(), oracleSourceDTO.getSchema());
        queryDTO.setSql(createTableSql);
        return super.getCreateTableSql(source, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        throw new SourceException("Not Support");
    }

    @Override
    public String getShowDbSql() {
        return DATABASE_QUERY;
    }

    /**
     * 处理Oracle schema和tableName，适配schema和tableName中有.的情况
     *
     * @param schema
     * @param tableName
     * @return
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        // 表名中可以带点、不可以带双引号，表名可能为 schema."tableName"、"schema".tableName、
        // "schema"."tableName"、schema.tableName，第四种情况不做考虑
        if (!tableName.startsWith("\"") && !tableName.endsWith("\"")) {
            tableName = String.format("\"%s\"", tableName);
            // 如果tableName包含Schema操作，无其他方法，只能去判断长度
        } else if (indexCount(tableName, "\"") >= 4) {
            return tableName;
        }
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        if (!schema.startsWith("\"") && !schema.endsWith("\"")) {
            schema = String.format("\"%s\"", schema);
        }
        return String.format("%s.%s", schema, tableName);
    }

    /**
     * 通过采用indexOf + substring + 递归的方式来获取指定字符的数量
     *
     * @param text      指定要搜索的字符串
     * @param countText 指定要搜索的字符
     */
    private static int indexCount(String text, String countText) {
        // 根据指定的字符构建正则
        Pattern pattern = Pattern.compile(countText);
        // 构建字符串和正则的匹配
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        // 循环依次往下匹配
        // 如果匹配,则数量+1
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    @Override
    public List<String> getRootDatabases(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        // 执行后 将从 root 获取 PDB，否则获取到的为当前用户所在的 PDB
        try {
            // 构造 pdb 模糊查询和条数限制sql
            String pdbConstr = buildSearchSql(PDB_SEARCH_SQL, queryDTO, queryDTO.getLimit());
            // 切换到 cdb root，此处不关闭 connection
            DBUtil.executeSql(connection, String.format(SqlConstant.ALTER_PDB_SESSION, CDB_ROOT), queryDTO.getQueryTimeout());
            List<Map<String, Object>> pdbList = executeQuery(connection, SqlQueryDTO.builder().sql(String.format(LIST_PDB, pdbConstr)).build(), false);
            return pdbList.stream().map(row -> MapUtils.getString(row, "NAME")).collect(Collectors.toList());
        } catch (Exception e) {
            throw new SourceException(String.format("Error getting PDB list.%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    @Override
    protected String getVersionSql() {
        return SHOW_VERSION;
    }
}
