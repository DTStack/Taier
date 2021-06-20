package com.dtstack.engine.datasource.common.enums;

public interface BaseEnum<C> {

    C getCode();

    String getName();

    default <T> boolean equalsCode(T code) {
        return code != null && code.equals(getCode());
    }

    default boolean equalsName(String name) {
        return name != null && name.equals(getName());
    }
}