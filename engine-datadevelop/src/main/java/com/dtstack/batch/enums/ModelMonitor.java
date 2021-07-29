package com.dtstack.batch.enums;

/**
 * @author sanyue
 */
public enum ModelMonitor {

    /**
     * 表模型
     */
    TABLE(1),

    /**
     * 列模型
     */
    COLUMN(2);

    private int type;

    ModelMonitor(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
