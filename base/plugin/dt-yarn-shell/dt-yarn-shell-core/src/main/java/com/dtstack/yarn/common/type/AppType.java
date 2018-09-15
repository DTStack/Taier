package com.dtstack.yarn.common.type;


import org.apache.commons.lang.StringUtils;

public abstract class AppType {
//    SHELL,
//    PYTHON,
//    JLOGSTASH,
//    NONE;

    public static AppType fromString(String type) {
        if (StringUtils.isBlank(type)) {
           return NONE;
        }
        return valueOf(type.toUpperCase());
    }
}
