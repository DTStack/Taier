package com.dtstack.engine.master.config;

import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.master.akka.AkkaMasterServerImpl;
import com.dtstack.engine.master.akka.MasterServer;
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
public class MasterServerBeanConfig {

    @Autowired
    private EnvironmentContext environmentContext;

    @Bean
    public MasterServer serverStart() {
        if (AkkaConfig.isLocalMode()) {
            return null;
        }
        MasterServer masterServer = new AkkaMasterServerImpl(environmentContext);
        return masterServer;
    }

}
