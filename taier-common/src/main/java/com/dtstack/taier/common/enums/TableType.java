package com.dtstack.taier.common.enums;


import com.dtstack.taier.common.exception.DtCenterDefException;

/**
 * @author qianyi
 */
public enum TableType {
    /**
     * 源表
     */
    SOURCE(1, "源表"),

    /**
     * 结果表
     */
    SINK(2, "结果表"),

    /**
     * 维表
     */
    SIDE(3, "维表");

    private final Integer tableType;

    private final String name;


    public Integer getTableType() {
        return tableType;
    }

    public String getName() {
        return name;
    }

    TableType(Integer tableType, String name) {
        this.tableType = tableType;
        this.name = name;
    }

    public static TableType getByType(Integer type) {
        for (TableType tableType : values()) {
            if (tableType.tableType.equals(type)) {
                return tableType;
            }
        }
        throw new DtCenterDefException(String.format("找不到[%s]对应的表类型", type));
    }
}
