package com.dtstack.engine.common.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public enum EngineType {
    //
    Flink('0'),
    Spark('1'),
    Datax('2'),
    //
    Learning('3'),
    //
    DtScript('4'),
    //
    Mysql('5'),
    //
    Oracle('6'),
    //
    Sqlserver('7'),
    //
    Maxcompute('8'),
    //
    Hadoop('9'),
    //
    Hive('a'),
    //
    PostgreSQL('b'),
    //
    Kylin('c'),
    //
    Impala('d'),

    TiDB('e'),
    GreenPlum('f'),
    Dummy('g'),;
    private char val;

    EngineType(char val) {
        this.val = val;
    }

    public char getVal() {
        return val;
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
            default:
                throw new UnsupportedOperationException("unsupported operation exception");
        }
    }

    public static EngineType getEngineType(char val) {
        for (EngineType type : EngineType.values()) {
            if (type.val == val) {
                return type;
            }
        }
        throw new UnsupportedOperationException("unsupported operation exception");
    }

    public static boolean isFlink(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("flink")) {
            return true;
        }

        return false;
    }

    public static boolean isSpark(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("spark")) {
            return true;
        }

        return false;
    }

    public static boolean isHadoop(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("hadoop")) {
            return true;
        }

        return false;
    }

//    public static boolean isDataX(String engineType) {
//        engineType = engineType.toLowerCase();
//        if (engineType.startsWith("datax")) {
//            return true;
//        }
//
//        return false;
//    }

    public static boolean isMysql(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("mysql")) {
            return true;
        }

        return false;
    }

    public static boolean isLearning(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("learning")) {
            return true;
        }

        return false;
    }

    public static boolean isDtScript(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("dtscript")) {
            return true;
        }

        return false;
    }

    private final static Pattern PATTERN = Pattern.compile("([a-zA-Z]+).*");

    public static String getEngineTypeWithoutVersion(String engineType) {

        Matcher matcher = PATTERN.matcher(engineType);
        if (!matcher.find()) {
            return engineType;
        }

        return matcher.group(1);
    }

}
