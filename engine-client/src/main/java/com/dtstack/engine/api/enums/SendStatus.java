package com.dtstack.engine.api.enums;

/**
 * Reason:
 * Date: 2017/5/15
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public enum SendStatus {

    PREPARED(0), SENDSUCCESS(1), SENDFAILURE(2);

    private int status;

    SendStatus(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
