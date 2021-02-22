package com.dtstack.engine.common.enums;

/**
 * @author yuebai
 * @date 2020-05-11
 */
public enum EFrontType {
    INPUT(0, "input"),
    RADIO(1, "radio"),
    GROUP(2, "group"),
    SELECT(3, "select"),
    CHECKBOX(4,"checkbox"),
    XML(5,"xml"),
    OTHER(6,"other");

    private int code;
    private String name;

    EFrontType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
