package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @time 2018/5/19
 */
public enum HiveTablePermissionType {

    /**
     * 只读
     */
    READ(0),

    /**
     * 读写
     */
    READ_WRITE(1),

    /**
     * 修改
     */
    ALTER(2);

    private Integer type;

    HiveTablePermissionType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
