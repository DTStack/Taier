package com.dtstack.batch.common.enums;

import com.dtstack.dtcenter.common.enums.MultiEngineType;

/**
 * @author yuebai
 * @date 2019-06-04
 */
public enum EngineCatalogueType {
    /**
     *
     */
    SPARK(-1, "SparkSQL"),

    /**
     *
     */
    LIBRA(-2, "LibraSQL"),

    /**
     *
     */
    TIDB(-3, "TiDB SQL"),

    /**
     *
     */
    ORACLE(-4, "Oracle SQL"),

    /**
     *
     */
    GREENPLUM(-5,"Greenplum SQL");

    private int type;

    private String desc;

    EngineCatalogueType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return this.type;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据名称获取 EngineCatalogueType
     * @param nodeName
     * @return
     */
    public static EngineCatalogueType getByeName (String nodeName) {
        for (EngineCatalogueType engineCatalogueType : EngineCatalogueType.values()) {
            if (engineCatalogueType.getDesc().equals(nodeName)) {
                return engineCatalogueType;
            }
        }

        return EngineCatalogueType.SPARK;
    }

    /**
     * 根据引擎类型获取目录结构
     * @param engineType
     * @return
     */
    public static EngineCatalogueType getByEngineType(Integer engineType) {
        if (MultiEngineType.HADOOP.getType() == engineType) {
            return EngineCatalogueType.SPARK;
        }

        if (MultiEngineType.LIBRA.getType() == engineType) {
            return EngineCatalogueType.LIBRA;
        }

        if (MultiEngineType.TIDB.getType() == engineType) {
            return EngineCatalogueType.TIDB;
        }

        if (MultiEngineType.ORACLE.getType() == engineType) {
            return EngineCatalogueType.ORACLE;
        }

        if (MultiEngineType.GREENPLUM.getType() == engineType) {
            return EngineCatalogueType.GREENPLUM;
        }
        return EngineCatalogueType.SPARK;
    }
}
