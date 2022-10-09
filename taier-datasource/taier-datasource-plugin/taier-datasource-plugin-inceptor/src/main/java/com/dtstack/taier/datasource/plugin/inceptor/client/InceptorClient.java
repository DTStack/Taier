package com.dtstack.taier.datasource.plugin.inceptor.client;

import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.enums.StoredType;
import com.dtstack.taier.datasource.plugin.common.utils.DBUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ReflectUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SearchUtil;
import com.dtstack.taier.datasource.plugin.common.utils.TableUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HadoopConfUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.hdfs.HdfsOperator;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosConfigUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.inceptor.InceptorConnFactory;
import com.dtstack.taier.datasource.plugin.inceptor.downloader.InceptorDownload;
import com.dtstack.taier.datasource.plugin.inceptor.downloader.InceptorORCDownload;
import com.dtstack.taier.datasource.plugin.inceptor.downloader.InceptorParquetDownload;
import com.dtstack.taier.datasource.plugin.inceptor.downloader.InceptorTextDownload;
import com.dtstack.taier.datasource.plugin.rdbms.AbsRdbmsClient;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.client.ITable;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InceptorSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hive.common.type.HiveDate;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;

import org.apache.hadoop.conf.Configuration;
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
import java.util.stream.Collectors;

/**
 * inceptor client
 *
 * @author ：wangchuan
 * date：Created in 下午2:19 2021/5/6
 * company: www.dtstack.com
 */
@Slf4j
public class InceptorClient extends AbsRdbmsClient {

    // 创建库指定注释
    private static final String CREATE_DB_WITH_COMMENT = "create database if not exists %s comment '%s'";

    // 创建库
    private static final String CREATE_DB = "create database %s";

    // 模糊查询查询指定schema下的表
    private static final String TABLE_BY_SCHEMA_LIKE = "show tables in %s like '%s'";

    // 模糊查询database
    private static final String SHOW_DB_LIKE = "show databases like '%s'";

    private static final String SHOW_TABLE_SQL = "show tables %s";

    // 根据schema选表表名模糊查询
    private static final String SEARCH_SQL = " like '%s' ";

    // null 名称的字段名
    private static final String NULL_COLUMN = "null";

    private static final ITable TABLE_CLIENT = new InceptorTableClient();

    // desc db info
    private static final String DESC_DB_INFO = "desc database %s";

    @Override
    protected ConnFactory getConnFactory() {
        return new InceptorConnFactory();
    }

    @Override
    protected DataSourceType getSourceType() {
        return DataSourceType.INCEPTOR;
    }

    // metaStore 地址 key
    private final static String META_STORE_URIS_KEY = "hive.metastore.uris";

    // 是否启用 kerberos 认证
    private final static String META_STORE_SASL_ENABLED = "hive.metastore.sasl.enabled";

    // metaStore 地址 principal 地址
    private final static String META_STORE_KERBEROS_PRINCIPAL = "hive.metastore.kerberos.principal";

    // 获取正在使用数据库
    private static final String CURRENT_DB = "select current_database()";

