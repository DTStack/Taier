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

package com.dtstack.taier.datasource.api.enums;

/**
 * company: www.dtstack.com
 *
 * @author ：nanqi
 * date ：Created in 14:15 2020/2/5
 * description：Redis 模式
 */
public enum RedisMode {
    /**
     * 单点
     */
    Standalone(1),

    /**
     * 哨兵
     */
    Sentinel(2),

    /**
     * 集群
     */
    Cluster(3);

    private int value;

    public int getValue() {
        return value;
    }

    RedisMode(int value) {
        this.value = value;
    }

    public static RedisMode getRedisModel(int mode) {
        for (RedisMode value : values()) {
            if (mode == value.getValue()) {
                return value;
            }
        }
        return null;
    }
}
