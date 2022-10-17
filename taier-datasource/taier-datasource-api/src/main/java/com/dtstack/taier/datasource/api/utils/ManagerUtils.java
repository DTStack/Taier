/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.datasource.api.utils;

import com.dtstack.taier.datasource.api.context.RuntimeContext;
import com.dtstack.taier.datasource.api.manager.AbstractManager;
import com.dtstack.taier.datasource.api.manager.ManagerFactory;

/**
 * manager utils
 *
 * @author ：wangchuan
 * date：Created in 11:24 2022/9/23
 * company: www.dtstack.com
 */
public class ManagerUtils {

    /**
     * 为 datasource 中的 manager 设置上下文和 ManagerFactory
     *
     * @param abstractManager 需要处理的 manager
     * @param runtimeContext  上下文信息
     * @param managerFactory  ManagerFactory
     */
    public static void setRCAndMF(AbstractManager abstractManager, RuntimeContext runtimeContext, ManagerFactory managerFactory) {
        abstractManager.setRuntimeContext(runtimeContext);
        abstractManager.setManagerFactory(managerFactory);
    }
}
