package com.dtstack.engine.common;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/1/25
 */

@FunctionalInterface
public interface Callback<E> {

    /**
     * 函数式接口
     * @param event 入参
     * @return
     */
    Object submit(E event);
}
