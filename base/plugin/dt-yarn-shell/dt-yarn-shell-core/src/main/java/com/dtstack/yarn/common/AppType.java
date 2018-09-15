package com.dtstack.yarn.common;


import org.apache.commons.lang.StringUtils;

public enum AppType {
    TENSORFLOW,
    MXNET,
    SHELL,
    PYTHON,
    NONE;

    public static AppType fromString(String type) {
        if (StringUtils.isBlank(type)) {
           return NONE;
        }
        return valueOf(type.toUpperCase());
    }
}
