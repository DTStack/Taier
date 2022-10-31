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

package com.dtstack.taier.datasource.api.manager;

import com.dtstack.taier.datasource.api.context.enhance.ManagerEnhance;
import com.dtstack.taier.datasource.api.context.RuntimeContext;
import com.dtstack.taier.datasource.api.exception.InitializeException;
import com.dtstack.taier.datasource.api.initialize.AbstractEnvInitialize;

/**
 * 初始化 manager
 *
 * @author ：wangchuan
 * date：Created in 19:56 2022/9/23
 * company: www.dtstack.com
 */
public abstract class AbstractManager extends AbstractEnvInitialize implements ManagerEnhance {

    private RuntimeContext runtimeContext;

    private ManagerFactory managerFactory;

    @Override
    public void setRuntimeContext(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    @Override
    public RuntimeContext getRuntimeContext() {
        if (this.runtimeContext != null) {
            return this.runtimeContext;
        } else {
            throw new InitializeException("The runtime context has not been initialized.");
        }
    }

    @Override
    public void setManagerFactory(ManagerFactory managerFactory) {
        this.managerFactory = managerFactory;
    }

    @Override
    public ManagerFactory getManagerFactory() {
        if (this.managerFactory != null) {
            return this.managerFactory;
        } else {
            throw new InitializeException("The manager factory has not been initialized.");
        }
    }
}
