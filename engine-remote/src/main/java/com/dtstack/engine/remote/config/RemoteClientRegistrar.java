package com.dtstack.engine.remote.config;

import com.dtstack.engine.remote.akka.config.ServerConfig;
import com.dtstack.engine.remote.annotation.EnableRemoteClient;
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

        if (!registry.containsBeanDefinition("serverConfig")) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(ServerConfig.class);
            beanDefinition.setInitMethodName("init");
            beanDefinition.setScope(ConfigurableBeanFactory.SCOPE_SINGLETON);
            beanDefinition.setAutowireCandidate(Boolean.TRUE);
            registry.registerBeanDefinition("serverConfig", beanDefinition);
        }

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableRemoteClient.class.getName(), false));
        RemoteClientScanner scanner = new RemoteClientScanner(registry);
        if (annotationAttributes != null) {
            String basePackage = annotationAttributes.getString("basePackage");
            if (StringUtils.isBlank(basePackage)) {
                // 扫描默认路径

            } else {
                scanner.doScan(basePackage);
            }
        }
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
