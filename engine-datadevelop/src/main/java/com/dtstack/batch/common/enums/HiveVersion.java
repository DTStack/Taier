package com.dtstack.batch.common.enums;

/**
 * hive版本枚举类
 *
 * @author ：wangchuan
 * date：Created in 下午3:58 2020/11/9
 * company: www.dtstack.com
 */
public enum HiveVersion {
    /**
     * hive1
     */
    HIVE_1x("1.x"),
    /**
     * hive2
     */
    HIVE_2x("2.x"),
    /**
     * hive3
     */
    HIVE_3x("3.x");

    private String version;

    public String getVersion() {
        return version;
    }

    HiveVersion(String version) {
        this.version = version;
    }

    public static HiveVersion getByVersion(String versionStr) {
        for (HiveVersion version : values()) {
            if (version.getVersion().equalsIgnoreCase(versionStr)) {
                return version;
            }
        }
        return null;
    }
}