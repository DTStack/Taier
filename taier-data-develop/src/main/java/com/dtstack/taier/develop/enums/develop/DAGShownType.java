package com.dtstack.taier.develop.enums.develop;

public enum DAGShownType {

    /**
     * 最大值
     */
    MAX(0.99),

    /**
     * 平均数
     */
    AVERAGE(0.5);

    private Double value;

    public Double getValue() {
        return value;
    }

    DAGShownType(Double value) {
        this.value = value;
    }
}
