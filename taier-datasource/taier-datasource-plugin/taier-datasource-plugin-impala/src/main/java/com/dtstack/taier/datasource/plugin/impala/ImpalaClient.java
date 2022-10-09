package com.dtstack.taier.datasource.plugin.impala;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.enums.StoredType;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.common.utils.TableUtil;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ImpalaSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 20:16 2020/1/7
 * @Description：Impala 连接
 */
public class ImpalaClient extends AbsRdbmsClient {

    // 获取正在使用数据库
    private static final String CURRENT_DB = "select current_database()";

    private static final String SHOW_TABLE_SQL = "show tables %s";

    // 根据schema选表表名模糊查询
    private static final String SEARCH_SQL = " LIKE '%s' ";

    // 获取当前版本号
    private static final String SHOW_VERSION = "select version()";

    private static final String DONT_EXIST = "doesn't exist";

    @Override
    protected ConnFactory getConnFactory() {
        return new ImpalaConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.IMPALA;
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        StringBuilder constr = new StringBuilder();
        if (Objects.nonNull(queryDTO) && StringUtils.isNotBlank(queryDTO.getTableNamePattern())) {
            constr.append(String.format(SEARCH_SQL, addFuzzySign(queryDTO)));
        }
        // 获取表信息需要通过show tables 语句
        String sql = String.format(SHOW_TABLE_SQL, constr.toString());
        Statement statement = null;
        ResultSet rs = null;
        List<String> tableList = new ArrayList<>();
        try {
            statement = connection.createStatement();
            if (Objects.nonNull(queryDTO) && Objects.nonNull(queryDTO.getLimit())) {
                // 设置最大条数
                statement.setMaxRows(queryDTO.getLimit());
            }
            DBUtil.setFetchSize(statement, queryDTO);
            rs = statement.executeQuery(sql);
            int columnSize = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                tableList.add(rs.getString(columnSize == 1 ? 1 : 2));
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get table exception：%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        return getTableList(source, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        ImpalaSourceDTO impalaSourceDTO = (ImpalaSourceDTO) sourceDTO;
        // schema 先从queryDTO中获取
        String schema = StringUtils.isBlank(queryDTO.getSchema()) ? impalaSourceDTO.getSchema() : queryDTO.getSchema();
        try {
            return getColumnMetaData(connection, schema, queryDTO.getTableName(), queryDTO.getFilterPartitionColumns());
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    @Override
    public List<ColumnMetaDTO> getFlinkColumnMetaData(ISourceDTO iSource, SqlQueryDTO queryDTO) {
        Connection connection = getCon(iSource);
        ImpalaSourceDTO impalaSourceDTO = (ImpalaSourceDTO) iSource;
        Statement statement = null;
        ResultSet rs = null;
        List<ColumnMetaDTO> columns = new ArrayList<>();
        try {
            statement = connection.createStatement();
            String queryColumnSql = "select * from " + transferSchemaAndTableName(impalaSourceDTO, queryDTO)
                    + " where 1=2";
            rs = statement.executeQuery(queryColumnSql);
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
                ColumnMetaDTO columnMetaDTO = new ColumnMetaDTO();
                columnMetaDTO.setKey(rsMetaData.getColumnName(i + 1));
                String type = rsMetaData.getColumnTypeName(i + 1);
                int columnType = rsMetaData.getColumnType(i + 1);
                int precision = rsMetaData.getPrecision(i + 1);
                int scale = rsMetaData.getScale(i + 1);
                //postgresql类型转换
                String flinkSqlType = ImpalaSqlAdapter.mapColumnTypeJdbc2Java(columnType, precision, scale);
                if (StringUtils.isNotEmpty(flinkSqlType)) {
                    type = flinkSqlType;
                }
                columnMetaDTO.setType(type);
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
            return columns;

        } catch (SQLException e) {
            if (e.getMessage().contains(DONT_EXIST)) {
                throw new SourceException(String.format(queryDTO.getTableName() + "table not exist,%s", e.getMessage()), e);
            } else {
                throw new SourceException(String.format("Failed to get meta information for the fields of table :%s. Please contact the DBA to check the database table information.", queryDTO.getTableName()), e);
            }
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }

    }

    private List<ColumnMetaDTO> getColumnMetaData(Connection conn, String schema, String tableName, Boolean filterPartitionColumns) {
        List<ColumnMetaDTO> columnList = new ArrayList<>();
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            LinkedHashMap<String, JSONObject> colNameMap = new LinkedHashMap<>();
            stmt = conn.createStatement();
            //首先判断是否是kudu表 是kudu表直接用主键代替 isPart
            resultSet = stmt.executeQuery("DESCRIBE " + transferSchemaAndTableName(schema, tableName));
            int columnCnt = resultSet.getMetaData().getColumnCount();

            // kudu表
            if (columnCnt > 3) {
                while (resultSet.next()) {
                    columnList.add(dealResult(resultSet,
                            resultSet.getString(DtClassConsistent.PublicConsistent.PRIMARY_KEY)));
                }
                return columnList;
            }

            //hive表 继续获取分区字段 先关闭之前的 rs
            resultSet.close();
            resultSet = stmt.executeQuery("DESCRIBE formatted " + transferSchemaAndTableName(schema, tableName));
            while (resultSet.next()) {
                String colName = resultSet.getString(DtClassConsistent.PublicConsistent.NAME).trim();

                if (StringUtils.isEmpty(colName)) {
                    continue;
                }
                if (colName.startsWith("#") && colName.contains(DtClassConsistent.PublicConsistent.COL_NAME)) {
                    continue;

                }
                if (colName.startsWith("#") || colName.contains("Partition Information")) {
                    break;
                }

                if (StringUtils.isNotBlank(colName)) {
                    columnList.add(dealResult(resultSet, Boolean.FALSE));
                }
            }

            while (resultSet.next() && !filterPartitionColumns) {
                String colName = resultSet.getString(DtClassConsistent.PublicConsistent.NAME);
                if (StringUtils.isBlank(colName)) {
                    continue;
                }
                if (colName.startsWith("#") && colName.contains(DtClassConsistent.PublicConsistent.COL_NAME)) {
                    continue;
                }
                if (colName.contains("Detailed Table Information") || colName.contains("Database:")) {
                    break;
                }

                columnList.add(dealResult(resultSet, Boolean.TRUE));
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
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        ImpalaSourceDTO impalaSourceDTO = (ImpalaSourceDTO) sourceDTO;
        // schema 先从queryDTO中获取
        String schema = StringUtils.isBlank(queryDTO.getSchema()) ? impalaSourceDTO.getSchema() : queryDTO.getSchema();
        try {
            return getTableMetaComment(connection, schema, queryDTO.getTableName());
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    private String getTableMetaComment(Connection conn, String schema, String tableName) {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(String.format(DtClassConsistent.HadoopConfConsistent.DESCRIBE_EXTENDED
                    , transferSchemaAndTableName(schema, tableName)));
            while (resultSet.next()) {
                String columnType = resultSet.getString(2);
                if (StringUtils.isNotBlank(columnType) && columnType.contains("comment")) {
                    return StringUtils.isBlank(resultSet.getString(3)) ? "" : resultSet.getString(3).trim();
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    transferSchemaAndTableName(schema, tableName)), e);
        } finally {
            DBUtil.closeDBResources(resultSet, statement, null);
        }
        return "";
    }

    private static ColumnMetaDTO dealResult(ResultSet resultSet, Object part) throws SQLException {
        ColumnMetaDTO metaDTO = new ColumnMetaDTO();
        metaDTO.setKey(resultSet.getString(DtClassConsistent.PublicConsistent.NAME).trim());
        metaDTO.setType(resultSet.getString(DtClassConsistent.PublicConsistent.TYPE).trim());
        metaDTO.setComment(resultSet.getString(DtClassConsistent.PublicConsistent.COMMENT));
        metaDTO.setPart(Boolean.TRUE.equals(part));
        return metaDTO;
    }

    @Override
    public List<String> getAllDatabases(ISourceDTO source, SqlQueryDTO queryDTO) {
        List<String> databases = super.getAllDatabases(source, queryDTO);
        databases.remove(0);
        return databases;
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        List<ColumnMetaDTO> columnMetaDTOS = getColumnMetaData(source,queryDTO);
        List<ColumnMetaDTO> partitionColumnMeta = new ArrayList<>();
        columnMetaDTOS.forEach(columnMetaDTO -> {
            if(columnMetaDTO.getPart()){
                partitionColumnMeta.add(columnMetaDTO);
            }
        });
        return partitionColumnMeta;
    }

    @Override
    public Table getTable(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO);
        ImpalaSourceDTO impalaSourceDTO = (ImpalaSourceDTO) sourceDTO;

        Table tableInfo = new Table();
        // schema 先从queryDTO中获取
        String schema = StringUtils.isBlank(queryDTO.getSchema()) ? impalaSourceDTO.getSchema() : queryDTO.getSchema();
        try {
            tableInfo.setName(queryDTO.getTableName());
            // 获取表注释
            tableInfo.setComment(getTableMetaComment(connection, schema, queryDTO.getTableName()));
            // 先获取全部字段，再过滤
            List<ColumnMetaDTO> columnMetaDTOS = getColumnMetaData(connection, schema, queryDTO.getTableName(), false);
            // 分区字段不为空表示是分区表
            if (ReflectUtil.fieldExists(Table.class, "isPartitionTable")) {
                tableInfo.setIsPartitionTable(CollectionUtils.isNotEmpty(TableUtil.getPartitionColumns(columnMetaDTOS)));
            }
            tableInfo.setColumns(TableUtil.filterPartitionColumns(columnMetaDTOS, queryDTO.getFilterPartitionColumns()));
            // 获取表结构信息
            getTable(tableInfo, impalaSourceDTO, schema, queryDTO.getTableName());
        } catch (Exception e) {
            throw new SourceException(String.format("SQL executed exception, %s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
        return tableInfo;

    }

    private void getTable(Table tableInfo, ImpalaSourceDTO impalaSourceDTO, String schema, String tableName) {
        List<Map<String, Object>> result = executeQuery(impalaSourceDTO, SqlQueryDTO.builder().sql("DESCRIBE formatted " + transferSchemaAndTableName(schema, tableName)).build());
        boolean isTableInfo = false;
        for (Map<String, Object> row : result) {
            String colName = MapUtils.getString(row, "name", "");
            String dataType = MapUtils.getString(row, "type", "");
            if (StringUtils.isBlank(colName) || StringUtils.isBlank(dataType)) {
                if (StringUtils.isNotBlank(colName) && colName.contains("# Detailed Table Information")) {
                    isTableInfo = true;
                }
            }
            // 去空格处理
            dataType = dataType.trim();
            if (!isTableInfo) {
                continue;
            }

            if (colName.contains("Location:")) {
                tableInfo.setPath(dataType);
                continue;
            }

            if (colName.contains("Table Type:")) {
                tableInfo.setExternalOrManaged(dataType);
                continue;
            }

            if (dataType.contains("field.delim")) {
                tableInfo.setDelim(MapUtils.getString(row, "comment", "").trim());
                continue;
            }

            if (colName.contains("Owner")) {
                tableInfo.setOwner(dataType);
                continue;
            }

            if (colName.contains("CreateTime")) {
                tableInfo.setCreatedTime(dataType);
                continue;
            }

            if (colName.contains("LastAccessTime")) {
                tableInfo.setLastAccess(dataType);
                continue;
            }

            if (colName.contains("Database")) {
                tableInfo.setDb(dataType);
                continue;
            }

            if (tableInfo.getStoreType() == null && colName.contains("InputFormat")) {
                for (StoredType hiveStoredType : StoredType.values()) {
                    if (StringUtils.containsIgnoreCase(dataType, hiveStoredType.getInputFormatClass())) {
                        tableInfo.setStoreType(hiveStoredType.getValue());
                        break;
                    }
                }
            }
        }
        // text 未获取到分隔符情况下添加默认值
        if (StringUtils.equalsIgnoreCase(StoredType.TEXTFILE.getValue(), tableInfo.getStoreType()) && Objects.isNull(tableInfo.getDelim())) {
            tableInfo.setDelim(DtClassConsistent.HiveConsistent.DEFAULT_FIELD_DELIMIT);
        }

        if (StringUtils.containsIgnoreCase(tableInfo.getExternalOrManaged(), "VIEW")) {
            tableInfo.setIsView(true);
        }
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    /**
     * 处理 schema和tableName
     *
     * @param schema
     * @param tableName
     * @return
     */
    @Override
    protected String transferSchemaAndTableName(String schema, String tableName) {
        if (StringUtils.isBlank(schema)) {
            return tableName;
        }
        //如果tableName带 . 如 database.table 直接返回
        if(StringUtils.isNotEmpty(tableName) && tableName.contains(".")){
            return tableName;
        }
        return String.format("%s.%s", schema, tableName);
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, SqlQueryDTO queryDTO) throws Exception {
        ImpalaDownload impalaDownload = new ImpalaDownload(getCon(source), queryDTO.getSql());
        impalaDownload.configure();
        return impalaDownload;
    }

    @Override
    protected String getFuzzySign() {
        return "*";
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
