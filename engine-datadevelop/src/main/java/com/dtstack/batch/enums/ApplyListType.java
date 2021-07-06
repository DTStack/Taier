package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @date 2018/5/22 19:33
 */
public enum ApplyListType {

    /**
     * 待我审批
     */
    REPLY_BY_ME(0),

    /**
     * 我申请的
     */
    MY_APPLY(1),

    /**
     * 我已处理的
     */
    REPLIED_BY_ME(2),

    /**
     * 权限收回
     */
    PERMISSION_REVOKE(3);

    private Integer type;

    ApplyListType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
