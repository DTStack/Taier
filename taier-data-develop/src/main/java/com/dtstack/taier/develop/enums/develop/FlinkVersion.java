package com.dtstack.taier.develop.enums.develop;

import org.apache.commons.lang3.StringUtils;

/**
 * 组建版本
 *
 * @author ：wangchuan
 * date：Created in 上午10:49 2021/7/27
 * company: www.dtstack.com
 */
public enum FlinkVersion {

    FLINK_180("1.8"),

    FLINK_110("1.10"),

    FLINK_112("1.12");

    private final String type;

    public final String getType() {
        return type;
    }

    FlinkVersion(String type) {
        this.type = type;
    }

    /**
     * 获取 flink 版本枚举
     *
     * @param version 版本 string
     * @return flink 枚举
     */
    public static FlinkVersion getVersion(String version) {
        if (StringUtils.isNotBlank(version)) {
            for (FlinkVersion componentVersion : FlinkVersion.values()) {
                if (StringUtils.equalsIgnoreCase(version, componentVersion.getType())) {
                    return componentVersion;
                }
            }
        }
        return FLINK_110;
    }
}
