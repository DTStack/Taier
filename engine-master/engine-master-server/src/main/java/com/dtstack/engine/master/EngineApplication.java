package com.dtstack.engine.master;

import com.dtstack.engine.common.security.NoExitSecurityManager;
import com.dtstack.engine.common.util.SystemPropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/07/08
 */
@SpringBootApplication(exclude = {
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class EngineApplication {

    private static Logger LOGGER = LoggerFactory.getLogger(EngineApplication.class);

    public static void main(String[] args) {
        try {
            SystemPropertyUtil.setSystemUserDir();
            SpringApplication application = new SpringApplication(EngineApplication.class);
            application.addListeners(new LogbackComponent());
            application.run(args);
            System.setSecurityManager(new NoExitSecurityManager());
        } catch (Throwable t) {
            LOGGER.error("start error:", t);
            System.exit(-1);
        } finally {
            LOGGER.info("engine-master start end...");
        }
    }
}