package com.dtstack.taier.develop.enums.develop;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 组建版本
 *
 * @author ：wangchuan
 * date：Created in 上午10:49 2021/7/27
 * company: www.dtstack.com
 */
public enum FlinkVersion {

    FLINK_112("1.12", Arrays.asList("1.12-on-yarn", "1.12-standalone"));

    private final String type;

    private final List<String> versions;

    public  String getType() {
        return type;
    }

    public List<String> getVersions() {
        return versions;
    }

    FlinkVersion(String type, List<String> versions) {
        this.type = type;
        this.versions = versions;
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
                if (componentVersion.getVersions().contains(version)) {
                    return componentVersion;
                }
            }
        }
        return FLINK_112;
    }
}
