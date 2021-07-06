package com.dtstack.batch.enums;

/**
 * @author sanyue
 * @date 2018/12/12
 */
public enum CarbonDataConfigType {

    /**
     * default
     */
    DEFAULT("default"),

    /**
     * custom
     */
    CUSTOM("custom");

    private String name;

    CarbonDataConfigType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
