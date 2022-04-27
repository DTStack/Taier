package com.dtstack.taier.develop.enums.develop;

public enum FlinkUDFType {

    /**
     * flink scala  函数
     */
    SCALA(0, "SCALAR"),

    /**
     * flink table  函数
     */
    TABLE(1, "TABLE"),

    /**
     * flink aggregate 函数
     */
    AGGREGATE(2, "AGGREGATE");

    /**
     * 函数的类型
     */
    int type;

    /**
     * 函数名称
     */
    String name;

    FlinkUDFType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }


    public static FlinkUDFType fromTypeValue(int type) {
        for (FlinkUDFType flinkUDFType : FlinkUDFType.values()) {
            if (type == flinkUDFType.type) {
                return flinkUDFType;
            }
        }
        return SCALA;
    }
}
