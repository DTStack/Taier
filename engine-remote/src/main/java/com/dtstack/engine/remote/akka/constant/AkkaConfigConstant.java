package com.dtstack.engine.remote.akka.constant;

/**
 * @Auther: dazhi
 * @Date: 2020/8/28 11:01 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaConfigConstant {

    /**
     * akka配置文件路径
     */
    public static final String CONFIG_PATH = "akka.config.path";

    /**
     * roleName
     */
    public static final String ROLE_NAME = "akka.cluster.roles";

    /**
     * 路由类型
     */
    public static final String CLUSTER_ROUTER="akka.cluster.router";

    /**
     * 自定义路由器
     */
    public static final String CLUSTER_ROUTER_CUSTOMIZE="akka.cluster.router.customize";

    /**
     * akka超时时间
     */
    public static final String AKKA_ASK_TIMEOUT = "akka.ask.timeout";

    /**
     * worker线程提交超时时间
     */
    public static final String WORKER_TIMEOUT = "worker.client.timeout";

    /**
     * akka的处理actor数量
     */
    public static final String AKKA_ACTOR_HANDLER_NUMBER = "akka.actor.handler.number";

    /**
     * akka等到的超时时间
     */
    public static final String AKKA_ASK_RESULTTIMEOUT = "akka.ask.resultTimeout";

    /**
     * akka 的identifiers列表
     */
    public static final String REMOTE_CLUSTER_WORKER_IDENTIFIERS = "remote.cluster.worker.identifiers";

    /**
     * akka的节点列表
     */
    public static final String REMOTE_CLUSTER_WORKER_NODES = "remote.cluster.worker.%s.seed-nodes";

}
