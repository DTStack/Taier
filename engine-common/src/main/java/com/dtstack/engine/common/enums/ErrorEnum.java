package com.dtstack.engine.common.enums;

/**
 * @author 猫爸
 */
public enum ErrorEnum implements DTEnums {
    SUCCESS(1, "执行成功");
    private int code;
    private String description;

    ErrorEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getDescription() {
        return this.description;
    }
}
