package com.dtstack.engine.common.akka;

/**
 * @Auther: jiangjunjie
 * @Date: 2020/3/3
 * @Description:
 */
public interface RpcService<T> {

    T loadConfig();

    /**
     * start master
     */
    void start(T t);
}
