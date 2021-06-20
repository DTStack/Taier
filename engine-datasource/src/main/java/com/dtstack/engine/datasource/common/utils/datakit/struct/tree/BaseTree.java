package com.dtstack.engine.datasource.common.utils.datakit.struct.tree;

import java.util.List;

/**
 * SLOGAN:让未来变成现在
 *
 * @author <a href="mailto:maoba@dtstack.com">猫爸</a>
 * 2018-04-17 20:36.
 */
public

abstract class BaseTree<K, T extends BaseTree> {

    private List<T> children;

    /**
     * 获取节点的id
     *
     * @return 本节点的id
     */
    public abstract K getId();

    /**
     * 获取节点的所属父id,若无父id则返回0
     *
     * @return 父节点的id
     */
    public abstract K getParentId();

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
