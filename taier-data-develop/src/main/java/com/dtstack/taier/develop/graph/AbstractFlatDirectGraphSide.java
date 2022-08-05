package com.dtstack.taier.develop.graph;

/**
 * @author leon
 * @date 2022-08-01 19:56
 **/
public abstract class AbstractFlatDirectGraphSide<V, I> implements FlatDirectGraphSide<V, I> {

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
