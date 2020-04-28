package com.dtstack.schedule.common.enums;


import com.dtstack.engine.common.exception.RdosDefineException;
import com.google.common.collect.Lists;

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
    Oracle(2),
    SQLServer(3),
    SQLSERVER_2017_LATER(32),//sqlserver 2017及以后
    PostgreSQL(4),
    RDBMS(5),
    HDFS(6),
    HIVE(7),
    HBASE(8),
    FTP(9),
    MAXCOMPUTE(10),
    ES(11),
    REDIS(12),
    MONGODB(13),
    KAFKA_11(14),
    ADS(15),
    BEATS(16),
    KAFKA_10(17),
    KAFKA_09(18),
    DB2(19),
    CarbonData(20),
    LIBRA(21),
    GBase_8a(22),
    Kylin(23),
    Kudu(24),
    Clickhouse(25),
    KAFKA(26),
    HIVE1X(27),
    Polardb_For_MySQL(28),
    IMPALA(29),
    Phoenix(30),
    TiDB(31),
    ES6(33),
    EMQ(34),
    DMDB(35),
    GREENPLUM6(36),
    ;

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
            default:
                throw new RdosDefineException("不支持数据源类型");
        }
    }

    public static DataBaseType getBaseType(int value) {
        DataSourceType sourceType = getSourceType(value);
        return getBaseType(sourceType);
    }

    public static List<Integer> getRDBMS(){
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
                GREENPLUM6.getVal());
    }
}
