package com.dtstack.engine.sql.flink;

public enum EPluginLoadMode {

    /**
     * 0:classpath
     */
    CLASSPATH(0),
    /**
     * 1:shipfile
     */
    SHIPFILE(1),

    /**
     * 2:localTest
     */
    LOCALTEST(3);

    private int type;

    EPluginLoadMode(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }
}
