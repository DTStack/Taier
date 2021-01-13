package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.SecurityLog;

import java.sql.Timestamp;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/6/4 21:51
 * @Description:
 */
public class SecurityLogDTO extends SecurityLog {

    private Timestamp startTime;

    private Timestamp endTime;

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }
}
