package com.dtstack.batch.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/6
 */
public enum MigrationParalleType {

    /**
     * 分批上传
     */
    BATCH(1, "分批上传"),

    /**
     * 整批上传
     */
    STRAIGHT(2, "整批上传");

    private int type;

    private String value;

    MigrationParalleType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
