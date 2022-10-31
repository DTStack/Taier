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

package com.dtstack.taier.common.enums;


import com.dtstack.taier.common.exception.DtCenterDefException;

/**
 * @author qianyi
 */
public enum TableType {
    /**
     * 源表
     */
    SOURCE(1, "源表"),

    /**
     * 结果表
     */
    SINK(2, "结果表"),

    /**
     * 维表
     */
    SIDE(3, "维表");

    private final Integer tableType;

    private final String name;


    public Integer getTableType() {
        return tableType;
    }

    public String getName() {
        return name;
    }

    TableType(Integer tableType, String name) {
        this.tableType = tableType;
        this.name = name;
    }

    public static TableType getByType(Integer type) {
        for (TableType tableType : values()) {
            if (tableType.tableType.equals(type)) {
                return tableType;
            }
        }
        throw new DtCenterDefException(String.format("找不到[%s]对应的表类型", type));
    }
}
