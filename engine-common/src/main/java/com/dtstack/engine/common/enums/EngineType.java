package com.dtstack.engine.common.enums;

/**
 * Company: www.dtstack.com
 * @author toutian
 */

public enum EngineType {
    Flink,
    Spark,
    Datax,
    Learning,
    DtScript,
    Mysql,
    Oracle,
    Sqlserver,
    Maxcompute,
    Hadoop,
    Hive,
    PostgreSQL,
    Kylin,
    Impala,
    TiDB,
    GreenPlum,
    Dummy,
    Presto,
    KingBase,
    InceptorSQL;

    public static EngineType getEngineType(String type) {

        switch (type.toLowerCase()) {
            case "flink":
                return EngineType.Flink;
            case "spark":
                return EngineType.Spark;
            case "datax":
                return EngineType.Datax;
            case "learning":
                return EngineType.Learning;
            case "dtscript":
                return EngineType.DtScript;
            case "mysql":
                return EngineType.Mysql;
            case "tidb":
                return EngineType.TiDB;
            case "oracle":
                return EngineType.Oracle;
            case "sqlserver":
                return EngineType.Sqlserver;
            case "maxcompute":
                return EngineType.Maxcompute;
            case "hadoop":
                return EngineType.Hadoop;
            case "hive":
                return EngineType.Hive;
            case "postgresql":
                return EngineType.PostgreSQL;
            case "kylin":
                return EngineType.Kylin;
            case "impala":
                return EngineType.Impala;
            case "greenplum":
                return EngineType.GreenPlum;
            case "dummy":
                return EngineType.Dummy;
            case "presto":
                return EngineType.Presto;
            case "kingbase":
                return EngineType.KingBase;
            case "inceptor":
                return EngineType.InceptorSQL;
            default:
                throw new UnsupportedOperationException("unsupported operation exception");
        }
    }

    public static boolean isFlink(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("flink")) {
            return true;
        }

        return false;
    }

}
