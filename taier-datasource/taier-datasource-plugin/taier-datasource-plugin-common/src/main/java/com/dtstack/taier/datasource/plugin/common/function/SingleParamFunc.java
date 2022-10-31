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

package com.dtstack.taier.datasource.plugin.common.function;

/**
 * 单入参函数
 *
 * @author ：wangchuan
 * date：Created in 下午5:42 2022/3/16
 * company: www.dtstack.com
 */
@FunctionalInterface
public interface SingleParamFunc<T, M> {

    /**
     * 方法执行
     *
     * @param param 方法执行入参
     * @return 执行结果
     * @throws Exception 异常信息
     */
    T execute(M param) throws Exception;
}
