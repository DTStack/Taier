package com.dtstack.taier.scheduler.server.action.fill;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2021/9/10 2:05 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class FillDataThreadPoolExecutor implements InitializingBean, DisposableBean {

    @Autowired
    private EnvironmentContext environmentContext;

    private final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @Override
    public void afterPropertiesSet() throws Exception {
        //线程池维护线程的最少数量
        executor.setCorePoolSize(environmentContext.getFillDataThreadPoolCorePoolSize());
        //线程池维护线程的最大数量
        executor.setMaxPoolSize(environmentContext.getMaxFillDataThreadPoolSize());
        //线程池所使用的缓冲队列
        executor.setQueueCapacity(environmentContext.getFillDataQueueCapacity());
        // 设置线程工程类
        executor.setThreadFactory(new CustomThreadFactory(this.getClass().getSimpleName()));
        executor.initialize();
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }

    public void submit(FillDataRunnable fillDataRunnable) {
        executor.execute(fillDataRunnable);
    }

}
