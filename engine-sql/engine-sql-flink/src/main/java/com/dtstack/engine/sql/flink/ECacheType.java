package com.dtstack.engine.sql.flink;

public enum ECacheType {
    /**
     * none
     */
    NONE,
    /**
     * lru
     */
    LRU,
    /**
     * all
     */
    ALL;

    public static boolean isValid(String type){
        for(ECacheType tmpType : ECacheType.values()){
            if(tmpType.name().equalsIgnoreCase(type)){
                return true;
            }
        }

        return false;
    }
}