    @Override
    public List<String> getTableList(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        Connection connection = getCon(sourceDTO, queryDTO);
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
            int maxLimit = 0;
            if (Objects.nonNull(queryDTO) && Objects.nonNull(queryDTO.getLimit())) {
                // 设置最大条数
                maxLimit = queryDTO.getLimit();
            }
            DBUtil.setFetchSize(statement, queryDTO);
            rs = statement.executeQuery(sql);
            int columnSize = rs.getMetaData().getColumnCount();
            int cnt = 0;
            while (rs.next()) {
                if(maxLimit > 0 && cnt >= maxLimit) {
                    break;
                }
                ++cnt;
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
    public List<String> getTableListBySchema(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        InceptorSourceDTO inceptorSourceDTO = (InceptorSourceDTO) sourceDTO;
        if (Objects.nonNull(queryDTO) && StringUtils.isNotBlank(queryDTO.getSchema())) {
            inceptorSourceDTO.setSchema(queryDTO.getSchema());
        }
        return getTableList(inceptorSourceDTO, queryDTO);
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

    /**
     * 获取表注释信息
     *
     * @param conn      数据源连接
     * @param tableName 表名
     * @return 表注释
     */
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
                            return split[1].split("[,}\n]")[0].trim();
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

    private List<ColumnMetaDTO> getColumnMetaData(Connection conn, String tableName, Boolean filterPartitionColumns) {
        List<ColumnMetaDTO> columnMetaDTOS = new ArrayList<>();
        Statement stmt = null;
        ResultSet resultSet = null;

        try {
            stmt = conn.createStatement();
            resultSet = stmt.executeQuery(String.format(DtClassConsistent.HadoopConfConsistent.DESCRIBE_EXTENDED, tableName));
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
            resultSet = stmt.executeQuery(String.format(DtClassConsistent.HadoopConfConsistent.DESCRIBE_EXTENDED, tableName));
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
                    metaDTO.ifPresent(columnMetaDTO -> columnMetaDTO.setPart(true));
                } else if (colName.contains("Partition Type")) {
                    //分区字段结束
                    partBegin = false;
                }
            }

            return columnMetaDTOS.stream().filter(column -> !filterPartitionColumns || !column.getPart()).collect(Collectors.toList());
        } catch (SQLException e) {
            throw new SourceException(String.format("Failed to get meta information for the fields of table :%s. Please contact the DBA to check the database table information,%s", tableName, e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(resultSet, stmt, null);
        }
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
        InceptorSourceDTO inceptorSourceDTO = (InceptorSourceDTO) sourceDTO;
        if (StringUtils.isBlank(inceptorSourceDTO.getDefaultFS())) {
            return Boolean.TRUE;
        }
        if (StringUtils.isNotBlank(inceptorSourceDTO.getMetaStoreUris())) {
            // 检查 metaStore 连通性
            checkMetaStoreConnect(inceptorSourceDTO.getMetaStoreUris(), inceptorSourceDTO.getKerberosConfig());
        }
        return HdfsOperator.checkConnection(inceptorSourceDTO.getDefaultFS(), inceptorSourceDTO.getConfig(), inceptorSourceDTO.getKerberosConfig());
    }

    /**
     * 检查 metaStore 连通性
     *
     * @param metaStoreUris  metaStore 地址
     * @param kerberosConfig kerberos 配置
     */
    private synchronized void checkMetaStoreConnect(String metaStoreUris, Map<String, Object> kerberosConfig) {
        HiveConf hiveConf = new HiveConf();
        hiveConf.set(META_STORE_URIS_KEY, metaStoreUris);
        // 重新设置 metaStore 地址
        ReflectUtil.setField(HiveMetaStoreClient.class, "metastoreUris", null, null);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            // metaStore kerberos 认证需要
            hiveConf.setBoolean(META_STORE_SASL_ENABLED, true);
            // 做两步兼容：先取 hive.metastore.kerberos.principal 的值，再取 principal，最后再取 keytab 中的第一个 principal
            String metaStorePrincipal = MapUtils.getString(kerberosConfig, META_STORE_KERBEROS_PRINCIPAL, MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL));
            if (StringUtils.isBlank(metaStorePrincipal)) {
                String keytabPath = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL_FILE);
                metaStorePrincipal = KerberosConfigUtil.getPrincipals(keytabPath).get(0);
                if (StringUtils.isBlank(metaStorePrincipal)) {
                    throw new SourceException("hive.metastore.kerberos.principal is not null...");
                }
            }
            log.info("hive.metastore.kerberos.principal:{}", metaStorePrincipal);
            hiveConf.set(META_STORE_KERBEROS_PRINCIPAL, metaStorePrincipal);
        }
        KerberosLoginUtil.loginWithUGI(kerberosConfig).doAs(
                (PrivilegedAction<Boolean>) () -> {
                    HiveMetaStoreClient client = null;
                    try {
                        client = new HiveMetaStoreClient(hiveConf);
                        client.getAllDatabases();
                    } catch (Exception e) {
                        throw new SourceException(String.format("metastore connection failed.:%s", e.getMessage()));
                    } finally {
                        if (Objects.nonNull(client)) {
                            client.close();
                        }
                    }
                    return true;
                }
        );
    }

