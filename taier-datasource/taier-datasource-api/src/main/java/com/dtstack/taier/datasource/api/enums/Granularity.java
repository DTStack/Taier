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

import java.util.HashMap;
import java.util.Map;

/**
 * TSDB 时间粒度
 *
 * @author ：wangchuan
 * date：Created in 上午10:25 2021/6/23
 * company: www.dtstack.com
 */
public enum Granularity {
    S1("1s"), S5("5s"), S15("15s"), M1("1m"), M5("5m"), M15("15m"), M60("60m"), H1("1h"), H2("2h"), H6("6h"), H24("24h");

    private static final Map<String, Granularity> CODE_MAP = new HashMap<String, Granularity>();

    static {
        for (Granularity typeEnum : Granularity.values()) {
            CODE_MAP.put(typeEnum.getName(), typeEnum);
        }
    }

    public static Granularity getEnum(String name) {
        return CODE_MAP.get(name);
    }

    private final String name;

    Granularity(String name) {
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
