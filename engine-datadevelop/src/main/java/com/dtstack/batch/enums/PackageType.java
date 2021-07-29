package com.dtstack.batch.enums;

public enum  PackageType {

    /**
     * NOMAL
     */
    NOMAL(0),

    /**
     * IMPORT
     */
    IMPORT(1);

    private Integer status;

    PackageType(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
