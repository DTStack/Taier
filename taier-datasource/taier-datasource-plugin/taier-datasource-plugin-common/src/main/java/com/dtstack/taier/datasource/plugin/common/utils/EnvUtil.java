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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 环境参数工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:02 2021/9/22
 * company: www.dtstack.com
 */
public class EnvUtil {

    /**
     * 测试连通性超时时间对应环境变量
     */
    private final static String TEST_CONN_TIMEOUT_KEY = "LOADER_TEST_CONN_TIMEOUT";

    // 测试连通性超时时间。单位：秒
    private final static int TEST_CONN_TIMEOUT = 30;

    /**
     * 获取测试连通性超时时间
     *
     * @return 超时时间
     */
    public static int getTestConnTimeout() {
        return getTestConnTimeout(TEST_CONN_TIMEOUT);
    }

    /**
     * 获取测试连通性超时时间
     *
     * @param def 默认超时时间
     * @return 超时时间
     */
    public static int getTestConnTimeout(int def) {
        String timeout = System.getenv(TEST_CONN_TIMEOUT_KEY);
        if (StringUtils.isNotBlank(timeout) && NumberUtils.isNumber(timeout)) {
            return NumberUtils.toInt(timeout);
        }
        return def;
    }
}
