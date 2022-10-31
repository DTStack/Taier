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

import com.dtstack.taier.datasource.plugin.common.function.SingleParamFunc;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.function.Supplier;

/**
 * 针对 AutoCloseable 接口实现类进行操作的工具类
 *
 * @author ：wangchuan
 * date：Created in 下午5:04 2022/3/16
 * company: www.dtstack.com
 */
public class CloseableUtil {

    /**
     * 执行方法并调用 close 关闭 obj
     *
     * @param autoCloseSupplier 获取 autoClose 接口实现类的方法, 只有在调用
     *                          {@link java.util.function.Supplier#get()} 方法时才会进行获取</p>
     * @param callbackFunc      方法执行回调
     * @param <T>               返回值的范型
     * @return 方法执行结果
     */
    public static <T, M extends AutoCloseable> T executeAndClose(Supplier<M> autoCloseSupplier,
                                                                 SingleParamFunc<T, M> callbackFunc) {
        try (M closeableObj = autoCloseSupplier.get()) {
            return callbackFunc.execute(closeableObj);
        } catch (Exception e) {
            throw new SourceException(String.format("execute method error : %s", e.getMessage()), e);
        }
    }
}
