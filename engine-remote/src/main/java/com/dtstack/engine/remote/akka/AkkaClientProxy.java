package com.dtstack.engine.remote.akka;

import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.annotation.RemoteClient;
import com.dtstack.engine.remote.config.FallbackContext;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.message.Message;
import com.dtstack.engine.remote.message.TargetInfo;
import com.dtstack.engine.remote.service.ClientService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 1:54 下午
 * @Email:dazhi@dtstack.com
 * @Description: 对扫描到的接口生成代理对象。
 */
public class AkkaClientProxy<T> implements FactoryBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(AkkaClientProxy.class);
    //被代理的接口Class对象
    private final Class<T> interfaceClass;
    private final RemoteClient annotation;
    private Class<?> targetClass;
    private Object fallbackObject;
    private ApplicationContext applicationContext;


    public AkkaClientProxy(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
        this.annotation = interfaceClass.getAnnotation(RemoteClient.class);
        Class<?> fallback = this.annotation.fallback();

        if (interfaceClass.isAssignableFrom(fallback)) {
            try {
                targetClass = fallback;
                fallbackObject = fallback.newInstance();
            } catch (Exception e) {
                logger.error("fallback is error!");
                targetClass = null;
                fallbackObject = null;
            }
        }
    }

    @Override
    public T getObject() throws Exception {
        //通过JDK动态代理创建代理类
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class[]{interfaceClass},
                (proxy, method, args) -> {
                    Method objectName;
                    try {
                        if (checkMethod(method)) {
                            // Object 的方法执行Object
                            logger.info("Object method,no handler");
                            objectName = getMethod(method);
                            return objectName.invoke(interfaceClass, args);
                        }

                        logger.info("create Proxy start!!");
                        //实现业务逻辑,比如发起网络连接，执行远程调用，获取到结果，并返回
                        ClientService service = applicationContext.getBean(ClientService.class);

                        if (service == null) {
                            throw new RemoteException("service does not exist， remote error......");
                        }

                        if (this.annotation == null || StringUtils.isBlank(this.annotation.value())) {
                            throw new RemoteException("not get remoteClient info ，unable to send remote service");
                        }

                        if (AkkaConfig.getLocalRoles().contains(annotation.value())) {
                            throw new RemoteException("unable to proxy local calls");
                        }

                        Message message = buildMessage(method, args);
                        Message result = service.sendMassage(message);
                        return result.result(method.getReturnType());
                    } catch (Exception e) {
                        logger.error("an error occurred:e:{}, perform fusing",e.toString());
                        if (targetClass == null || fallbackObject == null) {
                            throw e;
                        } else {
                            try {
                                FallbackContext.set(e);
                                objectName = targetClass.getMethod(method.getName(), method.getParameterTypes());
                                return objectName.invoke(fallbackObject, args);
                            } finally {
                                FallbackContext.remove();
                            }
                        }
                    }
                });
    }

    private Method getMethod(Method method) throws NoSuchMethodException {
        Method objectName;
        if (targetClass == null) {
            objectName = Object.class.getMethod(method.getName(), method.getParameterTypes());
        } else {
            objectName = targetClass.getMethod(method.getName(), method.getParameterTypes());
        }
        return objectName;
    }

    private boolean checkMethod(Method method) {
        Method[] methods = Object.class.getMethods();
        for (Method objectMethod : methods) {
            if (objectMethod.equals(method)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private Message buildMessage(Method method, Object[] args) {
        TargetInfo targetInfo = new TargetInfo();
        targetInfo.setClazz(interfaceClass.getName());
        targetInfo.setMethod(method.getName());
        Message message = new Message();
        message.setTargetInfo(targetInfo);
        message.setRoles(annotation.value());
        message.setTransport(args);
        return message;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
