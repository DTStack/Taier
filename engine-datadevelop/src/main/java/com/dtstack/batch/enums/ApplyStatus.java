package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @time 2018/5/19
 */
public enum ApplyStatus {

    /**
     * 审批中/待审批
     */
    APPLYING(0),

    /**
     * 通过
     */
    PASS(1),

    /**
     * 不通过
     */
    UN_PASS(2),

    /**
     * 过期
     */
    EXPIRED(3),

    /**
     * 撤销
     */
    CANCEL(4);

    private Integer status;

    ApplyStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
