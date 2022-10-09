package com.dtstack.taier.datasource.plugin.common.base;

/**
 * 方法回调
 *
 * @author ：wangchuan
 * date：Created in 11:59 上午 2021/9/1
 * company: www.dtstack.com
 */
public interface CallBack<T, M> {

    /**
     * 方法执行
     *
     * @param event 入参数
     * @return 回参
     */
    T execute(M event);
}
