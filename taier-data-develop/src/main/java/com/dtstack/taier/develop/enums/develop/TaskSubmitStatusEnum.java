package com.dtstack.taier.develop.enums.develop;

/**
 * @author zhiChen
 * @date 2021/12/7 14:14
 */
public enum TaskSubmitStatusEnum {
    /**
     * 未提交
     */
    UNSUBMITTED(0),

    /**
     * 提交中
     */
    SUBMITTING(1),

    /**
     * 已提交
     */
    SUBMITTED(2),

    /**
     * 提交失败
     */
    SUBMISSION_FAILED(3),
    ;
    int status;

    private TaskSubmitStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
