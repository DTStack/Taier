package com.dtstack.taier.common.enums;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @description:
 * @create: 2021-12-16 00:15
 **/
public enum CatalogueLevel {
    /**
     * 目录层级顶级
     */
    ONE(0),
    /**
     * 目录层级二级
     */
    SECOND(1),
    /**
     * 目录层级其他（多级）
     */
    OTHER(2);

    private int level;

    CatalogueLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
