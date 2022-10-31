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

/**
 * 基础类增强, 提供获取运行时上下文等能力
 *
 * @author ：wangchuan
 * date：Created in 10:05 2022/9/23
 * company: www.dtstack.com
 */
public interface Enhance {

    /**
     * 获取运行环境
     *
     * @return 运行环境
     */
    RuntimeContext getRuntimeContext();

    /**
     * 设置运行环境
     */
    void setRuntimeContext(RuntimeContext runtimeContext);
}
