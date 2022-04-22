package com.dtstack.taier.develop.enums.develop;

/**
 * @author zhiChen
 * https://dtstack.yuque.com/rd-center/sm6war/lvgh5o
 * @date 2021/9/17 10:16
 */
public enum TaskDirtyOutPutTypeEnum {


    LOG("log"),

    JDBC("jdbc"),

    ;


    private final String value;

    TaskDirtyOutPutTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
