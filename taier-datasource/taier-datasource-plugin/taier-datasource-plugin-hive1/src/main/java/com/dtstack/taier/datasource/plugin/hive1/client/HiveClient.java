/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.plugin.hive1.client;

import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.enums.StoredType;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.EnvUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.common.utils.TableUtil;
import com.dtstack.taier.datasource.plugin.hive1.downloader.HiveJdbcDownload;
import com.dtstack.taier.datasource.plugin.hive1.downloader.HiveORCDownload;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.hive1.HiveConnFactory;
import com.dtstack.taier.datasource.plugin.hive1.downloader.HiveParquetDownload;
import com.dtstack.taier.datasource.plugin.hive1.downloader.HiveTextDownload;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.client.ITable;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.Hive1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:06 2020/1/7
 * @Description：Hive 连接
 */
@Slf4j
public class HiveClient extends AbsRdbmsClient {

    // 获取正在使用数据库
    private static final String CURRENT_DB = "select current_database()";

    // 创建库指定注释
    private static final String CREATE_DB_WITH_COMMENT = "create database %s comment '%s'";

    // 创建库
    private static final String CREATE_DB = "create database %s";

    // 模糊查询查询指定schema下的表
    private static final String TABLE_BY_SCHEMA_LIKE = "show tables in %s like '%s'";

    // 模糊查询database
    private static final String SHOW_DB_LIKE = "show databases like '%s'";

    // null 名称的字段名
    private static final String NULL_COLUMN = "null";

    // hive table client
    private static final ITable TABLE_CLIENT = new Hive1TableClient();

    // show tables
    private static final String SHOW_TABLE_SQL = "show tables";

    // show tables like 'xxx'
    private static final String SHOW_TABLE_LIKE_SQL = "show tables like '%s'";

    // desc db info
    private static final String DESC_DB_INFO = "desc database %s";

