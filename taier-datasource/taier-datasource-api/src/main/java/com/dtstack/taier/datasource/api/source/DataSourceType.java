package com.dtstack.taier.datasource.api.source;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author nanqi
 * company www.dtstack.com
 * date Created in 10:32 2020/7/27
 */
public enum DataSourceType {
    // RDBMS
    MySQL(1, 0, "MySQL", "MySQL", "mysql5"),
    MySQL8(1001, 1, "MySQL", "MySQL8", "mysql5"),
    MySQLPXC(98, 1, "MySQL PXC", "MySQL PXC", "mysql5"),
    MYSQL_SHARDING(86, 1, "MySQL SHARDING", "MySQL SHARDING", "mysql_sharding"),
    Polardb_For_MySQL(28, 2, "PolarDB for MySQL8", "PolarDB for MySQL8", "mysql5"),
    Oracle(2, 3, "Oracle", "Oracle", "oracle"),
    SQLServer(3, 4, "SQLServer", "SQLServer", "sqlServer"),
    SQLSERVER_2017_LATER(32, 5, "SQLServer JDBC", "SQLServer JDBC", "sqlServer"),
    PostgreSQL(4, 6, "PostgreSQL", "PostgreSQL", "postgresql"),
    DB2(19, 7, "DB2", "DB2", "db2"),
    DMDB(35, 8, "DMDB For MySQL", "DMDB For MySQL", "dmdb"),
    RDBMS(5, 9, "RDBMS", "RDBMS", "mysql"),
    KINGBASE8(40, 10, "KingbaseES8", "KingbaseES8", "kingbase8"),
    DMDB_For_Oracle(67, 8, "DMDB For Oracle", "DMDB For Orcale", "dmdb"),
    GREAT_DB(95, 11, "GreatDb", "GreatDb", "mysql5"),

    // Hadoop
    HIVE(7, 20, "Hive2.x", "Hive2.x", "hive"),
    HIVE1X(27, 21, "Hive1.x", "Hive1.x", "hive1"),
    HIVE3X(50, 22, "Hive3.x", "Hive3.x", "hive3"),
    MAXCOMPUTE(10, 23, "MaxCompute", "MaxCompute", "maxcompute"),

    // MPP
    GREENPLUM6(36, 40, "Greenplum", "Greenplum", "greenplum6"),
    LIBRA(21, 41, "GaussDB", "GaussDB", "libra"),
    GBase_8a(22, 42, "GBase_8a", "GBase_8a", "gbase"),
    DORIS(57, 43, "Doris0.14.x(jdbc)", "Doris0.14.x(jdbc)", "doris"),
    STAR_ROCKS(91, 44, "StarRocks", "StarRocks", "starrocks"),

    // FileSystem
    HDFS(6, 60, "HDFS", "HDFS", "hdfs"),
    HDFS3(63, 61, "HDFS3", "HDFS3", "hdfs3"),
    HDFS3_CDP(1003, 1003, "HDFS3_CDP", "HDFS3_CDP", "hdfs3_cdp"),
    FTP(9, 62, "FTP", "FTP", "ftp"),
    S3(41, 63, "S3", "S3", "s3"),
    AWS_S3(51, 64, "AWS S3", "AWS S3", "aws_s3"),

    // Analytic
    SparkThrift2_1(45, 80, "SparkThrift2.x", "SparkThrift2.x", "spark"),
    IMPALA(29, 81, "Impala", "Impala", "impala"),
    Clickhouse(25, 82, "ClickHouse", "ClickHouse", "clickhouse"),
    TiDB(31, 83, "TiDB", "TiDB", "tidb"),
    CarbonData(20, 84, "CarbonData", "CarbonData", "hive"),
    Kudu(24, 85, "Kudu", "Kudu", "kudu"),
    ADS(15, 86, "AnalyticDB", "AnalyticDB", "mysql5"),
    ADB_FOR_PG(54, 87, "AnalyticDB PostgreSQL", "AnalyticDB PostgreSQL", "postgresql"),
    Kylin(23, 88, "Kylin", "Kylin", "kylin"),
    Presto(48, 89, "Presto", "Presto", "presto"),
    OceanBase(49, 90, "OceanBase", "OceanBase", "oceanbase"),
    INCEPTOR(52, 91, "Inceptor", "Inceptor", "inceptor"),
    TRINO(59, 92, "Trino", "Trino", "trino"),
    ICEBERG(66, 93, "IceBerg", "IceBerg", "iceberg"),
    SAP_HANA1(76, 93, "SAP HANA 1.x", "SAP HANA 1.x", "sap_hana"),
    SAP_HANA2(77, 94, "SAP HANA 2.x", "SAP HANA 2.x", "sap_hana"),

