package com.dtstack.batch.enums;

public enum TaskOperateType {

    /**
     * resource
     */
    RESOURCE(0),

    /**
     * 修改
     */
    EDIT(1),

    /**
     * 创建
     */
    CREATE(2),

    /**
     * 冻结
     */
    FROZEN(3),

    /**
     * 解冻
     */
    THAW(4),

    /**
     * 提交
     */
    COMMIT(5);

    private int type;

    TaskOperateType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
