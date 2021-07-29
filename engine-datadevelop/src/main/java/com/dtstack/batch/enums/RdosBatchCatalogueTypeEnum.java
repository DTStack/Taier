package com.dtstack.batch.enums;

/**
 * rdos_hive_catalogue 表 type分类
 */
public enum RdosBatchCatalogueTypeEnum {

    /**
     * normal
     */
    NORAML(0),

    /**
     * project
     */
    PROJECT(1);

    private Integer type;

    RdosBatchCatalogueTypeEnum(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