    // NoSQL
    HBASE(8, 100, "HBase", "HBase", "hbase"),
    HBASE2(39, 101, "HBase", "HBase2", "hbase"),
    Phoenix(30, 102, "Phoenix4.x", "Phoenix4.x", "phoenix"),
    PHOENIX5(38, 103, "Phoenix5.x", "Phoenix5.x", "phoenix5"),
    ES(11, 104, "ElasticSearch5.x", "ElasticSearch5.x", "es5"),
    ES6(33, 105, "ElasticSearch6.x", "ElasticSearch6.x", "es"),
    ES7(46, 106, "ElasticSearch7.x", "ElasticSearch7.x", "es7"),
    MONGODB(13, 107, "MongoDB", "MongoDB", "mongo"),
    REDIS(12, 108, "Redis", "Redis", "redis"),
    SOLR(53, 109, "Solr", "Solr", "solr"),

    // others
    KAFKA_2X(37, 120, "Kafka2.x", "Kafka", "kafka"),
    KAFKA(26, 121, "Kafka", "Kafka", "kafka"),
    KAFKA_11(14, 122, "Kafka_0.11", "Kafka", "kafka"),
    KAFKA_10(17, 123, "Kafka_0.10", "Kafka", "kafka"),
    KAFKA_09(18, 124, "Kafka_0.9", "Kafka", "kafka"),
    Confluent5(79, 125, "confluent 5.x", "confluent", "confluent5"),
    EMQ(34, 126, "EMQ", "EMQ", "emq"),
    WEB_SOCKET(42, 127, "WebSocket", "WebSocket", "websocket"),
    SOCKET(44, 128, "Socket", "Socket", "socket"),
    RESTFUL(47, 129, "Restful", "Restful", "restful"),
    VERTICA(43, 130, "Vertica", "Vertica", "vertica"),
    VERTICA11(69, 130, "Vertica_11", "Vertica", "vertica"),
    INFLUXDB(55, 131, "InfluxDB", "InfluxDB", "influxdb"),
    OPENTSDB(56, 132, "OpenTSDB", "OpenTSDB", "opentsdb"),
    Spark(1002, 134, "Spark", "Spark", "spark"),
    KylinRestful(58, 135, "KylinRestful", "KylinRestful", "kylinrestful"),

    DorisRestful(64, 139, "Doris0.14.x(http)", "Doris0.14.x(http)", "dorisrestful"),
    HIVE3_CDP(65, 140, "Hive3_CDP", "Hive3_CDP", "hive3_cdp"),

    CSP_S3(75, 148, "CSP S3", "CSP S3", "csp_s3"),

    YARN2(80, 146, "YARN2", "YARN2", "yarn2"),
    YARN3(81, 147, "YARN3", "YARN3", "yarn3"),
    KUBERNETES(84, 150, "KUBERNETES", "KUBERNETES", "kubernetes"),
    NFS(85, 151, "NFS", "NFS", "nfs"),
    TDENGINE(87, 152, "TDengine", "TDengine", "tdengine"),

    // 增加 kerberos 处理
    KERBEROS(300, 300, "KERBEROS", "KERBEROS", "kerberos");

    DataSourceType(int val, int order, String name, String groupTag, String pluginName) {
        this.val = val;
        this.order = order;
        this.name = name;
        this.groupTag = groupTag;
        this.pluginName = pluginName;
    }

    private static final List<Integer> RDBM_S = new ArrayList<>();
    private static final List<Integer> KAFKA_S = new ArrayList<>();
    public static final Map<String, List<Integer>> GROUP = new HashMap<>(DataSourceType.values().length);

