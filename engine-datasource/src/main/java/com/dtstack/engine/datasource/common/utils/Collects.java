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

package com.dtstack.engine.datasource.common.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 数组集合工具类
 * @description:
 * @author: liuxx
 * @date: 2021/3/12
 */
public class Collects {


    public final static boolean isEmpty(String[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public final static boolean isNotEmpty(String[] array) {
        return !isEmpty(array);
    }

    public final static boolean isEmpty(Collection<?> collection) {
        return Objects.isNull(collection) || collection.size() == 0;
    }

    public final static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 构建一个不可增删改的List
     *
     * @return 空的List集合
     */
    public final static List emptyList() {
        return Collections.emptyList();
    }

}
