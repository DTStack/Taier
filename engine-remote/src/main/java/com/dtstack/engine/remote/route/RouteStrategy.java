package com.dtstack.engine.remote.route;

import com.dtstack.engine.remote.node.AbstractNode;

import java.util.Collection;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/8/10 5:39 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface RouteStrategy {

    /**
     * 路由策略
     *
     * @param nodes 执行的节点
     * @return
     */
    AbstractNode route(List<AbstractNode> nodes);




}
