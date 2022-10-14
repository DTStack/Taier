package com.dtstack.taier.datasource.api.proxy;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.manager.ManagerFactory;

import java.lang.reflect.Proxy;

/**
 * client 代理工厂类, 提供获取 client 代理的功能
 *
 * @author ：wangchuan
 * date：Created in 11:35 2022/9/23
 * company: www.dtstack.com
 */
public class ClientExecuteProxyFactory {

    /**
     * 动态代理获取代理对象
     *
     * @param originClient   原始 client
     * @param clientType     代理类 class 类型
     * @param config         配置
     * @param managerFactory manager factory
     * @param <T>            返回值范型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxyClient(T originClient,
                                       Class<T> clientType,
                                       Config config,
                                       ManagerFactory managerFactory) {
        // 创建 client 代理对象
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{clientType},
                new ClientProxyInvocationHandle<>(originClient, config, managerFactory));
    }
}
