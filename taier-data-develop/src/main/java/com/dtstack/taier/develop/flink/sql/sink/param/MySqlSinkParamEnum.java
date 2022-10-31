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

package com.dtstack.taier.develop.flink.sql.sink.param;

import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;

/**
 * @author qianyi
 */
public enum MySqlSinkParamEnum implements ISqlParamEnum {

    /**
     * 地址
     */
    url("jdbcUrl", "url", "url"),

    /**
     * 账号
     */
    userName("username", "userName", "username"),

    /**
     * 密码
     */
    password("password", "password", "password"),

    /**
     * 表名
     */
    tableName("table", "tableName", "table-name"),

    /**
     * 缓存类型
     */
    batchSize("batchSize", "batchSize", "sink.buffer-flush.max-rows"),

    /**
     * 缓存时间
     */
    batchWaitInterval("batchWaitInterval", "batchWaitInterval", "sink.buffer-flush.interval"),

    /**
     * 缓存大小
     */
    allReplace("allReplace", "allReplace", "sink.all-replace"),

    /**
     * 并发数
     */
    parallelism("parallelism", "parallelism", "sink.parallelism");

    /**
     * 前端页面
     */
    private final String front;
    private final String flink110;
    private final String flink112;

    MySqlSinkParamEnum(String front, String flink110, String flink112) {
        this.front = front;
        this.flink110 = flink110;
        this.flink112 = flink112;
    }

    @Override
    public String getFront() {
        return front;
    }

    @Override
    public String getFlink110() {
        return flink110;
    }

    @Override
    public String getFlink112() {
        return flink112;
    }
}
