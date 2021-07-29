package com.dtstack.batch.sync.job;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public enum WriteMode {

    /**
     * append
     */
    APPEND("append"),

    /**
     * nonConflict
     */
    NONCONFLICT("nonConflict");

    private String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    WriteMode(String val) {
        this.val = val;
    }
}
