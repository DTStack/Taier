package com.dtstack.taier.develop.graph;

/**
 * An edge of a Flat directed graph
 *
 * <p>
 * flat means that parent {@link FlatDirectGraphSide#parent()} represent the
 * value of the parent node instead of a reference
 * <p>
 *
 * @author leon
 * @date 2022-08-01 19:56
 **/
public interface FlatDirectGraphSide<V, I> extends GraphNode<V>, IdProvider<I> {

    V parent();
}
