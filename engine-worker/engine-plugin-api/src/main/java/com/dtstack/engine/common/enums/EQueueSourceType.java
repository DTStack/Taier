package com.dtstack.engine.common.enums;

/**
 * @author yuebai
 * @date 2020-11-18
 */
public enum EQueueSourceType {
    NORMAL(0, "normalQueue"),
    DELAY(1, "delayQueue");

    private int code;
    private String name;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    EQueueSourceType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
