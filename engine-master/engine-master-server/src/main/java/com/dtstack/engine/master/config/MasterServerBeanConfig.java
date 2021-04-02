package com.dtstack.engine.master.config;

import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.common.akka.config.AkkaLoad;
import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.akka.AkkaMasterServerImpl;
import com.dtstack.engine.master.akka.MasterServer;
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
            AkkaConfig.init(AkkaLoad.load(environmentContext.getConfigPath()));
            return null;
        }
        MasterServer masterServer = new AkkaMasterServerImpl(environmentContext);
        return masterServer;
    }

    @Bean
    public ClientOperator clientOperator(){
        return ClientOperator.getInstance();
    }

}
