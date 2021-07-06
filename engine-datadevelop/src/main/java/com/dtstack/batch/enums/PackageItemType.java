package com.dtstack.batch.enums;

public enum PackageItemType {

    /**
     * 任务
     */
    TASK(0),

    /**
     * 表
     */
    TABLE(1),

    /**
     * 资源
     */
    RESOURCE(2),

    /**
     * 函数
     */
    FUNCTION(3),

    /**
     * 存储过程
     */
    PROCEDURE(4);

    private int type;

    PackageItemType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
