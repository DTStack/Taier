package com.dtstack.engine.remote.config;

import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.akka.config.AkkaServerConfig;
import com.dtstack.engine.remote.annotation.RemoteClient;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.Set;

/**
 * @Auther: dazhi
 * @Date: 2020/9/4 10:41 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RemoteConditional implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        // 加载一下配置文件防止配置文件没有被加载
        conditionContext.getBeanFactory().getBean(AkkaServerConfig.class);
        Set<String> localRoles = AkkaConfig.getLocalRoles();

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotatedTypeMetadata.getAnnotationAttributes(RemoteClient.class.getName(), false));
        if (annotationAttributes != null) {
            String role = annotationAttributes.getString("value");
            if (localRoles.contains(role)) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }
}
