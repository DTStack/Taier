package com.dtstack.engine.remote.annotation;

import com.dtstack.engine.remote.config.RemoteClientRegistrar;
import com.dtstack.engine.remote.node.strategy.ConfigurationNodeInfoStrategy;
import com.dtstack.engine.remote.node.strategy.NodeInfoStrategy;
import com.dtstack.engine.remote.route.BalancedRouteStrategy;
import com.dtstack.engine.remote.route.RouteStrategy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 2:58 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RemoteClientRegistrar.class})
public @interface EnableRemoteClient {

    /**
     * 设置扫描包路径
     *
     * @return 包路径
     */
    String basePackage() default "";

    /**
     * 设置配置文件
     *
     * @return
     */
    String properties() default "";

    /**
     * 设置transport框架
     *
     */
    String transport() default "netty";

    /**
     * 唯一标识
     * @return
     */
    String identifier();

    /**
     * 设置 节点加载策略
     */
    Class<? extends NodeInfoStrategy> nodeInfoStrategy() default ConfigurationNodeInfoStrategy.class;

    /**
     * 设置 路由策略
     */
    Class<? extends RouteStrategy> routeStrategy() default BalancedRouteStrategy.class;

}
