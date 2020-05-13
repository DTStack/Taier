package com.dtstack.engine.master.enums;

/**
 * @author yuebai
 * @date 2020-05-11
 */
public enum  EFrontType {
    INPUT(0,"input"),
    RADIO(1,"radio");

    private int code;
    private String name;

    EFrontType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
