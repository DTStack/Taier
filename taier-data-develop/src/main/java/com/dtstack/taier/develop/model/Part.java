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

package com.dtstack.taier.develop.model;

import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.dao.domain.ComponentConfig;

import java.util.List;

public interface Part {
    EComponentType getType();

    /**
     * 获取组件显示版本 对应的 具体版本值
     *
     * @return
     */
    String getVersionValue();

    /**
     * 加载组件模版
     *
     * @return
     */
    List<ComponentConfig> loadTemplate();

    /**
     * 获取组件对应pluginName
     *
     * @return
     */
    String getPluginName();

    /**
     * 获取组件依赖的资源组件
     *
     * @return
     */
    EComponentType getResourceType();

    /**
     * 获取组件版本适配参数
     * @return
     */
    Long getExtraVersionParameters();
}
