package com.dtstack.taier.develop.graph;


import java.util.List;
import java.util.function.Function;

/**
 * Loop judge for Flat Directed Graph {@link FlatDirectGraphSide}
 *
 * @author leon
 * @date 2022-08-01 18:55
 **/
public interface FlatDirectedGraphLoopJudge<V, I, S extends FlatDirectGraphSide<V, I>> {

    List<S> getSidesForJudge();

    boolean isLoop(Function<List<V>, List<S>> parentSideProvider, Function<List<V>, List<S>> childSideProvider);

}
