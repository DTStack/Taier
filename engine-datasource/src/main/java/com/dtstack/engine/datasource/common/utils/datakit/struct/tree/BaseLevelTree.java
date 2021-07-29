package com.dtstack.engine.datasource.common.utils.datakit.struct.tree;

/**
 * SLOGAN:让未来变成现在
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a>
 * 2018-04-17 22:08.
 */
public abstract class BaseLevelTree<K, T extends BaseLevelTree> extends BaseTree<K, T> {
    private String level;

    /**
     * 获取目录层级,如1、1_1、1_1_1、1_1_2
     *
     * @return 目录层级
     */
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
