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

package com.dtstack.taier.datasource.api.dto.tsdb;

import java.util.HashMap;
import java.util.Map;

/**
 * 聚合器类型
 *
 * @author ：wangchuan
 * date：Created in 上午10:20 2021/6/24
 * company: www.dtstack.com
 */
public enum Aggregator {

    AVG("avg"), COUNT("count"), DEV("dev"), FIRST("first"), LAST("last"),
    MIMMIN("mimmin"), MIMMAX("mimmax"), MIN("min"), MAX("max"), NONE("none"),
    P50("p50"), P75("p75"), P90("p90"), P95("p95"), P99("p99"), P999("p999"),
    SUM("sum"), ZIMSUM("zimsum");

    private static final Map<String, Aggregator> CODE_MAP = new HashMap<String, Aggregator>();

    static {
        for (Aggregator typeEnum : Aggregator.values()) {
            CODE_MAP.put(typeEnum.getName(), typeEnum);
        }
    }

    public static Aggregator getEnum(String name) {
        return CODE_MAP.get(name);
    }

    private final String name;

    Aggregator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
