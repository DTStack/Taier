package com.dtstack.engine.master.config;

import com.dtstack.engine.master.akka.AkkaWorkerManager;
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
    public AkkaWorkerManager actorManager() {
        AkkaWorkerManager akkaWorkerManager = new AkkaWorkerManager();
        akkaWorkerManager.setEnvironmentContext(environmentContext);
        return akkaWorkerManager;
    }

}
