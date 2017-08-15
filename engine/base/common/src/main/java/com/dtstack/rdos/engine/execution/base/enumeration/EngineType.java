package com.dtstack.rdos.engine.execution.base.enumeration;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public enum EngineType {

    Flink120(0),Spark(1),Datax(2),Flink130(3),Sparkyarn(4);

    private int val;

    EngineType(int val){
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static EngineType getEngineType(String type){

        switch (type.toLowerCase()){

            case "flink120":return EngineType.Flink120;
            
            case "flink130":return EngineType.Flink130;

            case "spark":return EngineType.Spark;

            case "datax":return EngineType.Datax;

            case "sparkyarn": return EngineType.Sparkyarn;
        }
         return null;
    }

    public static EngineType getEngineType(int val){
        for(EngineType type : EngineType.values()){
            if(type.val == val){
                return type;
            }
        }

        return null;
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

    public static boolean isSparkYarn(String engineType){
        engineType = engineType.toLowerCase();
        if(engineType.startsWith("sparkyarn")){
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

}
