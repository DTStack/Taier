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

package com.dtstack.taiga.common.enums;


import java.util.regex.Pattern;

/**
 * refer:http://blog.csdn.net/ring0hx/article/details/6152528
 * <p/>
 */
public enum DataBaseType {
    MySql("mysql", "com.mysql.jdbc.Driver"),
    TDDL("mysql", "com.mysql.jdbc.Driver"),
    DRDS("drds", "com.mysql.jdbc.Driver"),
    Oracle("oracle", "oracle.jdbc.OracleDriver"),
    SQLServer("sqlserver", "net.sourceforge.jtds.jdbc.Driver"),
    SQLSSERVER_2017_LATER("sqlserver_2017_later", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    PostgreSQL("postgresql", "org.postgresql.Driver"),
    RDBMS("rdbms", "com.alibaba.rdbms.plugin.rdbms.util.DataBaseType"),
    DB2("db2", "com.ibm.db2.jcc.DB2Driver"),
    HIVE("hive2", "org.apache.hive.jdbc.HiveDriver"),
    CarbonData("carbonData", "org.apache.hive.jdbc.HiveDriver"),
    Spark("Spark", "org.apache.hive.jdbc.HiveDriver"),
    ADS("ads","com.mysql.jdbc.Driver"),
    RDS("rds","com.mysql.jdbc.Driver"),
    MaxCompute("maxcompute","com.aliyun.odps.jdbc.OdpsDriver"),
    LIBRA("libra", "org.postgresql.Driver"),
    GBase8a("GBase8a","com.gbase.jdbc.Driver"),
    Kylin("Kylin","org.apache.kylin.jdbc.Driver"),
    Kudu("Kudu","org.apache.hive.jdbc.HiveDriver"),
    Impala("Impala", "com.cloudera.impala.jdbc41.Driver"),
    Clickhouse("Clickhouse","ru.yandex.clickhouse.ClickHouseDriver"),
    HIVE1X("hive", "org.apache.hive.jdbc.HiveDriver"),
    Polardb_For_MySQL("polardb for mysql","com.mysql.jdbc.Driver"),
    Phoenix("Phoenix", "org.apache.phoenix.jdbc.PhoenixDriver"),
    TiDB("TiDB", "com.mysql.jdbc.Driver"),
    MySql8("mysql8", "com.mysql.cj.jdbc.Driver"),
    DMDB("DMDB","dm.jdbc.driver.DmDriver"),
    Greenplum6("Greenplum6", "com.pivotal.jdbc.GreenplumDriver"),
    Presto("presto", "com.facebook.presto.jdbc.PrestoDriver"),
    HIVE3("hive3", "org.apache.hive.jdbc.HiveDriver"),
    Inceptor("inceptor", "org.apache.hive.jdbc.HiveDriver"),
    adb_Postgrepsql("adb-postgresql", "org.postgresql.Driver"),
    OCEANBASE("oceanbase", "com.mysql.jdbc.Driver");

    private String typeName;
    private String driverClassName;

    DataBaseType(String typeName, String driverClassName) {
        this.typeName = typeName;
        this.driverClassName = driverClassName;
    }


    private static Pattern mysqlPattern = Pattern.compile("jdbc:mysql://(.+):\\d+/.+");
    private static Pattern oraclePattern = Pattern.compile("jdbc:oracle:thin:@(.+):\\d+:.+");

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public static String getHiveTypeName(DataSourceType hiveDataSourceType) {
        switch (hiveDataSourceType) {
            case HIVE1X:
                return HIVE1X.getTypeName();
            case HIVE:
            case SPARKTHRIFT2_1:
                return HIVE.getTypeName();
            case HIVE3:
                return HIVE3.getTypeName();
            case INCEPTOR_SQL:
                return Inceptor.getTypeName();
            default:
                return null;
        }
    }

}
