package com.dtstack.batch.enums;

/**
 * @author jiangbo
 * @explanation
 * @date 2018/12/20
 */
public enum SyncModel {

    /**
     * 没有增量标识
     */
    NO_INCRE_COL(0),

    /**
     * 有增量标识
     */
    HAS_INCRE_COL(1);

    private int model;

    SyncModel(int model) {
        this.model = model;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }
}
