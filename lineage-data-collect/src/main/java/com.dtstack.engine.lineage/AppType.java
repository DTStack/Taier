package com.dtstack.engine.lineage;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/2/9
 */
public enum AppType {

    /**
     * 离线
     */
    RDOS(1),
    /**
     * 数据质量
     */
    DQ(2),
    /**
     * 数据api
     */
    API(3),
    /**
     * tag
     */
    TAG(4),
    /**
     * 老资产
     */
    MAP(5),
    /**
     * 控制台
     */
    CONSOLE(6),
    /**
     * 流计算
     */
    STREAM(7),
    /**
     * 算法平台
     */
    DATASCIENCE(8),
    /**
     * 数据资产
     */
    DATAASSETS(9),
    /**
     * dagschedulex
     */
    DAGSCHEDULEX(99);

    private int type;

    AppType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AppType getValue(int value) {
        AppType[] values = AppType.values();
        for (AppType appType : values) {
            if (appType.getType() == value) {
                return appType;
            }
        }
        return null;
    }
}
