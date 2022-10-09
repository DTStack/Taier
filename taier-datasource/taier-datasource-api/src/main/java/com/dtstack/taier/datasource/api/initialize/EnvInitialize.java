package com.dtstack.taier.datasource.api.initialize;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.exception.InitializeException;

/**
 * 环境初始化
 *
 * @author ：wangchuan
 * date：Created in 20:11 2022/9/23
 * company: www.dtstack.com
 */
public interface EnvInitialize {

    /**
     * 初始化
     *
     * @param config 配置信息
     * @throws InitializeException 初始化异常信息
     */
    void init(Config config) throws InitializeException;

    /**
     * 销毁
     */
    void destroy();
}
