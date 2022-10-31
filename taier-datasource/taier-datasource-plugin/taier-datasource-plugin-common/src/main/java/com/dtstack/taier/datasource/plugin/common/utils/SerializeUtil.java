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

package com.dtstack.taier.datasource.plugin.common.utils;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.Objects;

/**
 * 序列化工具
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/5/21
 * company: www.dtstack.com
 */
public class SerializeUtil {

    /**
     * 转换对象
     *
     * @param origin      原始对象
     * @param targetClass 目标对象 class 类型
     * @param <T>         目标对象类型
     * @return 转换后的对象
     */
    public static <T> T transBean(Object origin, Class<T> targetClass) {
        if (Objects.isNull(origin) || Objects.isNull(targetClass)) {
            throw new SourceException("origin object or target class can not be null...");
        }
        String objectJson = JSON.toJSONString(origin);
        return JSON.parseObject(objectJson, targetClass);
    }
}
