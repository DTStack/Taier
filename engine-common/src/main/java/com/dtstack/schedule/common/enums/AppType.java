package com.dtstack.schedule.common.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/2/9
 */
public enum AppType {

    RDOS(1), DQ(2), API(3), TAG(4), MAP(5), CONSOLE(6), STREAM(7), DATASCIENCE(8),
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
