package com.dtstack.taier.datasource.api.utils;

import java.util.concurrent.Callable;

/**
 * classloader utils
 *
 * @author ：wangchuan
 * date：Created in 13:52 2022/9/23
 * company: www.dtstack.com
 */
public class ClassloaderUtils {

    /**
     * 执行方法前设置线程上下文 classloader 为指定的 classloader, 并在执行后设置回原始 classloader
     *
     * @param callable 执行逻辑
     * @param ec       指定的 classloader
     * @param <R>      执行结果范性
     * @return 执行结果
     */
    public static <R> R executeAndReset(Callable<R> callable, ClassLoader ec) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ec);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}
