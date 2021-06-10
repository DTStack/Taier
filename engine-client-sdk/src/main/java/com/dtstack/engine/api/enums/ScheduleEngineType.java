package com.dtstack.engine.api.enums;


import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public enum ScheduleEngineType {

    Flink(0, "flink"),
    Spark(1, "spark"),
    Datax(2, "datax"),
    Learning(3, "learning"),
    Shell(4, "shell"),
    Python2(5, "python2"),
    DtScript(6, "dtScript"),
    Python3(7, "python3"),
    Hadoop(8, "hadoop"),
    Carbon(9, "carbon"),
    Libra(10, "postgresql"),
    Kylin(11, "kylin"),
    HIVE(12, "hive"),
    IMPALA(13, "impala"),
    TIDB(14, "tidb"),
    ORACLE(15, "oracle"),
    GREENPLUM(16, "greenplum"),
    KUBERNETES(17, "kubernetes"),
    MYSQL(18, "mysql"),
    SQLSERVER(19, "sqlserver"),
    MAX_COMPUTE(20, "maxcompute"),
    DUMMY(21, "dummy"),
    Presto(22, "presto"),
    KING_BASE(23,"kingbase"),
    INCEPTOR_SQL(24,"inceptor"),
    DTSCRIPT_AGENT(25,"dtscript-agent"),
    FLINK_ON_STANDALONE(26,"flink-on-standalone"),
    ANALYTICDB_FOR_PG(27,"adb-postgresql");

    private int val;

    private String engineName;

    ScheduleEngineType(int val, String engineName) {
        this.val = val;
        this.engineName = engineName;
    }

    private static final Map<String ,ScheduleEngineType> ENGINE_CACHE;
    static {
        ENGINE_CACHE = Arrays.stream(values()).collect(Collectors.toConcurrentMap(ScheduleEngineType::getEngineName,val->val));
    }

    public int getVal() {
        return val;
    }

    public String getEngineName() {
        return engineName;
    }

    public static ScheduleEngineType getEngineType(String type) {

        switch (type.toLowerCase()) {

            case "flink":
                return ScheduleEngineType.Flink;

            case "spark":
                return ScheduleEngineType.Spark;

            case "datax":
                return ScheduleEngineType.Datax;

            case "learning":
                return ScheduleEngineType.Learning;

            case "python2":
                return ScheduleEngineType.Python2;

            case "python3":
                return ScheduleEngineType.Python3;

            case "shell":
                return ScheduleEngineType.Shell;

            case "dtscript":
                return ScheduleEngineType.DtScript;

            case "hadoop":
                return ScheduleEngineType.Hadoop;

            case "carbon":
                return ScheduleEngineType.Carbon;

            case "librasql":
                return ScheduleEngineType.Libra;
            case "hive":
                return ScheduleEngineType.HIVE;
            case "libra":
                return ScheduleEngineType.Libra;
            case "kylin":
                return ScheduleEngineType.Kylin;
            case "impala":
                return ScheduleEngineType.IMPALA;
            case "tidb":
                return ScheduleEngineType.TIDB;
            case "oracle":
                return ScheduleEngineType.ORACLE;
            case "greenplum":
                return ScheduleEngineType.GREENPLUM;
            case "kubernetes":
                return ScheduleEngineType.KUBERNETES;
            case "mysql":
                return ScheduleEngineType.MYSQL;
            case "sqlserver":
                return ScheduleEngineType.SQLSERVER;
            case "maxcompute":
                return ScheduleEngineType.MAX_COMPUTE;
            case "dummy":
                return ScheduleEngineType.DUMMY;
            case "presto":
                return ScheduleEngineType.Presto;
            case "kingbase":
                return ScheduleEngineType.KING_BASE;
            case "flinkonstandalone":
                return ScheduleEngineType.FLINK_ON_STANDALONE;
            default:
                return ENGINE_CACHE.get(type);
        }
    }
}
