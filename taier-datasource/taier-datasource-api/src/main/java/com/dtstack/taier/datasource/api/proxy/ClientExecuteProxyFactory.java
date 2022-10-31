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

package com.dtstack.taier.datasource.api.proxy;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.manager.ManagerFactory;

import java.lang.reflect.Proxy;

/**
 * client 代理工厂类, 提供获取 client 代理的功能
 *
 * @author ：wangchuan
 * date：Created in 11:35 2022/9/23
 * company: www.dtstack.com
 */
public class ClientExecuteProxyFactory {

    /**
     * 动态代理获取代理对象
     *
     * @param originClient   原始 client
     * @param clientType     代理类 class 类型
     * @param config         配置
     * @param managerFactory manager factory
     * @param <T>            返回值范型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxyClient(T originClient,
                                       Class<T> clientType,
                                       Config config,
                                       ManagerFactory managerFactory) {
        // 创建 client 代理对象
        return (T) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{clientType},
                new ClientProxyInvocationHandle<>(originClient, config, managerFactory));
    }
}
