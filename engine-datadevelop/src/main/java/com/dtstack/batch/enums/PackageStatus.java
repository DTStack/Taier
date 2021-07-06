package com.dtstack.batch.enums;

public enum PackageStatus {
    /**
     * 待发布
     */
    WAIT_PUBLISH(0),

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAILURE(2),

    /**
     * 发布中
     */
    PUBLISHING(3);

    private Integer status;

    PackageStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
