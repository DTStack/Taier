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

package com.dtstack.taier.develop.flink.sql.core;

/**
 * sql 参数基本方法
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public interface ISqlParamEnum {

    /**
     * 获取前端映射参数
     *
     * @return 前端展示参数
     */
    String getFront();

    /**
     * 获取 flink 1.10 版本参数
     *
     * @return flink 1.10 版本参数
     */
    String getFlink110();

    /**
     * 获取 flink 1.12 版本参数
     *
     * @return flink 1.12 版本参数
     */
    String getFlink112();
}
