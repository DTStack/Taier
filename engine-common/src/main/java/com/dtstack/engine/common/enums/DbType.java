package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 9:20 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum DbType {

    Oracle(2),
    TiDB(31),
    GREENPLUM6(36),
    ANALYTICDB_FOR_PG(54),
    MYSQL(1),
    OCEANBASE(55);

    private int typeCode;

    DbType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
