package com.dtstack.sdk.core.common;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 14:05 2021/03/20
 * @Description：Sdk 中 App 类型
 */
public enum SdkAppType {
    /**
     * 离线计算
     */
    BATCH(1, "BatchWork"),

    /**
     * 数据质量
     */
    DQ(2, "DataQuality"),

    /**
     * 数据api
     */
    API(3, "DataApi"),
    /**
     * 标签引擎
     */
    TAG(4, "TagEngine"),

    /**
     * 历史遗留
     */
    MAP(5, "Map"),

    /**
     * 控制台
     */
    CONSOLE(6, "Console"),

    /**
     * 流计算
     */
    STREAM(7, "StreamWork"),

    /**
     * 数据科学
     */
    DATASCIENCE(8, "DataScience"),

    /**
     * 数据资产
     */
    DATAASSETS(9, "DataAssets"),

    /**
     * 调度引擎
     */
    DAGSCHEDULEX(10, "DagScheduleX"),

    /**
     * 数据源中心
     */
    DATACENTER(11, "DataCenter"),

    /**
     * 第三方
     */
    Others(99, "Others"),
    ;

    private int type;

    private String name;

    SdkAppType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public static SdkAppType getValue(int value) {
        SdkAppType[] values = SdkAppType.values();
        for (SdkAppType appType : values) {
            if (appType.getType() == value) {
                return appType;
            }
        }
        return null;
    }
}
