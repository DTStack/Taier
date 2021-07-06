package com.dtstack.batch.enums;

/**
 * date: 2021/5/26 11:10 上午
 * author: zhaiyue
 */
public enum SelectSqlTypeEnum {

    /**
     * IMAPLA
     */
    IMPALA(1),

    /**
     * INCEPTOR
     */
    INCEPTOR(2);

    private Integer type;

    SelectSqlTypeEnum(Integer typeCode) {
        this.type = typeCode;
    }

    public Integer getType() {
        return type;
    }

}
