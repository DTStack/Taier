package com.dtstack.taier.datasource.api.source;

/**
 * @author ：Nanqi
 * company: www.dtstack.com
 * Date ：Created in 10:39 2020/7/27
 * Description：数据源基础
 */
public enum DataBaseType {
    MySql("mysql", "com.mysql.jdbc.Driver"),
    TDDL("mysql", "com.mysql.jdbc.Driver"),
    DRDS("drds", "com.mysql.jdbc.Driver"),
    Oracle("oracle", "oracle.jdbc.OracleDriver"),
    SQLServer("sqlserver", "net.sourceforge.jtds.jdbc.Driver", "select 1111"),
    SQLSSERVER_2017_LATER("sqlserver_2017_later", "com.microsoft.sqlserver.jdbc.SQLServerDriver", "select 1111"),
    PostgreSQL("postgresql", "org.postgresql.Driver"),
    RDBMS("rdbms", "com.alibaba.rdbms.plugin.rdbms.util.DataBaseType"),
    DB2("db2", "com.ibm.db2.jcc.DB2Driver"),
    HIVE("hive", "org.apache.hive.jdbc.HiveDriver"),
    HIVE3("hive3", "org.apache.hive.jdbc.HiveDriver"),
    CarbonData("carbonData", "org.apache.hive.jdbc.HiveDriver"),
    Spark("hive", "org.apache.hive.jdbc.HiveDriver"),
    INCEPTOR("inceptor", "org.apache.hive.jdbc.HiveDriver"),
    ADS("mysql", "com.mysql.jdbc.Driver"),
    ADB_FOR_PG("postgresql", "org.postgresql.Driver"),
    RDS("mysql", "com.mysql.jdbc.Driver"),
    MaxCompute("maxcompute", "com.aliyun.odps.jdbc.OdpsDriver"),
    LIBRA("postgresql", "org.postgresql.Driver"),
    GBase8a("gbase", "com.gbase.jdbc.Driver", "select 1111"),
    Kylin("kylin", "org.apache.kylin.jdbc.Driver", "select 1111"),
    Kudu("kudu", "org.apache.hive.jdbc.HiveDriver"),
    Impala("impala", "com.cloudera.impala.jdbc41.Driver"),
    Clickhouse("clickhouse", "ru.yandex.clickhouse.ClickHouseDriver"),
    HIVE1X("hive1", "org.apache.hive.jdbc.HiveDriver", "show tables"),
    Polardb_For_MySQL("mysql", "com.mysql.jdbc.Driver", "!table"),
    Phoenix("Phoenix", "org.apache.phoenix.jdbc.PhoenixDriver"),
    TiDB("TiDB", "com.mysql.jdbc.Driver"),
    MySql8("mysql8", "com.mysql.cj.jdbc.Driver"),
    DMDB("DMDB For MySQL", "dm.jdbc.driver.DmDriver"),
    DMDB_For_Oracle("DMDB For Oracle", "dm.jdbc.driver.DmDriver"),
    Greenplum6("Greenplum6", "com.pivotal.jdbc.GreenplumDriver"),
    Phoenix5("Phoenix5", "org.apache.phoenix.jdbc.PhoenixDriver"),
    KINGBASE8("kingbase8", "com.kingbase8.Driver"),
    Presto("presto", "com.facebook.presto.jdbc.PrestoDriver", "select 1111"),
    TRINO("trino", "io.trino.jdbc.TrinoDriver", "select 1111"),
    OceanBase("oceanbase", "com.alipay.oceanbase.jdbc.Driver", "select 1111"),
    Doris("doris", "com.mysql.jdbc.Driver", "select 1111"),
    sapHana1("sapHana1", "com.sap.db.jdbc.Driver", "SELECT CURRENT_SCHEMA FROM DUMMY"),
    sapHana2("sapHana2", "com.sap.db.jdbc.Driver", "SELECT CURRENT_SCHEMA FROM DUMMY"),
    TDengine("TDengine", "com.taosdata.jdbc.rs.RestfulDriver", "SELECT 1111");

    private String typeName;
    private String driverClassName;
    private String testSql;

    DataBaseType(String typeName, String driverClassName) {
        this.typeName = typeName;
        this.driverClassName = driverClassName;
    }

    DataBaseType(String typeName, String driverClassName, String testSql) {
        this.typeName = typeName;
        this.driverClassName = driverClassName;
        this.testSql = testSql;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getTestSql() {
        return testSql;
    }

    public String getTypeName() {
        return typeName;
    }
}
