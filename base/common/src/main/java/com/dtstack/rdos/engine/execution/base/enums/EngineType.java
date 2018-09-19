package com.dtstack.rdos.engine.execution.base.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum EngineType {

    Flink(0), Spark(1), Datax(2),Learning(3), DtYarnShell(4);

    private int val;

    EngineType(int val){
        this.val = val;
    }

    public int getVal() {
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

            case "dtyarnshell": return EngineType.DtYarnShell;

            default:
                throw new UnsupportedOperationException("不支持的操作类型");
        }
    }

    public static EngineType getEngineType(int val){
        for(EngineType type : EngineType.values()){
            if(type.val == val){
                return type;
            }
        }
        throw new UnsupportedOperationException("不支持的操作类型");
    }

    public static boolean isFlink(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("flink")){
            return true;
        }

        return false;
    }

    public static boolean isSpark(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("spark")){
            return true;
        }

        return false;
    }

    public static boolean isHadoop(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("hadoop")){
            return true;
        }

        return false;
    }

    public static boolean isDataX(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("datax")){
            return true;
        }

        return false;
    }

    public static boolean isMysql(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("mysql")){
            return true;
        }

        return false;
    }

    public static boolean isLearning(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("learning")){
            return true;
        }

        return false;
    }

    public static boolean isDtYarnShell(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("dt-yarn-shell")){
            return true;
        }

        return false;
    }

    public static String getEngineTypeWithoutVersion(String engineType){
        Pattern pattern = Pattern.compile("([a-zA-Z]+).*");
        Matcher matcher = pattern.matcher(engineType);
        if(!matcher.find()){
            return engineType;
        }

        return matcher.group(1);
    }

}
