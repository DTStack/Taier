package com.dtstack.taier.datasource.api.manager.list;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.constant.ConfigConstants;
import com.dtstack.taier.datasource.api.manager.AbstractManager;
import com.dtstack.taier.datasource.api.thread.ProxyThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 管理代理代理方法执行的线程池
 *
 * @author ：wangchuan
 * date：Created in 14:54 2022/9/23
 * company: www.dtstack.com
 */
@Slf4j
public class ProxyThreadPoolManager extends AbstractManager {

    /**
     * 每个 classloader 维护一个线程池
     */
    private Map<ClassLoader, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<>();

    @Override
    public void open() {
        // ignore
    }

    @Override
    public void close() {
        threadPoolExecutorMap.keySet().forEach(this::destroyByClassloader);
        threadPoolExecutorMap.clear();
        // help gc.
        threadPoolExecutorMap = null;
    }

    /**
     * 根据 classloader 销毁对应的 threadPool
     *
     * @param classLoader classloader
     */
    public void destroyByClassloader(ClassLoader classLoader) {
        if (classLoader == null) {
            log.warn("wait destroy classloader is null.");
            return;
        }
        if (!(classLoader instanceof URLClassLoader)) {
            return;
        }
        List<URL> urLs = Arrays.asList(((URLClassLoader) classLoader).getURLs());

        log.info("start shutdown proxy thread pool, classloader urls: [{}]...", urLs);
        threadPoolExecutorMap
                .remove(classLoader)
                .shutdownNow();
        log.info("shutdown proxy thread pool , classloader urls: [{}] success.", urLs);
    }


    /**
     * 获取线程池
     *
     * @param classLoader 指定的 classloader
     * @return 线程池
     */
    public ThreadPoolExecutor getThreadPoolExecutor(ClassLoader classLoader) {
        String pluginName = getManagerFactory().getManager(ClassloaderManager.class).getPluginNameByClassloader(classLoader);
        ProxyThreadFactory proxyThreadFactory = new ProxyThreadFactory(classLoader, pluginName);
        Config config = getRuntimeContext().getConfig();
        return threadPoolExecutorMap.computeIfAbsent(
                classLoader,
                key -> new ThreadPoolExecutor(
                        config.getConfig(ConfigConstants.EXECUTE_POOL_CORE_SIZE, Integer.class, 10),
                        config.getConfig(ConfigConstants.EXECUTE_POOL_MAX_SIZE, Integer.class, 10),
                        config.getConfig(ConfigConstants.EXECUTE_POOL_KEEPALIVE_TIME, Integer.class, 10),
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<>(config.getConfig(ConfigConstants.EXECUTE_POOL_QUEUE_SIZE, Integer.class, 100)),
                        proxyThreadFactory));
    }
}
