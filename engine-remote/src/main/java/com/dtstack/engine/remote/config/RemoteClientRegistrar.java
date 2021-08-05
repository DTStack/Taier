package com.dtstack.engine.remote.config;

import com.dtstack.engine.remote.akka.config.AkkaServerConfig;
import com.dtstack.engine.remote.annotation.EnableRemoteClient;
import com.dtstack.engine.remote.enums.Transport;
import com.dtstack.engine.remote.exception.RemoteException;
import com.dtstack.engine.remote.node.strategy.NodeInfoStrategy;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 3:00 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RemoteClientRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        if ("false".equalsIgnoreCase(environment.getProperty("remote.annotation.switch"))){
            return;
        }

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableRemoteClient.class.getName(), false));

        if (annotationAttributes == null) {
            throw new RemoteException("remote exception");
        }

        // 设置配置文件 默认加载 usr.dir 位置下的
        String properties = annotationAttributes.getString(EnableRemoteClientConst.PROPERTIES);
        if (StringUtils.isNotBlank(properties)) {
            System.setProperty("remote.properties.file.name",properties);
        }

        // 加载配置类
        loadConfiguration(registry, annotationAttributes);

        RemoteClientScanner scanner = new RemoteClientScanner(registry);
        String basePackage = annotationAttributes.getString(EnableRemoteClientConst.BASE_PACKAGE);
        if (StringUtils.isBlank(basePackage)) {
            // 扫描默认路径

        } else {
            scanner.doScan(basePackage);
        }
    }

    private void loadConfiguration(BeanDefinitionRegistry registry, AnnotationAttributes annotationAttributes) {
        // 加载 serverConfig
        String transport = annotationAttributes.getString(EnableRemoteClientConst.TRANSPORT);
        Class<?> aClass = loadClass(transport);
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(aClass);
        beanDefinition.setInitMethodName("init");
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        beanDefinition.setAutowireCandidate(Boolean.TRUE);
        registry.registerBeanDefinition("serverConfig", beanDefinition);

        // 加载 NodeInfoStrategy
        Class<?> aClass1 = annotationAttributes.getClass(EnableRemoteClientConst.NODE_INFO_STRATEGY);
        beanDefinition.setBeanClass(aClass1);
        beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
        beanDefinition.setAutowireCandidate(Boolean.TRUE);
        registry.registerBeanDefinition("nodeInfoStrategy", beanDefinition);
    }

    private Class<? extends ServerConfig> loadClass(String transport) {
        Class<? extends ServerConfig> aClass = Transport.getClass(transport);
        if (aClass ==null) {
            try {
                return (Class<? extends ServerConfig>)Class.forName(transport);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RemoteException(e);
            }
        }
        return aClass;
    }

    static class EnableRemoteClientConst {
        public static final String  TRANSPORT = "transport";
        public static final String  NODE_INFO_STRATEGY = "nodeInfoStrategy";
        public static final String  BASE_PACKAGE = "basePackage";
        public static final String  PROPERTIES = "properties";


    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
