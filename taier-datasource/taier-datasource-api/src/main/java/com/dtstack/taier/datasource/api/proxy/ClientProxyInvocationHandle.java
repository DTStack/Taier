package com.dtstack.taier.datasource.api.proxy;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.constant.ConfigConstants;
import com.dtstack.taier.datasource.api.downloader.IDownloader;
import com.dtstack.taier.datasource.api.manager.ManagerFactory;
import com.dtstack.taier.datasource.api.manager.list.ProxyThreadPoolManager;
import com.dtstack.taier.datasource.api.utils.ClassloaderUtils;
import com.dtstack.taier.datasource.api.utils.RetryUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理 client, 提供 client 一些通用处理的能力, 如重试、方法执行超时等
 *
 * @author ：wangchuan
 * date：Created in 13:46 2022/9/23
 * company: www.dtstack.com
 */
public class ClientProxyInvocationHandle<T> implements InvocationHandler {

    private final T client;

    private final Config config;

    private final ManagerFactory managerFactory;

    public ClientProxyInvocationHandle(T client, Config config, ManagerFactory managerFactory) {
        this.client = client;
        this.config = config;
        this.managerFactory = managerFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1. 先增加重试、超时, 后期扩展
        return invokeMethod(method, args);
    }

    protected Object invokeMethod(Method method, Object[] args) throws Throwable {
        try {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            // 重试次数
            Integer retryTimes = config.getConfig(ConfigConstants.RETRY_TIMES, Integer.class, 1);
            Long retryIntervalTime = config.getConfig(ConfigConstants.RETRY_INTERVAL_TIME, Long.class, 1000L);
            Long executeTimeout;
            // sql 执行超时时间设置为 5 小时
            if (method.getName().contains("executeBatchQuery")) {
                executeTimeout = config.getConfig(ConfigConstants.SQL_EXECUTE_TIMEOUT, Long.class, 5 * 60 * 60 * 1000L);
            } else {
                executeTimeout = config.getConfig(ConfigConstants.EXECUTE_TIMEOUT, Long.class, 5 * 60 * 1000L);
            }

            Object result = RetryUtils.asyncExecuteWithRetry(() -> method.invoke(client, args),
                    retryTimes,
                    retryIntervalTime,
                    false,
                    executeTimeout,
                    managerFactory.getManager(ProxyThreadPoolManager.class).getThreadPoolExecutor(client.getClass().getClassLoader()));

            // 如果返回值是 IDownloader 的子类, 则需要返回该子类对象的代理类, 在调用内部方法时设置线程上下文类加载器为加载插件的 ChildFirstClassLoader
            if (result instanceof IDownloader) {
                ClassLoader oldClassLoader = result.getClass().getClassLoader();
                return Proxy.newProxyInstance(
                        Thread.currentThread().getContextClassLoader(),
                        new Class<?>[]{IDownloader.class,},
                        (p, m, a) ->
                                ClassloaderUtils.executeAndReset(
                                        () -> m.invoke(result, a), oldClassLoader));
            }
            return result;
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
