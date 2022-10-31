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

package com.dtstack.taier.develop.sql.node;

import com.dtstack.taier.develop.sql.Column;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/26 10:11
 * @Description:对应于: {@link org.apache.calcite.sql.SqlCall}
 * 非叶子结点。是一个sql operator的调用。
 *
 * 通常来讲，每一个非叶子结点都应当是一个call（select，insert这种由于其需要的方法太多，
 * 将其单独作为了Node，而非继承Call）。call和operator基本上描述了一个node
 *
 * call是对一系列操作数的调用
 */
public abstract class BaseCall extends Node {
    public BaseCall(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }
}
