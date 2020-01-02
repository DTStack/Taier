package com.dtstack.engine.common.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
@FunctionalInterface
public interface ClassLoaderCallBack<T> {

    T execute() throws Exception;

}
