package com.dtstack.schedule.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/28
 */
public enum EProjectScheduleStatus {

    PAUSE(1), NORMAL(0);

    private Integer status;

    EProjectScheduleStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
