/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
