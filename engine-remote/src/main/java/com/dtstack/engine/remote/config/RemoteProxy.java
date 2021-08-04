package com.dtstack.engine.remote.config;

import com.dtstack.engine.remote.annotation.RemoteClient;
import com.dtstack.engine.remote.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;

/**
 * @Auther: dazhi
 * @Date: 2021/7/30 2:46 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class RemoteProxy<T> implements FactoryBean<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteProxy.class);
    protected final Class<T> interfaceClass;
    protected final RemoteClient annotation;
    protected Class<?> targetClass;
    protected Object fallbackObject;

    protected RemoteProxy(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.annotation = interfaceClass.getAnnotation(RemoteClient.class);

        Class<?> fallback = this.annotation.fallback();

        if (interfaceClass.isAssignableFrom(fallback)) {
            try {
                targetClass = fallback;
                fallbackObject = fallback.newInstance();
            } catch (Exception e) {
                LOGGER.error("fallback is error!");
                targetClass = null;
                fallbackObject = null;
            }
        }
    }


    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                (proxy, method, args) -> proxyRun(proxy,method,args));
    }

    /**
     * 发送远程的代理逻辑
     *
     * @param proxy 代理类
     * @param method 代理方法
     * @param args 方法的参数
     * @return 结果集
     */
    protected abstract Object proxyRun(Object proxy, Method method, Object[] args) throws Throwable;

}
