package com.dtstack.taier.develop.enums.develop;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:25 2019-07-04
 * @Description：数据同步建表策略
 */
public enum SyncCreateTableMode {
    /**
     * 自动建表
     */
    AUTO_CREATE(0),
    /**
     * 手动创建
     */
    MANUAL_SELECTION(1);

    private Integer mode;

    SyncCreateTableMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public static SyncCreateTableMode getByMode(Integer mode) {
        SyncCreateTableMode[] values = SyncCreateTableMode.values();
        for (SyncCreateTableMode value : values) {
            if (value.getMode().equals(mode)) {
                return value;
            }
        }
        return null;
    }
}
