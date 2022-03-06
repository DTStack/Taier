package com.dtstack.taier.develop.enums.develop;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/15
 */
public enum FileType {
    /**
     * hive orc文件格式
     */
    ORCFILE("orc"),

    /**
     * hive text文件格式
     */
    TEXTFILE("text"),

    /**
     * parquet 文件格式
     */
    PARQUET("parquet");

    private String val;

    FileType(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
