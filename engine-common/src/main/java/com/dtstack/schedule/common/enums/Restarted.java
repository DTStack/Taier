package com.dtstack.schedule.common.enums;

/**
 * Reason:
 * Date: 2017/8/15
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public enum Restarted {

    NORMAL(0), RESTARTED(1);

    private int status;

    Restarted(int status){
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
