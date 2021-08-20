package com.dtstack.engine.common.enums;

/**
 *
 * 引擎类型
 * Date: 2019/5/28
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum MultiEngineType {

    HADOOP(1,"Hadoop"),
    LIBRA(2, "LibrA"),
    KYLIN(3, "Kylin"),
    TIDB(4,"TiDB"),
    ORACLE(5,"Oracle"),
    GREENPLUM(6, "Greenplum"),
    PRESTO(7, "Presto");

    private int type;

    private String name;

    public String getName() {
        return name;
    }

    MultiEngineType(int type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType(){
        return this.type;
    }

    public static MultiEngineType getByName(String name){
        for (MultiEngineType value : MultiEngineType.values()) {
            if(value.getName().equalsIgnoreCase(name)){
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + name);
    }

    public static MultiEngineType getByType(int type){
        for (MultiEngineType value : MultiEngineType.values()) {
            if(value.getType() == type){
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + type);
    }
}
