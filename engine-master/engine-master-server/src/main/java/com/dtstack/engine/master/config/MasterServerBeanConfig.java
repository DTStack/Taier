package com.dtstack.engine.master.config;

import com.dtstack.engine.common.client.ClientOperator;
import com.dtstack.engine.pluginapi.sftp.SftpFileManage;
import com.dtstack.engine.common.env.EnvironmentContext;
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
    public ClientOperator clientOperator(){
        return ClientOperator.getInstance(environmentContext.getPluginPath());
    }

    @Bean
    public SftpFileManage sftpFileManage() {
        return SftpFileManage.getInstance();
    }

}
