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

package com.dtstack.taier.develop.flink.sql.source.param;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import org.apache.commons.lang.StringUtils;

/**
 * 偏移量
 */
public enum OffsetModeEnum implements ISqlParamEnum {

    /**
     * earliest
     */
    EARLIEST("earliest", "earliest", "earliest-offset"),

    /**
     * latest
     */
    LATEST("latest", "latest", "latest-offset"),
    /**
     * group ，暂时未用
     */
    @Deprecated
    GROUP("group", "group", "group-offsets"),

    /**
     * timestamp
     */
    TIMESTAMP("timestamp", "timestamp", "timestamp"),

    /**
     * specific
     */
    SPECIFIC("custom", "custom", "specific-offsets");

    /**
     * 前端页面
     */
    private final String front;
    private final String flink110;
    private final String flink112;

    OffsetModeEnum(String front, String flink110, String flink112) {
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

    public static OffsetModeEnum getByFront(String frontValue) {
        for (OffsetModeEnum offsetModeEnum : values()) {
            if (StringUtils.equalsIgnoreCase(offsetModeEnum.getFront(), frontValue)) {
                return offsetModeEnum;
            }
        }
        throw new DtCenterDefException(String.format("找不到对应的偏移量类型:%s", frontValue));
    }
}
