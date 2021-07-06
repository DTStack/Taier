package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @date 2018/6/12 17:32
 */
public enum BatchHiveTablePermissionType {

    /**
     * 未授权的
     */
    UN_PERMISSION(0),

    /**
     * 授权的
     */
    PERMISSIONED(1),

    /**
     * 待审批的状态
     */
    WAIT_REPLY(2),

    /**
     * 我管理的
     */
    MANAGER(3);

    private Integer type;

    BatchHiveTablePermissionType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
