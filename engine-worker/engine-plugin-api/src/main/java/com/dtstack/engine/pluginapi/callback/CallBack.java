package com.dtstack.engine.pluginapi.callback;

/**
 * Created by sishu.yss on 2017/8/28.
 */
@FunctionalInterface
public interface CallBack<T> {

    T execute() throws Exception;

}
