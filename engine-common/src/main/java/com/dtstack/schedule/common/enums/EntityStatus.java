package com.dtstack.schedule.common.enums;

/**
 * @author yuebai
 * @date 2020-04-28
 */
public enum EntityStatus {
    normal(0),
    disable(1);

    private int status;

    private EntityStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }
}
