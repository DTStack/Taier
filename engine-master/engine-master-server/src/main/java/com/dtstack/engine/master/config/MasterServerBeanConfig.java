package com.dtstack.engine.master.config;

import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.sftp.SftpFileManage;
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
    public SftpFileManage sftpFileManage() {
        return SftpFileManage.getInstance();
    }

}
