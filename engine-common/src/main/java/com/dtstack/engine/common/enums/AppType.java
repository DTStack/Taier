package com.dtstack.engine.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/2/9
 */
public enum AppType {

    RDOS(1,"离线开发"),
    DQ(2,"数据质量"),
    API(3,"数据api"),
    TAG(4,"标签引擎"),
    MAP(5,"数据地图"),
    CONSOLE(6,"控制台"),
    STREAM(7,"实时开发"),
    DATASCIENCE(8,"数据科学"),
    DATAASSETS(9,"数据资产"),
    INDEX(10,"指标"),
    DAGSCHEDULEX(99,"调度");

    private int type;

    private String name;

    AppType(int type,String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
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
