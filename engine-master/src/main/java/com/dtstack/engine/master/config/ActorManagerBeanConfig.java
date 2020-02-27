package com.dtstack.engine.master.config;

import com.dtstack.engine.master.akka.ActorManager;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/26
 */
@Configuration
public class ActorManagerBeanConfig {

    @Autowired
    private EnvironmentContext environmentContext;

    @Bean
    public ActorManager actorManager() {
        ActorManager actorManager = new ActorManager();
        actorManager.setName(environmentContext.getAkkaSystemName());
        actorManager.setPath(environmentContext.getAkkaRemotePath());
        return actorManager;
    }

}
