package com.dtstack.engine.remote.node;

import com.dtstack.engine.remote.factory.RemoteThreadFactory;
import com.dtstack.engine.remote.netty.util.CallerThreadExecutePolicy;
import com.dtstack.engine.remote.route.RouteStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 5:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class RemoteNodes {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteNodes.class);
    /**
     * 节点信息
     */
    private final String identifier;

    /**
     * 路由策越
     */
    private RouteStrategy routeStrategy;

    /**
     * callback thread executor
     */
    private final ExecutorService callbackExecutor;

    /**
     *
     */
    private final ScheduledExecutorService responseFutureExecutor;

    /**
     * key : address
     * value : node
     */
    private final Map<String , AbstractNode> refs = new ConcurrentHashMap<>(128);


    private final Map<String , AtomicInteger> heartBeatCount = new ConcurrentHashMap<>(128);



    public RemoteNodes(String identifier) {
        this.identifier = identifier;
        callbackExecutor = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(1000), new RemoteThreadFactory(identifier+"-"+this.getClass().getSimpleName()),
                new CallerThreadExecutePolicy());
        responseFutureExecutor = Executors.newSingleThreadScheduledExecutor(new RemoteThreadFactory(identifier+"_responseFutureExecutor"+this.getClass().getSimpleName()));
    }

    public Map<String, AbstractNode> getRefs() {
        return refs;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setRouteStrategy(RouteStrategy routeStrategy) {
        this.routeStrategy = routeStrategy;
    }

    public void close() {
        for (Map.Entry<String, AbstractNode> entry : refs.entrySet()) {
            AbstractNode value = entry.getValue();
            value.close();
        }
    }

    public void close(String address) {
        AbstractNode abstractNode = refs.get(address);
        if (abstractNode != null) {
            abstractNode.close();
        }
    }

    public AbstractNode route() {
        return routeStrategy.route(new ArrayList<>(refs.values()));
    }


    public void start() {
        responseFutureExecutor.scheduleWithFixedDelay(()->{
            for (Map.Entry<String, AbstractNode> entry : refs.entrySet()) {
                String key = entry.getKey();
                AbstractNode node = entry.getValue();
                if (node.getStatus().equals(AbstractNode.NodeStatus.UNAVAILABLE)) {
                    // 结点已经是不可用的，尝试重新连接
                    if (node.connect()) {
                        // 连接成功
                        LOGGER.info("{} connect success",key);
                    } else {
                        // 连接失败
                        LOGGER.warn("{} connect fail",key);
                    }
                    return;
                }

                // 检查心跳
                if (node.heartbeat()){
                    heartBeatCount.remove(node.getAddress());
                } else {
                    AtomicInteger count = heartBeatCount.get(node.getAddress());
                    if (count == null) {
                        count = new AtomicInteger(0);
                        heartBeatCount.put(node.getAddress(),count);
                    } else {
                        int i = count.addAndGet(1);
                        if (i > 3) {
                            node.setStatus(AbstractNode.NodeStatus.UNAVAILABLE);
                        }
                    }

                }
            }
        },10L,2,TimeUnit.SECONDS);
    }


}
