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

package com.dtstack.taier.datasource.api.config;

import com.dtstack.taier.datasource.api.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * abs config
 *
 * @author ：wangchuan
 * date：Created in 20:19 2022/9/23
 * company: www.dtstack.com
 */
public abstract class AbstractConfig implements Config {

    /**
     * 配置缓存
     */
    private final Map<String, Object> configCache = new ConcurrentHashMap<>();

    /**
     * 获取配置项
     *
     * @param key  配置 key
     * @param type 配置项类型
     * @param <T>  类型范型
     * @return 配置项
     */
    public <T> T getConfig(String key, Class<T> type) {
        return ClassUtils.castOrThrow(type, configCache.get(key));
    }

    @Override
    public <T> T getConfig(String key, Class<T> type, T defaultValue) {
        T value = getConfig(key, type);
        return Objects.isNull(value) ? defaultValue : value;
    }

    /**
     * 设置配置
     *
     * @param key   配置 key
     * @param value 配置 value
     */
    public void setConfig(String key, Object value) {
        configCache.put(key, value);
    }

    /**
     * 添加所有配置项
     *
     * @param other 其他配置类
     */
    public void addAll(Configuration other) {
        configCache.putAll(other.toMap());
    }

    /**
     * 添加所有配置项
     *
     * @param map map
     */
    public void addAll(Map<String, Object> map) {
        configCache.putAll(map);
    }

    /**
     * 转 map
     *
     * @return map config
     */
    public Map<String, Object> toMap() {
        synchronized (configCache) {
            return new HashMap<>(configCache);
        }
    }
}
