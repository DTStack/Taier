package com.dtstack.taier.datasource.api.context;

import com.dtstack.taier.datasource.api.config.Config;

/**
 * client 执行上下文
 *
 * @author ：wangchuan
 * date：Created in 20:39 2022/9/23
 * company: www.dtstack.com
 */
public class RuntimeContext {

    private Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
