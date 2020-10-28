package com.dtstack.lineage.enums;

/**
 * @author chener
 * @Classname DataSourceType
 * @Description 数据源类型枚举
 * @Date 2020/10/23 15:23
 * @Created chener@dtstack.com
 */
public enum DataSourceType {
    HIVE1(1),
    HIVE2(2),
    SPARK_THRIFT(3),
    IMPALA(4),
    TIDB(5),
    ORACLE(6),
    LIBRA(7),
    MYSQL(8),
    GREENPLUM(9),
    SQLSERVER(10)
    ;
    private int type;

    public int getType() {
        return type;
    }

    DataSourceType(int type) {
        this.type = type;
    }
}
