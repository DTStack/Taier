package com.dtstack.engine.remote.enums;

import akka.routing.*;

/**
 * @Auther: dazhi
 * @Date: 2020/9/1 3:45 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 *      路由类型: 默认提供4种 RoundRobin：轮询（默认），Random:随机，SmallestMailbox：空闲，Broadcast：广播
 *          使用系统的路由器：akka.cluster.router: RoundRobin（类型）
 *      当然系统支持自定义路由器实现
 *          实现步骤： 1. 实现接口 RoutingLogic
 *                   2. 在配置文件中配置 akka.cluster.router.customize=实现类的完整类名
 *      如果同时配置自定义路由器和系统路由器时，默认使用自定义路由器
 */
public enum RouterTypeEnum {

    /**
     * 轮询
     */
    ROUND_ROBIN_ROUTING("RoundRobin",new RoundRobinRoutingLogic()),
    /**
     * 随机
     */
    RANDOM_ROUTING("Random",new RandomRoutingLogic()),
    /**
     * 空闲
     */
    SMALLEST_MAILBOX_ROUTING("SmallestMailbox",new SmallestMailboxRoutingLogic()),
    /**
     * 广播
     */
    BROADCAST_ROUTING("Broadcast",new BroadcastRoutingLogic());

    private final String type;
    private final RoutingLogic routingLogic;

    RouterTypeEnum(String type, RoutingLogic routingLogic) {
        this.type = type;
        this.routingLogic = routingLogic;
    }

    public String getType() {
        return type;
    }

    public RoutingLogic getRoutingLogic() {
        return routingLogic;
    }

    public static RoutingLogic getRoutingLogicByType(String type){
        RouterTypeEnum[] values = RouterTypeEnum.values();

        for (RouterTypeEnum value : values) {
            if (value.type.equals(type)) {
                return value.routingLogic;
            }
        }
        return null;
    }


}
