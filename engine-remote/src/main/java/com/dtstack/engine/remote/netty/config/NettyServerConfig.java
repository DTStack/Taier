package com.dtstack.engine.remote.netty.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dtstack.engine.remote.akka.AkkaClientServiceImpl;
import com.dtstack.engine.remote.akka.actor.ObjectActor;
import com.dtstack.engine.remote.akka.config.AkkaConfig;
import com.dtstack.engine.remote.config.NodeStrategyServerConfig;
import com.dtstack.engine.remote.config.ServerConfig;
import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.netty.NettyClientServiceImpl;
import com.dtstack.engine.remote.netty.NettyRemoteClient;
import com.dtstack.engine.remote.node.strategy.NodeInfoStrategy;
import com.dtstack.engine.remote.service.ClientService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @Auther: dazhi
 * @Date: 2021/8/3 3:43 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class NettyServerConfig implements NodeStrategyServerConfig, ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    private NodeInfoStrategy nodeInfoStrategy;

    @Override
    public void init() {
        if (!NettyConfig.hasLoad()) {
            NettyConfig.init(environment,applicationContext);
        }
    }

    @Bean(name = "clientService", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public ClientService clientService() {
        NettyClientServiceImpl nettyClientService = new NettyClientServiceImpl();
        NettyRemoteClient nettyRemoteClient = new NettyRemoteClient(nodeInfoStrategy);
        nettyClientService.setClient(nettyRemoteClient);
        return nettyClientService;
    }



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    @Autowired
    public void setNodeInfoStrategy(NodeInfoStrategy nodeInfoStrategy) {
        this.nodeInfoStrategy = nodeInfoStrategy;
    }
}
