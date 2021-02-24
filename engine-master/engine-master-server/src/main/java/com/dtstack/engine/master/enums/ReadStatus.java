package com.dtstack.engine.master.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/3/8
 */
public enum ReadStatus {

    UNREAD(0), READ(1), ALL(2);

    private int status;

    ReadStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
