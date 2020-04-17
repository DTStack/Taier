package com.dtstack.schedule.common.enums;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/11/04
 */
public enum Expired {

    NONE(0), EXPIRE(1);

    private int val;

    Expired(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}