    static {
        RDBM_S.add(MySQL.val);
        RDBM_S.add(MySQL8.val);
        RDBM_S.add(MySQLPXC.val);
        RDBM_S.add(Polardb_For_MySQL.val);
        RDBM_S.add(Oracle.val);
        RDBM_S.add(SQLServer.val);
        RDBM_S.add(SQLSERVER_2017_LATER.val);
        RDBM_S.add(PostgreSQL.val);
        RDBM_S.add(DB2.val);
        RDBM_S.add(DMDB.val);
        RDBM_S.add(RDBMS.val);
        RDBM_S.add(HIVE.val);
        RDBM_S.add(HIVE1X.val);
        RDBM_S.add(HIVE3X.val);
        RDBM_S.add(HIVE3_CDP.val);
        RDBM_S.add(Spark.val);
        RDBM_S.add(SparkThrift2_1.val);
        RDBM_S.add(Presto.val);
        RDBM_S.add(Kylin.val);
        RDBM_S.add(VERTICA.val);
        RDBM_S.add(VERTICA11.val);
        RDBM_S.add(GREENPLUM6.val);
        RDBM_S.add(LIBRA.val);
        RDBM_S.add(GBase_8a.val);
        RDBM_S.add(Clickhouse.val);
        RDBM_S.add(TiDB.val);
        RDBM_S.add(CarbonData.val);
        RDBM_S.add(ADS.val);
        RDBM_S.add(ADB_FOR_PG.val);
        RDBM_S.add(Phoenix.val);
        RDBM_S.add(PHOENIX5.val);
        RDBM_S.add(IMPALA.val);
        RDBM_S.add(OceanBase.val);
        RDBM_S.add(INCEPTOR.val);
        RDBM_S.add(KINGBASE8.val);
        RDBM_S.add(TRINO.val);
        RDBM_S.add(ICEBERG.val);
        RDBM_S.add(DMDB_For_Oracle.val);
        RDBM_S.add(SAP_HANA1.val);
        RDBM_S.add(SAP_HANA2.val);
        RDBM_S.add(GREAT_DB.val);

        KAFKA_S.add(KAFKA.val);
        KAFKA_S.add(KAFKA_09.val);
        KAFKA_S.add(KAFKA_10.val);
        KAFKA_S.add(KAFKA_11.val);
        KAFKA_S.add(KAFKA_2X.val);

        for (DataSourceType dataSourceType : DataSourceType.values()) {
            List<Integer> types = DataSourceType.GROUP.get(dataSourceType.getGroupTag());
            if (null == types) {
                types = Lists.newArrayList();
            }
            types.add(dataSourceType.val);
            DataSourceType.GROUP.put(dataSourceType.getGroupTag(), types);
        }
    }


    /**
     * 数据源值
     */
    private final int val;

    /**
     * 排序顺序
     */
    private final int order;

    /**
     * 数据源名称
     */
    private final String name;

    /**
     * 数据源所属组
     */
    private final String groupTag;

    /**
     * 插件包目录名称
     */
    private final String pluginName;

    /**
     * 根据值获取数据源类型
     *
     * @param value 数据源插件 val
     * @return 数据源插件枚举
     */
    public static @NotNull DataSourceType getSourceType(int value) {
        for (DataSourceType type : DataSourceType.values()) {
            if (type.val == value) {
                return type;
            }
        }
        throw new SourceException(String.format("Data source type[%s] is not supported", value));
    }

    public Integer getVal() {
        return val;
    }

    public String getName() {
        return name;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getGroupTag() {
        return groupTag;
    }

    public int getOrder() {
        return order;
    }

    /**
     * 获取所有的关系数据库
     *
     * @return 所有关系型数据库的 val
     */
    public static List<Integer> getRDBMS() {
        return RDBM_S;
    }

    /**
     * 获取所有的 kafka 相关数据源
     *
     * @return 所有 kafka 相关数据源的 val
     */
    public static List<Integer> getKafkaS() {
        return KAFKA_S;
    }

    /**
     * 用来计算未使用的最小数据源值
     *
     * @param args main args
     */
    public static void main(String[] args) {
        List<DataSourceType> collect = Arrays.stream(DataSourceType.values()).sorted(Comparator.comparingInt(DataSourceType::getVal)).collect(Collectors.toList());
        // 利用 toMap 特性，校验 value 枚举值是否重复
        try {
            collect.stream().collect(Collectors.toMap(DataSourceType::getVal, Function.identity()));
        } catch (Exception e) {
            System.err.println(e);
            return;
        }
        int val = 1;
        for (DataSourceType dataSourceType : collect) {
            val = val == dataSourceType.getVal() - 1 ? dataSourceType.getVal() : val;
        }
        System.out.println("Sys.out.currentVal : " + (val + 1));
    }
}