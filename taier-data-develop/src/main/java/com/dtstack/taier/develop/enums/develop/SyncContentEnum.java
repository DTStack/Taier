package com.dtstack.taier.develop.enums.develop;

/**
 * @author zhiChen
 * @date 2021/11/1 14:33
 */
public enum SyncContentEnum {

    /**
     * 数据同步
     */
    DATA_SYNC(1,"数据同步"),

    /**
     * 数据&结构同步
     */
    DATA_STRUCTURE_SYNC(2,"数据&结构同步");

    private Integer type;
    private String name;

    SyncContentEnum(Integer type, String name){
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
