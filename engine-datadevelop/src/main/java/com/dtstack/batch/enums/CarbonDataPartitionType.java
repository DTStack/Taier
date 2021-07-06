package com.dtstack.batch.enums;

/**
 * @author sanyue
 * @date 2018/12/11
 */
public enum CarbonDataPartitionType {

    /**
     * native_hive
     */
    NATIVE_HIVE(1),

    /**
     * hash
     */
    HASH(2),

    /**
     * range
     */
    RANGE(3),

    /**
     * list
     */
    LIST(4);

    private Integer type;

    CarbonDataPartitionType(Integer type) {
        this.type = type;

    }

    public Integer getType() {
        return type;
    }
}
