package com.dtstack.batch.common.enums;

/**
 * @author jiangbo
 * @date 2018/6/27 15:36
 */
public enum TempJobType {

    /**
     *
     */
    SELECT(1),
    /**
     *
     */
    INSERT(0),
    /**
     *
     */
    CREATE_AS(2),
    /**
     *
     */
    SIMPLE_SELECT(3),
    /**
     *
     */
    PYTHON_SHELL(4),
    /**
     *
     */
    SYNC_TASK(5),
    /**
     *
     */
    CARBON_SQL(6),
    /**
     *
     */
    CREATE(7);

    private Integer type;

    TempJobType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
