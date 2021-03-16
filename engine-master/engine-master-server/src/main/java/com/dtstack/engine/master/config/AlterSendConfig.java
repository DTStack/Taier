package com.dtstack.engine.master.config;

import com.dtstack.engine.alert.AlterConfig;
import com.dtstack.engine.alert.AlterSender;
import com.dtstack.engine.alert.DefaultAlterSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 3:27 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Configuration
public class AlterSendConfig {

    
    @Bean
    public AlterSender getAlterSender(){
        AlterConfig alterConfig = new AlterConfig();
        return new DefaultAlterSender(alterConfig);
    }
}
