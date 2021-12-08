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

package com.dtstack.engine.common.enums;


import com.dtstack.engine.pluginapi.constrant.ComponentConstant;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 * 值 1000 以上表示未启用，后续标号
 */
public enum DataSourceType {
    MySQL(1),
    MySQL8(1001),
    MySQLPXC(98),
    Polardb_For_MySQL(28),
    Oracle(2),
    SQLServer(3),
    SQLSERVER_2017_LATER(32),
    PostgreSQL(4),
    DB2(19),
    DMDB(35),
    RDBMS(5),
    KINGBASE8(40),
    HIVE(7),
    HIVE1X(27),
    HIVE3(50),
    MAXCOMPUTE(10),
    GREENPLUM6(36),
    LIBRA(21),
    GBase_8a(22),
    DORIS(57),
    HDFS(6),
    FTP(9),
    S3(41),
    AWS_S3(51),
    SPARKTHRIFT2_1(45),
    IMPALA(29),
    Clickhouse(25),
    TiDB(31),
    CarbonData(20),
    Kudu(24),
    ADS(15),
    ADB_POSTGREPSQL(54),
    Kylin(23),
    Presto(48),
    OceanBase(49),
    INCEPTOR_SQL(52),
    HBASE(8),
    HBASE2(39),
    Phoenix(30),
    PHOENIX5(38),
    ES(11),
    ES6(33),
    ES7(46),
    MONGODB(13),
    REDIS(12),
    SOLR(53),
    HBASE_GATEWAY(99),
    KAFKA_2X(37),
    KAFKA(26),
    KAFKA_11(14),
    KAFKA_10(17),
    KAFKA_09(18),
    EMQ(34),
    WEB_SOCKET(42),
    SOCKET(44),
    RESTFUL(47),
    VERTICA(43),
    INFLUXDB(55),
    OPENTSDB(56),
    BEATS(16),
    /**
     * spark thrift
     */
    Spark(1002),
    KylinRestful(58),

    /**
     * 因为数据资产新增自定义数据集，将该类型与之做对应
     */
    CUSTOM(1000),
    /**
     * 未知数据源，即类型暂时不确定，后续可能会修改为正确类型的数据源
     */
    UNKNOWN(3000);

    private int val;

    DataSourceType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }


    public static DataSourceType getSourceType(int value) {
        for (DataSourceType type : DataSourceType.values()) {
            if (type.val == value) {
                return type;
            }
        }

        throw new RdosDefineException("不支持数据源类型");
    }

    public static DataBaseType getBaseType(DataSourceType sourceType) {
        switch (sourceType) {
            case MySQL:
                return DataBaseType.MySql;
            case Oracle:
                return DataBaseType.Oracle;
            case SQLServer:
                return DataBaseType.SQLServer;
            case PostgreSQL:
                return DataBaseType.PostgreSQL;
            case LIBRA:
                return DataBaseType.LIBRA;
            case RDBMS:
                return DataBaseType.RDBMS;
            case HIVE:
                return DataBaseType.HIVE;
            case HIVE1X:
                return DataBaseType.HIVE1X;
            case MAXCOMPUTE:
                return DataBaseType.MaxCompute;
            case DB2:
                return DataBaseType.DB2;
            case ADS:
                return DataBaseType.ADS;
            case CarbonData:
                return DataBaseType.CarbonData;
            case GBase_8a:
                return DataBaseType.GBase8a;
            case Kylin:
                return DataBaseType.Kylin;
            case Kudu:
                return DataBaseType.Kudu;
            case Clickhouse:
                return DataBaseType.Clickhouse;
            case Polardb_For_MySQL:
                return DataBaseType.Polardb_For_MySQL;
            case IMPALA:
                return DataBaseType.Impala;
            case Phoenix:
                return DataBaseType.Phoenix;
            case TiDB:
                return DataBaseType.TiDB;
            case SQLSERVER_2017_LATER:
                return DataBaseType.SQLSSERVER_2017_LATER;
            case DMDB:
                return DataBaseType.DMDB;
            case GREENPLUM6:
                return DataBaseType.Greenplum6;
            case Presto:
                return DataBaseType.Presto;
            case INCEPTOR_SQL:
                return DataBaseType.Inceptor;
            case HIVE3:
                return DataBaseType.HIVE3;
            default:
                throw new RdosDefineException("不支持数据源类型");
        }
    }

    public static DataBaseType getBaseType(int value) {
        DataSourceType sourceType = getSourceType(value);
        return getBaseType(sourceType);
    }

    public static List<Integer> getRDBMS() {
        return Lists.newArrayList(
                MySQL.getVal(),
                Oracle.getVal(),
                SQLServer.getVal(),
                SQLSERVER_2017_LATER.getVal(),
                PostgreSQL.getVal(),
                HIVE.getVal(),
                HIVE1X.getVal(),
                DB2.getVal(),
                ADS.getVal(),
                CarbonData.getVal(),
                RDBMS.getVal(),
                GBase_8a.getVal(),
                LIBRA.getVal(),
                Clickhouse.getVal(),
                Polardb_For_MySQL.getVal(),
                IMPALA.getVal(),
                Phoenix.getVal(),
                TiDB.getVal(),
                DMDB.getVal(),
                GREENPLUM6.getVal(),
                Presto.getVal());
    }

    public static String getEngineType(DataSourceType sourceType) {

        switch (sourceType) {
            case MySQL:
                return "mysql";
            case HIVE:
            case SPARKTHRIFT2_1:
                return "hive2";
            case HIVE1X:
                return "hive";
            case GREENPLUM6:
                return "greenplum";
            case IMPALA:
                return "impala";
            case Oracle:
                return "oracle";
            case PostgreSQL:
                return "postgresql";
            case Presto:
                return "presto";
            case SQLServer:
                return "sqlserver";
            case TiDB:
                return "tidb";
            case KINGBASE8:
                return "kingbase";
            case ADB_POSTGREPSQL:
                return ComponentConstant.ANALYTICDB_FOR_PG_NAME;
            default:
                throw new RdosDefineException("不支持的数据源类型");
        }
    }

    public static DataSourceType convertEComponentType(EComponentType componentType, String version) {
        switch (componentType) {
            case TIDB_SQL:
                return TiDB;
            case IMPALA_SQL:
                return IMPALA;
            case ORACLE_SQL:
                return Oracle;
            case HIVE_SERVER:
                return StringUtils.isBlank(version) || version.startsWith("2") ? HIVE : HIVE1X;
            case GREENPLUM_SQL:
                return GREENPLUM6;
            case PRESTO_SQL:
                return Presto;
            case LIBRA_SQL:
                return PostgreSQL;
            case SPARK_THRIFT:
                return SPARKTHRIFT2_1;
            default:
                return null;
        }
    }

    public static List<DataSourceType> noNeedUserNamePasswordDataSources = Lists.newArrayList(DataSourceType.HBASE,
            DataSourceType.Phoenix,DataSourceType.HIVE,DataSourceType.SPARKTHRIFT2_1,
            DataSourceType.HIVE1X,DataSourceType.IMPALA,DataSourceType.HIVE3,DataSourceType.PHOENIX5,DataSourceType.INCEPTOR_SQL);


    public static List<Integer> hadoopDirtyDataSource = Lists.newArrayList(
            DataSourceType.HIVE1X.getVal(),
            DataSourceType.HIVE.getVal(),
            DataSourceType.HIVE3.getVal(),
            DataSourceType.SPARKTHRIFT2_1.getVal());

}