    private IDownloader getDownloaderFromHdfs(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        InceptorSourceDTO inceptorSourceDTO = (InceptorSourceDTO) sourceDTO;
        Table table;
        // 普通字段集合
        ArrayList<ColumnMetaDTO> commonColumn = new ArrayList<>();
        // 分区字段集合
        ArrayList<String> partitionColumns = new ArrayList<>();
        // 分区表所有分区 如果为 null 标识不是分区表，如果为空标识分区表无分区
        List<String> partitions = null;
        try {
            // 获取表详情信息
            table = getTable(inceptorSourceDTO, queryDTO);
            //当属于未知存储类型时（eg:hyperbase,es），通过jdbc读取数据
            if (table.getStoreType() == null) {
                String sql = String.format("select * from %s", queryDTO.getTableName());
                return getDownloader(sourceDTO, sql, 100);
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
                partitions = TABLE_CLIENT.showPartitions(inceptorSourceDTO, queryDTO.getTableName());
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
        if (StringUtils.isBlank(inceptorSourceDTO.getDefaultFS())) {
            throw new SourceException("defaultFS incorrect format");
        }
        List<String> finalPartitions = partitions;
        return KerberosLoginUtil.loginWithUGI(inceptorSourceDTO.getKerberosConfig()).doAs(
                (PrivilegedAction<IDownloader>) () -> {
                    try {
                        Configuration conf = HadoopConfUtil.getHdfsConf(inceptorSourceDTO.getDefaultFS(), inceptorSourceDTO.getConfig(), inceptorSourceDTO.getKerberosConfig());
                        return createDownloader(table.getStoreType(), conf, table.getPath(), commonColumn, table.getDelim(), partitionColumns, needIndex, queryDTO.getPartitionColumns(), finalPartitions, inceptorSourceDTO.getKerberosConfig());
                    } catch (Exception e) {
                        throw new SourceException(String.format("create downloader exception,%s", e.getMessage()), e);
                    }
                }
        );
    }

    /**
     * 根据存储格式创建对应的inceptorDownloader
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
     * @param kerberosConfig   kerberos 配置
     * @return downloader
     * @throws Exception 异常信息
     */
    private IDownloader createDownloader(String storageMode, Configuration conf, String tableLocation, List<ColumnMetaDTO> columns,
                                         String fieldDelimiter,
                                         ArrayList<String> partitionColumns, List<Integer> needIndex,
                                         Map<String, String> filterPartitions, List<String> partitions,
                                         Map<String, Object> kerberosConfig) throws Exception {
        // 根据存储格式创建对应的inceptorDownloader
        if (StringUtils.isBlank(storageMode)) {
            throw new SourceException("inceptor table reads for this storage type are not supported");
        }
        List<String> columnNames = columns.stream().map(ColumnMetaDTO::getKey).collect(Collectors.toList());
        if (StringUtils.containsIgnoreCase(storageMode, "text")) {
            InceptorTextDownload inceptorTextDownload = new InceptorTextDownload(conf, tableLocation, columnNames,
                    fieldDelimiter, partitionColumns, filterPartitions, needIndex, kerberosConfig);
            inceptorTextDownload.configure();
            return inceptorTextDownload;
        }

        if (StringUtils.containsIgnoreCase(storageMode, "orc")) {
            InceptorORCDownload inceptorORCDownload = new InceptorORCDownload(conf, tableLocation, columnNames,
                    partitionColumns, needIndex, partitions, kerberosConfig);
            inceptorORCDownload.configure();
            return inceptorORCDownload;
        }

        if (StringUtils.containsIgnoreCase(storageMode, "parquet")) {
            InceptorParquetDownload inceptorParquetDownload = new InceptorParquetDownload(conf, tableLocation, columns,
                    partitionColumns, needIndex, filterPartitions, partitions, kerberosConfig);
            inceptorParquetDownload.configure();
            return inceptorParquetDownload;
        }

        throw new SourceException("inceptor table reads for this storage type are not supported");
    }

    @Override
    public IDownloader getDownloader(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) throws Exception {
        if (StringUtils.isNotBlank(queryDTO.getSql())) {
            return getDownloader(sourceDTO, queryDTO.getSql(), 100);
        }
        return getDownloaderFromHdfs(sourceDTO, queryDTO);
    }

    @Override
    public IDownloader getDownloader(ISourceDTO source, String sql, Integer pageSize) throws Exception {
        InceptorDownload inceptorDownload = new InceptorDownload(getCon(source), sql, pageSize);
        inceptorDownload.configure();
        return inceptorDownload;
    }

    /**
     * 处理hive分区信息和sql语句
     *
     * @param sqlQueryDTO 查询条件
     * @return 处理后的数据预览sql
     */
    @Override
    protected String dealSql(ISourceDTO sourceDTO, SqlQueryDTO sqlQueryDTO) {
        Map<String, String> partitions = sqlQueryDTO.getPartitionColumns();
        StringBuilder partSql = new StringBuilder();
        //拼接分区信息
        if (MapUtils.isNotEmpty(partitions)) {
            boolean check = true;
            partSql.append(" where ");
            Set<String> set = partitions.keySet();
            for (String column : set) {
                if (check) {
                    partSql.append(column).append("=").append(partitions.get(column));
                    check = false;
                } else {
                    partSql.append(" and ").append(column).append("=").append(partitions.get(column));
                }
            }
        }
        return "select * from " + sqlQueryDTO.getTableName() + partSql.toString() + limitSql(sqlQueryDTO.getPreviewNum());
    }

    @Override
    public List<ColumnMetaDTO> getPartitionColumn(ISourceDTO sourceDTO, SqlQueryDTO queryDTO) {
        List<ColumnMetaDTO> columnMetaDTOS = getColumnMetaData(sourceDTO, queryDTO);
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
        InceptorSourceDTO inceptorSourceDTO = (InceptorSourceDTO) sourceDTO;

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
            getTable(tableInfo, inceptorSourceDTO, queryDTO.getTableName());
        } catch (Exception e) {
            throw new SourceException(String.format("SQL executed exception, %s", e.getMessage()), e);
        } finally {
            DBUtil.closeDBResources(null, null, connection);
        }
        return tableInfo;
    }

    /**
     * 获取表结构信息
     *
     * @param tableInfo 表信息
     * @param inceptorSourceDTO      连接信息
     * @param tableName 表名
     */
    private void getTable(Table tableInfo, InceptorSourceDTO inceptorSourceDTO, String tableName) {
        List<Map<String, Object>> result = executeQuery(inceptorSourceDTO, SqlQueryDTO.builder().sql("desc formatted " + tableName).build());
        for (Map<String, Object> row : result) {
            // category和attribute不可为空
            if (StringUtils.isBlank(MapUtils.getString(row, "category")) || StringUtils.isBlank(MapUtils.getString(row, "attribute"))) {
                continue;
            }
            // 去空格处理
            String category = MapUtils.getString(row, "category").trim();
            String attribute = MapUtils.getString(row, "attribute").trim();

            if (StringUtils.containsIgnoreCase(category, "Location")) {
                tableInfo.setPath(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "Type")) {
                tableInfo.setExternalOrManaged(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "field.delim")) {
                tableInfo.setDelim(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "Owner")) {
                tableInfo.setOwner(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "CreateTime")) {
                tableInfo.setCreatedTime(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "LastAccess")) {
                tableInfo.setLastAccess(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "CreatedBy")) {
                tableInfo.setCreatedBy(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "Database")) {
                tableInfo.setDb(attribute);
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "transactional")) {
                if (StringUtils.containsIgnoreCase(attribute, "true")) {
                    tableInfo.setIsTransTable(true);
                }
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "Type")) {
                tableInfo.setIsView(StringUtils.containsIgnoreCase(attribute, "VIEW"));
                continue;
            }

            if (StringUtils.containsIgnoreCase(category, "InputFormat")) {
                for (StoredType hiveStoredType : StoredType.values()) {
                    if (StringUtils.containsIgnoreCase(attribute, hiveStoredType.getInputFormatClass())) {
                        tableInfo.setStoreType(hiveStoredType.getValue());
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
    protected String getFuzzySign() {
        return "*";
    }

    @Override
    public String getDescDbSql(String dbName) {
        return String.format(DESC_DB_INFO, dbName);
    }

    @Override
    protected Object dealResult(Object result){
        result = super.dealResult(result);
        if(result instanceof HiveDate){
            try {
                HiveDate hiveresult = (HiveDate) result;
                return hiveresult.toString();
            } catch (SourceException e) {
                log.error("Hivedate format transform String exception",e);
            }
        }
        return result;
    }

    @Override
    protected String getCurrentDbSql() {
        return CURRENT_DB;
    }


    @Override
    protected Pair<Character, Character> getSpecialSign() {
        return Pair.of('`', '`');
    }
}
