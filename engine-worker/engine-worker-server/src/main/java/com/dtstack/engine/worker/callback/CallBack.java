package com.dtstack.engine.worker.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
@FunctionalInterface
public interface CallBack<T> {

    T execute() throws Exception;

}
