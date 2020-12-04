package com.dtstack.engine.api.enums;

import java.util.Objects;

/**
 * @author chener
 * @Classname DataSourceType
 * @Description 数据源类型枚举
 * @Date 2020/10/23 15:23
 * @Created chener@dtstack.com
 */
public enum DataSourceType {
    HIVE1(1,"Hive1Server"),
    HIVE2(2,"HiveServer"),
    SPARK_THRIFT(3,"SparkThrift"),
    IMPALA(4,"Impala SQL"),
    TIDB(5,"TiDB SQL"),
    ORACLE(6,"Oracle SQL"),
    LIBRA(7,"LibrA SQL"),
    MYSQL(8,"Mysql"),
    GREENPLUM(9,"Greenplum SQL"),
    SQLSERVER(10,"SqlServer SQL"),
    //手动添加的数据源
    CUSTOM(11,"custom")
    ;
    private int type;

    private String name;

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    DataSourceType(int type,String name) {

        this.type = type;
        this.name = name;
    }

    public static DataSourceType getByType(Integer type){
        if (Objects.isNull(type)){
            return null;
        }
        for (DataSourceType sourceType:values()){
            if (sourceType.getType() == type){
                return sourceType;
            }
        }
        return null;
    }

    public static DataSourceType getByName(String name){
        if (Objects.isNull(name)){
            return null;
        }
        for (DataSourceType sourceType:values()){
            if (sourceType.getName() == name){
                return sourceType;
            }
        }
        return null;
    }
}
