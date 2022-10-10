package com.dtstack.taier.datasource.api.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * TSDB 时间粒度
 *
 * @author ：wangchuan
 * date：Created in 上午10:25 2021/6/23
 * company: www.dtstack.com
 */
public enum Granularity {
    S1("1s"), S5("5s"), S15("15s"), M1("1m"), M5("5m"), M15("15m"), M60("60m"), H1("1h"), H2("2h"), H6("6h"), H24("24h");

    private static final Map<String, Granularity> CODE_MAP = new HashMap<String, Granularity>();

    static {
        for (Granularity typeEnum : Granularity.values()) {
            CODE_MAP.put(typeEnum.getName(), typeEnum);
        }
    }

    public static Granularity getEnum(String name) {
        return CODE_MAP.get(name);
    }

    private final String name;

    Granularity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
