package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 9:15 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum EComponentApiType {

    HDFS(4),
    SPARK_THRIFT(6),
    CARBON_DATA(7),
    HIVE_SERVER(9),
    IMPALA_SQL(11),
    SFTP(10),
    PRESTO_SQL(16);

    private int typeCode;

    EComponentApiType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
