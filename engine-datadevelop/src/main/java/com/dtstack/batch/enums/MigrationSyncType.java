package com.dtstack.batch.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/6
 */
public enum MigrationSyncType {

    /**
     * 增量
     */
    APPEND(1, "增量"),

    /**
     * 全量
     */
    OVERWRITE(2, "全量");

    private int type;

    private String value;

    MigrationSyncType(int type, String value) {
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
