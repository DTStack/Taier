package com.dtstack.engine.remote.route;

import com.dtstack.engine.remote.exception.NoNodeException;
import com.dtstack.engine.remote.node.AbstractNode;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Auther: dazhi
 * @Date: 2021/8/10 5:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class BalancedRouteStrategy implements RouteStrategy {

    private final static AtomicInteger countStrategy = new AtomicInteger(0);

    @Override
    public AbstractNode route(List<AbstractNode> nodes) {
        if (nodes.size()>0) {
            if (nodes.size() == 1) {
                AbstractNode abstractNode = nodes.get(0);
                if (AbstractNode.NodeStatus.USABLE.equals(abstractNode.getStatus())) {
                    return abstractNode;
                }

                throw new NoNodeException("no find usable node");
            }

            int size = nodes.size();
            int count = size;
            while (true) {
                int andIncrement = countStrategy.getAndIncrement();
                count--;
                AbstractNode abstractNode = nodes.get((andIncrement % size));
                if (AbstractNode.NodeStatus.USABLE.equals(abstractNode.getStatus())) {
                    return abstractNode;
                }

                if (count <= 0) {
                    throw new NoNodeException("no find usable node");
                }
            }
        }
        throw new NoNodeException("no find usable node");
    }

}
