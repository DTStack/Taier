package com.dtstack.taier.datasource.plugin.common.function;

/**
 * 单入参函数
 *
 * @author ：wangchuan
 * date：Created in 下午5:42 2022/3/16
 * company: www.dtstack.com
 */
@FunctionalInterface
public interface SingleParamFunc<T, M> {

    /**
     * 方法执行
     *
     * @param param 方法执行入参
     * @return 执行结果
     * @throws Exception 异常信息
     */
    T execute(M param) throws Exception;
}
