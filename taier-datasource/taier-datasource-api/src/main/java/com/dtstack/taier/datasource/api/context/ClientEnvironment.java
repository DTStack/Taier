package com.dtstack.taier.datasource.api.context;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.config.Configuration;
import com.dtstack.taier.datasource.api.manager.ManagerFactory;

/**
 * 执行环境构建
 *
 * @author ：wangchuan
 * date：Created in 20:39 2022/9/23
 * company: www.dtstack.com
 */
public class ClientEnvironment {

    /**
     * 全局配置
     */
    private Config config;

    /**
     * manager 工厂
     */
    private ManagerFactory managerFactory;

    /**
     * 有参构造
     *
     * @param config 配置信息
     */
    public ClientEnvironment(Config config) {
        this.config = config;
    }

    public ClientEnvironment() {
        this.config = new Configuration();
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public ManagerFactory getManagerFactory() {
        return managerFactory;
    }

    /**
     * 启动
     */
    public void start() {
        // 初始化 manager factory
        managerFactory = new ManagerFactory();
        // 初始化上下文
        RuntimeContext runtimeContext = new RuntimeContext();
        runtimeContext.setConfig(config);
        managerFactory.init(runtimeContext);
    }

    /**
     * 停止
     */
    public void stop() {
        if (managerFactory != null) {
            managerFactory.close();
        }
    }
}
