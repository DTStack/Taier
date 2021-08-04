package com.dtstack.engine.remote.akka.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.dtstack.engine.remote.akka.actor.ObjectActor;
import com.dtstack.engine.remote.config.ServerConfig;
import com.dtstack.engine.remote.constant.ServerConstant;
import com.dtstack.engine.remote.service.ClientService;
import com.dtstack.engine.remote.akka.AkkaClientServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/26
 */
public class AkkaServerConfig implements ApplicationContextAware, EnvironmentAware, ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(AkkaServerConfig.class);

    private ApplicationContext applicationContext;

    private Environment environment;

    @Override
    public void init(){
        if (!AkkaConfig.hasLoad()) {
            AkkaConfig.init(environment,applicationContext);
        }
    }

    @Bean(name="clientService",destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public ClientService clientService(){
        AkkaClientServiceImpl clientService = new AkkaClientServiceImpl();
        ActorSystem system = ActorSystem.create(ServerConstant.SERVER_PATH, AkkaConfig.getConfig());
        ActorRef ebIndexActor = system.actorOf(Props.create(ObjectActor.class).withDispatcher(ServerConstant.BLOCKING_DISPATCHER), ServerConstant.BASE_PATH);
        clientService.setActorRef(ebIndexActor);
        return clientService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
