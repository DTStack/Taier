package com.dtstack.batch.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/12/13
 */
public enum LifeStatus {

    /**
     * init
     */
    INIT(0),

    /**
     * normal
     */
    NORMAL(1),

    /**
     * destroy
     */
    DESTROY(2),

    /**
     * error
     */
    ERROR(3);

    private int value;

    public int getValue() {
        return value;
    }

    LifeStatus(int value) {
        this.value = value;
    }
}