    @Override
    protected ConnFactory getConnFactory() {
        return new HiveConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.HIVE1X;
    }

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        // 获取表信息需要通过show tables 语句
        String sql;
        if (Objects.nonNull(queryDTO) && StringUtils.isNotEmpty(queryDTO.getTableNamePattern())) {
            // 模糊查询
            sql = String.format(SHOW_TABLE_LIKE_SQL, addFuzzySign(queryDTO));
        } else {
            sql = SHOW_TABLE_SQL;
        }
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
            throw new SourceException(String.format("get table exception,%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return SearchUtil.handleSearchAndLimit(tableList, queryDTO);
    }

    @Override
    public List<String> getTableListBySchema(ISourceDTO source, SqlQueryDTO queryDTO) {
        Hive1SourceDTO hive1SourceDTO = (Hive1SourceDTO) source;
        if (Objects.nonNull(queryDTO) && StringUtils.isNotBlank(queryDTO.getSchema())) {
            hive1SourceDTO.setSchema(queryDTO.getSchema());
        }
        return getTableList(hive1SourceDTO, queryDTO);
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaData(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        try {
            return getColumnMetaData(connection, queryDTO.getTableName(), queryDTO.getFilterPartitionColumns());
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    private List<ColumnMetaDTO> getColumnMetaData(Connection conn, String tableName, Boolean filterPartitionColumns) {
        List<ColumnMetaDTO> columnMetaDTOS = new ArrayList<>();
        Statement stmt = null;
        ResultSet resultSet = null;

        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery("desc extended " + tableName);
            while (resultSet.next()) {
                String dataType = resultSet.getString(DtClassConsistent.PublicConsistent.DATA_TYPE);
                String colName = resultSet.getString(DtClassConsistent.PublicConsistent.COL_NAME);
                if (StringUtils.isEmpty(dataType) || StringUtils.isBlank(colName)) {
                    break;
                }

                colName = colName.trim();
                ColumnMetaDTO metaDTO = new ColumnMetaDTO();
                metaDTO.setType(dataType.trim());
                metaDTO.setKey(colName);
                metaDTO.setComment(resultSet.getString(DtClassConsistent.PublicConsistent.COMMENT));

                if (colName.startsWith("#") || "Detailed Table Information".equals(colName)) {
                    break;
                }
                columnMetaDTOS.add(metaDTO);
            }

            DBUtil.closeDBResources(resultSet, null, null);
            resultSet = stmt.executeQuery("desc extended " + tableName);
            boolean partBegin = false;
            while (resultSet.next()) {
                String colName = resultSet.getString(DtClassConsistent.PublicConsistent.COL_NAME).trim();

                if (colName.contains("# Partition Information")) {
                    partBegin = true;
                }

                if (colName.startsWith("#")) {
                    continue;
                }

                if ("Detailed Table Information".equals(colName)) {
                    break;
                }

                // 处理分区标志
                if (partBegin && !colName.contains("Partition Type")) {
                    Optional<ColumnMetaDTO> metaDTO =
                            columnMetaDTOS.stream().filter(meta -> colName.trim().equals(meta.getKey())).findFirst();
                    if (metaDTO.isPresent()) {
                        metaDTO.get().setPart(true);
                    }
                } else if (colName.contains("Partition Type")) {
                    //分区字段结束
                    partBegin = false;
                }
            }

            return columnMetaDTOS.stream().filter(column -> !filterPartitionColumns || !column.getPart()).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new SourceException(String.format("Failed to get meta information for the fields of table :%s. Please contact the DBA to check the database table information.",
                    tableName), e);
        } finally {
            DBUtil.closeDBResources(resultSet, stmt, null);
        }
    }

    @Override
    public String getTableMetaComment(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        try {
            return getTableMetaComment(connection, queryDTO.getTableName());
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
    }

    private String getTableMetaComment(Connection conn, String tableName) {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            resultSet = statement.executeQuery(String.format(DtClassConsistent.HadoopConfConsistent.DESCRIBE_EXTENDED
                    , tableName));
            while (resultSet.next()) {
                String columnName = resultSet.getString(1);
                if (StringUtils.isNotEmpty(columnName) && columnName.toLowerCase().contains(DtClassConsistent.HadoopConfConsistent.TABLE_INFORMATION)) {
                    String string = resultSet.getString(2);
                    if (StringUtils.isNotEmpty(string) && string.contains(DtClassConsistent.HadoopConfConsistent.HIVE_COMMENT)) {
                        String[] split = string.split(DtClassConsistent.HadoopConfConsistent.HIVE_COMMENT);
                        if (split.length > 1) {
                            return split[1].split(",|}|\n")[0].trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("get table: %s's information error. Please contact the DBA to check the database、table information.",
                    tableName), e);
        } finally {
            DBUtil.closeDBResources(resultSet, statement, null);
        }
        return "";
    }

    @Override
    public List<ColumnMetaDTO> getColumnMetaDataWithSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        List<ColumnMetaDTO> columns = super.getColumnMetaDataWithSql(sourceDTO, queryDTO);
        columns.forEach(column -> {
            String key = column.getKey();
            if (StringUtils.isNotBlank(key) && key.split("\\.", -1).length ==2) {
                column.setKey(key.split("\\.", -1)[1]);
            }
        });
        return columns;
    }

    @Override
    public Boolean testCon(ISourceDTO sourceDTO) {
        // 先校验数据源连接性
        Boolean testCon = super.testCon(sourceDTO);
        if (!testCon) {
            return Boolean.FALSE;
        }

        Hive1SourceDTO hive1SourceDTO = (Hive1SourceDTO) sourceDTO;
        if (StringUtils.isBlank(hive1SourceDTO.getDefaultFS())) {
            return Boolean.TRUE;
        }

        return HdfsOperator.checkConnection(hive1SourceDTO.getDefaultFS(), hive1SourceDTO.getConfig(), hive1SourceDTO.getKerberosConfig());
    }

    @Override
    public IDownloader getDownloader(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Hive1SourceDTO hive1SourceDTO = (Hive1SourceDTO) sourceDTO;
        Table table;
        // 普通字段集合
        ArrayList<ColumnMetaDTO> commonColumn = new ArrayList<>();
        // 分区字段集合
        ArrayList<String> partitionColumns = new ArrayList<>();
        // 分区表所有分区 如果为 null 标识不是分区表，如果为空标识分区表无分区
        List<String> partitions = null;
        try {
            // 获取表详情信息
            table = getTable(hive1SourceDTO, queryDTO);
            //当属于未知存储类型时（eg:hyperbase,es），通过jdbc读取数据并拼接sql语句
            if (table.getStoreType() == null && StringUtils.isBlank(queryDTO.getSql())) {
                queryDTO.setSql(String.format("select * from %s", queryDTO.getTableName()));
            }
            for (ColumnMetaDTO columnMetaDatum : table.getColumns()) {
                // 非分区字段
                if (columnMetaDatum.getPart()) {
                    partitionColumns.add(columnMetaDatum.getKey());
                    continue;
                }
                commonColumn.add(columnMetaDatum);
            }
            // 分区表
            if (CollectionUtils.isNotEmpty(partitionColumns)) {
                partitions = TABLE_CLIENT.showPartitions(hive1SourceDTO, queryDTO.getTableName());
                if (CollectionUtils.isNotEmpty(partitions)) {
                    // 转化成小写，因为分区字段即使是大写在 hdfs 上仍是小写存在
                    partitions = partitions.stream()
                            .filter(StringUtils::isNotEmpty)
                            .map(String::toLowerCase)
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            throw new SourceException(String.format("failed to get table detail: %s", e.getMessage()), e);
        }
        // 查询的字段列表，支持按字段获取数据
        List<String> columns = queryDTO.getColumns();
        // 需要的字段索引（包括分区字段索引）
        List<Integer> needIndex = Lists.newArrayList();
        // columns字段不为空且不包含*时获取指定字段的数据
        if (CollectionUtils.isNotEmpty(columns) && !columns.contains("*")) {
            // 保证查询字段的顺序!
            for (String column : columns) {
                if (NULL_COLUMN.equalsIgnoreCase(column)) {
                    needIndex.add(Integer.MAX_VALUE);
                    continue;
                }
                // 判断查询字段是否存在
                boolean check = false;
                for (int j = 0; j < table.getColumns().size(); j++) {
                    if (column.equalsIgnoreCase(table.getColumns().get(j).getKey())) {
                        needIndex.add(j);
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    throw new SourceException("The query field does not exist! Field name：" + column);
                }
            }
        }

        // 校验高可用配置
        if (StringUtils.isBlank(hive1SourceDTO.getDefaultFS())) {
            throw new SourceException("defaultFS incorrect format");
        }
        transformDelim(table);
        List<String> finalPartitions = partitions;
        return KerberosLoginUtil.loginWithUGI(hive1SourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<IDownloader>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil.getHdfsConf(hive1SourceDTO.getDefaultFS(), hive1SourceDTO.getConfig(), hive1SourceDTO.getKerberosConfig());
                        return createDownloader(table.getStoreType(), conf, table.getPath(), commonColumn, table.getDelim(), partitionColumns, needIndex, queryDTO.getPartitionColumns(), finalPartitions, hive1SourceDTO, queryDTO.getSql());
                    } catch (Exception e) {
                        throw new SourceException(String.format("create downloader exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    /**
     * 获取hdfs 里真正的切分符
     *
     * @param table
     */
    private void transformDelim(Table table) {
        if (StringUtils.isEmpty(table.getDelim())) {
            return;
        }
        Boolean isLazySimpleSerDe = ReflectUtil.fieldExists(Table.class, "isLazySimpleSerDe") ? table.getIsLazySimpleSerDe() : true;
        String fieldDelimiter = table.getDelim();
        String finalFieldDelimiter = isLazySimpleSerDe ? (fieldDelimiter.charAt(0) == '\\' ? fieldDelimiter.substring(0, 2) : fieldDelimiter.substring(0, 1)) : fieldDelimiter;
        table.setDelim(finalFieldDelimiter);
    }

    /**
     * 根据存储格式创建对应的hiveDownloader
     *
     * @param storageMode      存储格式
     * @param conf             配置
     * @param tableLocation    表hdfs路径
     * @param columns          字段集合
     * @param fieldDelimiter   textFile 表列分隔符
     * @param partitionColumns 分区字段集合
     * @param needIndex        需要查询的字段索引位置
     * @param filterPartitions 需要查询的分区
     * @param partitions       全部分区
     * @param hive1SourceDTO   sourceDTO
     * @param sql              当获取jdbc downloader时需要
     * @return downloader
     * @throws Exception 异常信息
     */
    private @NotNull IDownloader createDownloader(String storageMode, Configuration conf, String tableLocation,
                                                  List<ColumnMetaDTO> columns, String fieldDelimiter,
                                                  ArrayList<String> partitionColumns, List<Integer> needIndex,
                                                  Map<String, String> filterPartitions, List<String> partitions,
                                                  Hive1SourceDTO hive1SourceDTO, String sql) throws Exception {
        // 根据存储格式创建对应的hiveDownloader
        List<String> columnNames = columns.stream().map(ColumnMetaDTO::getKey).collect(Collectors.toList());
        if (StringUtils.containsIgnoreCase(storageMode, "text")) {
            HiveTextDownload hiveTextDownload = new HiveTextDownload(conf, tableLocation, columnNames,
                    fieldDelimiter, partitionColumns, filterPartitions, needIndex, partitions, hive1SourceDTO.getKerberosConfig());
            hiveTextDownload.configure();
            return hiveTextDownload;
        }

        if (StringUtils.containsIgnoreCase(storageMode, "orc")) {
            HiveORCDownload hiveORCDownload = new HiveORCDownload(conf, tableLocation, columnNames,
                    partitionColumns, needIndex, partitions, hive1SourceDTO.getKerberosConfig());
            hiveORCDownload.configure();
            return hiveORCDownload;
        }

        if (StringUtils.containsIgnoreCase(storageMode, "parquet")) {
            HiveParquetDownload hiveParquetDownload = new HiveParquetDownload(conf, tableLocation, columns,
                    partitionColumns, needIndex, filterPartitions, partitions, hive1SourceDTO.getKerberosConfig());
            hiveParquetDownload.configure();
            return hiveParquetDownload;
        }

        //es & hbase等作为外表时storageMode为null，走jdbc的downloader方式
        if (StringUtils.isBlank(storageMode)) {
            HiveJdbcDownload jdbcDownload = new HiveJdbcDownload(getCon(hive1SourceDTO), sql);
            jdbcDownload.configure();
            return jdbcDownload;
        }

        throw new SourceException("Hive table reads for this storage type are not supported");
    }

    @Override
    protected String dealSql(ISourceDTO iSourceDTO, SqlQueryDTO sqlQueryDTO) {
        Map<String, String> partitions = sqlQueryDTO.getPartitionColumns();
        StringBuilder partSql = new StringBuilder();
        //拼接分区信息
        if (MapUtils.isNotEmpty(partitions)) {
            boolean check = true;
            partSql.append(" where ");
            Set<String> set = partitions.keySet();
            for (String column : set) {
                if (check) {
                    partSql.append(column + "=").append(partitions.get(column));
                    check = false;
                } else {
                    partSql.append(" and ").append(column + "=").append(partitions.get(column));
                }
            }
        }
        return "select * from " + sqlQueryDTO.getTableName() + partSql.toString() + limitSql(sqlQueryDTO.getPreviewNum());
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO source, SqlQueryDTO queryDTO) {
        List<ColumnMetaDTO> columnMetaDTOS = getColumnMetaData(source, queryDTO);
        List<ColumnMetaDTO> partitionColumnMeta = new ArrayList<>();
        columnMetaDTOS.forEach(columnMetaDTO -> {
            if (columnMetaDTO.getPart()) {
                partitionColumnMeta.add(columnMetaDTO);
            }
        });
        return partitionColumnMeta;
    }

    @Override
    public Table getTable(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        Hive1SourceDTO hive1SourceDTO = (Hive1SourceDTO) sourceDTO;

        Table tableInfo = new Table();
        try {
            tableInfo.setName(queryDTO.getTableName());
            // 获取表注释
            tableInfo.setComment(getTableMetaComment(connection, queryDTO.getTableName()));
            // 先获取全部字段，再过滤
            List<ColumnMetaDTO> columnMetaDTOS = getColumnMetaData(connection, queryDTO.getTableName(), false);
            // 分区字段不为空表示是分区表
            if (ReflectUtil.fieldExists(Table.class, "isPartitionTable")) {
                tableInfo.setIsPartitionTable(CollectionUtils.isNotEmpty(TableUtil.getPartitionColumns(columnMetaDTOS)));
            }
            tableInfo.setColumns(TableUtil.filterPartitionColumns(columnMetaDTOS, queryDTO.getFilterPartitionColumns()));
            // 获取表结构信息
            getTable(tableInfo, hive1SourceDTO, queryDTO.getTableName());
        } catch (Exception e) {
            throw new SourceException(String.format("SQL executed exception, %s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
        return tableInfo;
    }

    private void getTable(Table tableInfo, Hive1SourceDTO hive1SourceDTO, String tableName) {
        List<Map<String, Object>> result = executeQuery(hive1SourceDTO, SqlQueryDTO.builder().sql("desc formatted " + tableName).build());
        boolean isTableInfo = false;
        for (Map<String, Object> row : result) {
            String colName = MapUtils.getString(row, "col_name", "");
            String comment = MapUtils.getString(row, "comment", "");
            String dataTypeOrigin = MapUtils.getString(row, "data_type", "");
            if (StringUtils.isBlank(colName) || StringUtils.isEmpty(dataTypeOrigin)) {
                if (StringUtils.isNotBlank(colName) && colName.contains("# Detailed Table Information")) {
                    isTableInfo = true;
                }
            }
            // 去空格处理
            String dataType = dataTypeOrigin.trim();
            if (!isTableInfo) {
                continue;
            }

            if (colName.contains("Location")) {
                tableInfo.setPath(dataType);
                continue;
            }

            if (colName.contains("Table Type")) {
                if (ReflectUtil.fieldExists(Table.class, "isView")) {
                    tableInfo.setIsView(StringUtils.containsIgnoreCase(dataType, "VIEW"));
                }
                tableInfo.setExternalOrManaged(dataType);
                continue;
            }

            // 兼容一下返回值 Type 的情况
            if (("Type".equals(colName.trim()) || "Type:".equals(colName.trim())) && StringUtils.isEmpty(tableInfo.getExternalOrManaged())) {
                if (ReflectUtil.fieldExists(Table.class, "isView")) {
                    tableInfo.setIsView(StringUtils.containsIgnoreCase(dataType, "VIEW"));
                }
                tableInfo.setExternalOrManaged(dataType);
                continue;
            }

            if (colName.contains("field.delim")) {
                tableInfo.setDelim(dataTypeOrigin);
                continue;
            }

            if (dataType.contains("field.delim")) {
                String delimit = MapUtils.getString(row, "comment", "");
                tableInfo.setDelim(delimit);
                continue;
            }

            if (colName.contains("Owner")) {
                tableInfo.setOwner(dataType);
                continue;
            }

            if (colName.contains("CreateTime") || colName.contains("CreatedTime")) {
                tableInfo.setCreatedTime(dataType);
                continue;
            }

            if (colName.contains("LastAccess")) {
                tableInfo.setLastAccess(dataType);
                continue;
            }

            if (colName.contains("CreatedBy")) {
                tableInfo.setCreatedBy(dataType);
                continue;
            }

            if (colName.contains("Database")) {
                tableInfo.setDb(dataType);
                continue;
            }

            if (StringUtils.containsIgnoreCase(dataType, "transactional")) {
                if (ReflectUtil.fieldExists(Table.class, "isTransTable") && StringUtils.containsIgnoreCase(comment, "true")) {
                    tableInfo.setIsTransTable(true);
                }
                continue;
            }

            if (tableInfo.getStoreType() == null && colName.contains("InputFormat")) {
                for (StoredType hiveStoredType : StoredType.values()) {
                    if (dataType.contains(hiveStoredType.getInputFormatClass())) {
                        tableInfo.setStoreType(hiveStoredType.getValue());
                        break;
                    }
                }
            }

            //单字符作为分隔符 org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe
            //多字符作为分隔符  org.apache.hadoop.hive.contrib.serde2.MultiDelimitSerDe,org.apache.hadoop.hive.contrib.serde2.RegexSerDe
            if (colName.contains("SerDe Library")) {
                if (ReflectUtil.fieldExists(Table.class, "isLazySimpleSerDe")) {
                    if (StringUtils.containsIgnoreCase(dataType, "LazySimpleSerDe")) {
                        tableInfo.setIsTransTable(true);
                    } else {
                        tableInfo.setIsTransTable(false);
                    }
                }
            }
        }
        // text 未获取到分隔符情况下添加默认值
        if (StringUtils.equalsIgnoreCase(StoredType.TEXTFILE.getValue(), tableInfo.getStoreType()) && Objects.isNull(tableInfo.getDelim())) {
            tableInfo.setDelim(DtClassConsistent.HiveConsistent.DEFAULT_FIELD_DELIMIT);
        }
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }

    @Override
    protected String getCreateDatabaseSql(String dbName, String comment) {
        return StringUtils.isBlank(comment) ? String.format(CREATE_DB, dbName) : String.format(CREATE_DB_WITH_COMMENT, dbName, comment);
    }

    @Override
    public Boolean isDatabaseExists(ISourceDTO source, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name cannot be empty!");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(SHOW_DB_LIKE, dbName)).build()));
    }

    @Override
    public Boolean isTableExistsInDatabase(ISourceDTO source, String tableName, String dbName) {
        if (StringUtils.isBlank(dbName)) {
            throw new SourceException("database name cannot be empty!");
        }
        return CollectionUtils.isNotEmpty(executeQuery(source, SqlQueryDTO.builder().sql(String.format(TABLE_BY_SCHEMA_LIKE, dbName, tableName)).build()));
    }

    @Override
    public String getDescDbSql(String dbName) {
        return String.format(DESC_DB_INFO, dbName);
    }

    @Override
    public String getCreateTableSql(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
        String sql = queryDTO.getSql() == null ? "show create table " + queryDTO.getTableName() : queryDTO.getSql();
        Statement statement = null;
        ResultSet rs = null;
        StringBuilder createTableSql = new StringBuilder();
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(sql);
            int columnSize = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                createTableSql.append(rs.getString(columnSize == 1 ? 1 : 2));
            }
        } catch (Exception e) {
            throw new SourceException(String.format("failed to get the create table sql：%s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(rs, statement, connection);
        }
        return createTableSql.toString();
    }

    @Override
    protected String getFuzzySign() {
        return "*";
    }

    @Override
    protected Pair<Character, Character> getSpecialSign() {
        return Pair.of('`', '`');
    }

    @Override
    protected void writeSchemaFile(ISourceDTO source, String schema, String outputDir) {
        Hive1SourceDTO hive1SourceDTO = (Hive1SourceDTO) source;
        try (FileSystem fs = HdfsOperator.getFileSystem(hive1SourceDTO.getKerberosConfig(), hive1SourceDTO.getConfig(), hive1SourceDTO.getDefaultFS());
             FSDataOutputStream out = fs.create(new Path(outputDir));) {
            out.write(schema.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
