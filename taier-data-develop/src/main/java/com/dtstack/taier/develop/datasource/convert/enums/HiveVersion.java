package com.dtstack.taier.develop.datasource.convert.enums;

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
    HIVE_3x("3.x"),

    /**
     * hive3.x-apache
     */
    HIVE_3x_APACHE("3.x-apache"),

    /**
     * hive3.x-cdp
     */
    HIVE_3x_CDP("3.x-cdp"),

    /**
     * hive2.x-tbds
     */
    HIVE_2x_TBDS("2.x-tbds"),
    ;

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