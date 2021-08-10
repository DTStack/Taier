package com.dtstack.engine.remote.config;

import com.dtstack.engine.remote.node.strategy.NodeInfoStrategy;
import com.dtstack.engine.remote.route.RouteStrategy;

/**
 * @Auther: dazhi
 * @Date: 2021/8/4 4:41 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface NodeStrategyServerConfig extends ServerConfig {

    /**
     * 设置节点配置策略
     */
    void setNodeInfoStrategy(NodeInfoStrategy nodeInfoStrategy);

    /**
     * 设置节点负载均衡策略
     */
    void setRouteStrategy(RouteStrategy routeStrategy);

}
