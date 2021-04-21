package com.dtstack.engine.master.tree;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/4/8 1:44 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class Tree<K,V> {

    private K node;

    private Map<K,V> childNode;

    public K getNode() {
        return node;
    }

    public void setNode(K node) {
        this.node = node;
    }

    public Map<K, V> getChildNode() {
        return childNode;
    }

    public void setChildNode(Map<K, V> childNode) {
        this.childNode = childNode;
    }
}
