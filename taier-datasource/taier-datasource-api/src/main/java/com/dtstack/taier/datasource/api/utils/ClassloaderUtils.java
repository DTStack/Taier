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

package com.dtstack.taier.datasource.api.utils;

import java.util.concurrent.Callable;

/**
 * classloader utils
 *
 * @author ：wangchuan
 * date：Created in 13:52 2022/9/23
 * company: www.dtstack.com
 */
public class ClassloaderUtils {

    /**
     * 执行方法前设置线程上下文 classloader 为指定的 classloader, 并在执行后设置回原始 classloader
     *
     * @param callable 执行逻辑
     * @param ec       指定的 classloader
     * @param <R>      执行结果范性
     * @return 执行结果
     */
    public static <R> R executeAndReset(Callable<R> callable, ClassLoader ec) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(ec);
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }
}
