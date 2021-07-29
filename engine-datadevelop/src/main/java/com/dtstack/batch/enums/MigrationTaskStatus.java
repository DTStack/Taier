package com.dtstack.batch.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/8
 */
public enum MigrationTaskStatus {

    /**
     * 初始
     */
    INIT(0),

    /**
     * 成功
     */
    SUCCESS(1),

    /**
     * 失败
     */
    FAILED(2);

    private int status;

    MigrationTaskStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
