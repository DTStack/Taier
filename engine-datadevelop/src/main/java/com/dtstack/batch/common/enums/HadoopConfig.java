package com.dtstack.batch.common.enums;

/**
 *
 */
public enum HadoopConfig {

    /**
     * HDFS 配置
     */
    HDFS_DEFAULTFS("defaultFS"),

    /**
     * HDFS 正则
     */
    DEFAULT_FS_REGEX("hdfs://.*"),

    /**
     * 高可用配置
     */
    HADOOP_CONFIG("hadoopConfig");

    private String val;

    public String getVal() {
        return val;
    }

    HadoopConfig(String val) {
        this.val = val;
    }

}
