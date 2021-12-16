package com.dtstack.engine.common.enums;

/**
 * @author 猫爸
 */
public interface DTEnums {

    default int code() {
        return getCode();
    }

    default String description() {
        return getDescription();
    }

    int getCode();

    String getDescription();
}
