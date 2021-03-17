package com.dtstack.engine.master.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Auther: 尘二(chener @ dtstack.com)
 * @Date: 2018/12/7 16:05
 * @Description:
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    private int corePoolSize = 3;

    private int maxPoolSize = 100;

    private int queueCapacity = 8;

    private int keepAlive = 60;

    /**
     * @return
     */
    @Bean(name="taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setKeepAliveSeconds(keepAlive);
        executor.initialize();
        return executor;
    }
}
