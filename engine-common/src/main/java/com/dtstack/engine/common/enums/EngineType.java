package com.dtstack.engine.common.enums;


import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public enum EngineType {
    //
    Flink(0, "flink"),
    Spark(1, "spark"),
    Datax(2, "datax"),
    Learning(3,"learning"),
    Shell(4,"shell"),
    Python2(5,"python2"),
    DtScript(6, "dtScript"),
    Python3(7, "python3"),
    Hadoop(8, "hadoop"),
    Carbon(9, "carbon"),
    GaussDB(10, "postgresql"),
    Kylin(11,"kylin"),
    HIVE(12,"hive"),
    IMPALA(13, "impala"),
    TIDB(14,"tidb"),
    ORACLE(15,"oracle"),
    GREENPLUM(16,"greenplum"),
    KUBERNETES(17, "kubernetes"),
    MYSQL(18, "mysql"),
    SQLSERVER(19, "sqlserver"),
    MAX_COMPUTE(20, "maxcompute"),
    DUMMY(21, "dummy"),
    PRESTO(22, "presto"),
    KING_BASE(23,"kingbase"),
    INCEPTOR_SQL(24,"inceptor"),
    DTSCRIPT_AGENT(25,"dtscript-agent"),
    FLINK_ON_STANDALONE(26,"flink-on-standalone"),
    ANALYTICDB_FOR_PG(27,"adb-postgresql"),
    DB2(28, "db2"),
    OCEANBASE(29, "oceanbase"),
    TRINO(30, "trino");
   ;

    private final Integer val;

    private final String engineName;

    EngineType(int val, String engineName) {
        this.val = val;
        this.engineName = engineName;
    }

    public int getVal() {
        return val;
    }

    public String getEngineName(){
        return engineName;
    }

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

            case "python2":
                return EngineType.Python2;

            case "python3":
                return EngineType.Python3;

            case "shell":
                return EngineType.Shell;

            case "dtscript":
                return EngineType.DtScript;

            case "hadoop":
                return EngineType.Hadoop;

            case "carbon":
                return EngineType.Carbon;

            case "gaussdbsql":
                return EngineType.GaussDB;
            case "hive":
                return EngineType.HIVE;
            case "gaussdb":
                return EngineType.GaussDB;
            case"kylin":
                return EngineType.Kylin;
            case "impala":
                return EngineType.IMPALA;
            case "tidb":
                return EngineType.TIDB;
            case "presto":
                return EngineType.PRESTO;
            default:
                return null;
        }
    }

    public static EngineType getEngineType(int val) {
        for (EngineType type : EngineType.values()) {
            if (type.val == val) {
                return type;
            }
        }

        return null;
    }

    public static String getEngineName(int val){
        EngineType engineType = getEngineType(val);
        assert engineType != null;
        return engineType.getEngineName().toLowerCase();
    }

    public static EngineType getByEScriptType(Integer scriptType) {
        return getByPythonVersion(++scriptType);
    }
    public static EngineType getByPythonVersion(Integer version){
        EngineType engineType;
        if (version.equals(2)) {
            engineType = Python2;
        } else if (version.equals(3)) {
            engineType = Python3;
        } else {
            throw new DtCenterDefException(ErrorCode.PYTHON_NOT_SUPPORT);
        }
        return engineType;
    }

    /**
     * 判断该引擎类型 url是否需要 额外参数
     * @return
     */

    public static Boolean isNeedParam(String engineName){
        List<String> engineList = new ArrayList<>();
        engineList.add(EngineType.Flink.getEngineName());
        engineList.add(EngineType.Spark.getEngineName());
        engineList.add(EngineType.Datax.getEngineName());
        engineList.add(EngineType.Learning.getEngineName());
        engineList.add(EngineType.Shell.getEngineName());
        engineList.add(EngineType.Python2.getEngineName());
        engineList.add(EngineType.DtScript.getEngineName());
        engineList.add(EngineType.Hadoop.getEngineName());
        engineList.add(EngineType.Carbon.getEngineName());
        engineList.add(EngineType.GaussDB.getEngineName());
        engineList.add(EngineType.Kylin.getEngineName());
        engineList.add(EngineType.HIVE.getEngineName());
        engineList.add(EngineType.IMPALA.getEngineName());
        engineList.add(EngineType.TIDB.getEngineName());
        if (engineList.contains(engineName)){
            return true;
        }
        return false;
    }

}
