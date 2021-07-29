package com.dtstack.batch.common.enums;

public enum FileMergeType {

    /**
     * 周期性治理
     */
    PERIOD(1,"周期性治理"),

    /**
     * 一次性治理
     */
    ONECE(2,"一次性治理");

    private Integer type;
    private String desc;

    FileMergeType(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
