package com.dtstack.engine.api.enums;

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
    MAX_COMPUTE(20, "maxCompute"),
    DUMMY(21, "dummy");
    private int val;

    private String engineName;

    ScheduleEngineType(int val, String engineName) {
        this.val = val;
        this.engineName = engineName;
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
            case "kubernetes":
                return ScheduleEngineType.KUBERNETES;
            case "mysql":
                return ScheduleEngineType.MYSQL;
        }
        return null;
    }

    public static ScheduleEngineType getEngineType(int val) {
        for (ScheduleEngineType type : ScheduleEngineType.values()) {
            if (type.val == val) {
                return type;
            }
        }

        return null;
    }

    public static String getEngineName(int val) {
        ScheduleEngineType scheduleEngineType = getEngineType(val);
        return scheduleEngineType.getEngineName().toLowerCase();
    }

    public static ScheduleEngineType getByEScriptType(Integer scriptType) {
        return getByPythonVersion(++scriptType);
    }

    public static ScheduleEngineType getByPythonVersion(Integer version) {
        ScheduleEngineType scheduleEngineType;
        if (version.equals(2)) {
            scheduleEngineType = Python2;
        } else if (version.equals(3)) {
            scheduleEngineType = Python3;
        } else {
            throw new UnsupportedOperationException("python不支持2.x和3.x之外的版本类型");
        }
        return scheduleEngineType;
    }

}
