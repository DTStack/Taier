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

package com.dtstack.taier.develop.utils.develop.sync.format;

import com.dtstack.taier.common.exception.RdosDefineException;

import java.util.Arrays;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/2/23
 */
public enum ColumnType {
    //数值
    BIT,
    TINYINT,
    SMALLINT,
    INT,
    BIGINT,
    FLOAT,
    DOUBLE,
    INTEGER,
    NUMBER,
    DECIMAL,
    NUMERIC,
    MEDIUMINT,
    REAL,
    BINARY_DOUBLE,
    UINT8,
    UINT16,
    UINT32,
    UINT64,
    INT2,
    INT4,
    INT8,
    INT16,
    INT32,
    INT64,
    FLOAT2,
    FLOAT4,
    FLOAT8,
    FLOAT32,
    FLOAT64,
    BIGSERIAL,
    SMALLSERIAL,
    SERIAL,

    //字符串
    STRING,
    VARCHAR,
    VARCHAR2,
    CHAR,
    CHARACTER,
    NCHAR,
    TINYTEXT,
    TEXT,
    CLOB,
    MEDIUMTEXT,
    LONGTEXT,
    LONGVARCHAR,
    LONGNVARCHAR,
    NVARCHAR,
    NVARCHAR2,
    BINARY,

    SMALLDATETIME,
    DATETIME,
    DATE,
    TIMESTAMP,
    TIME,
    YEAR,

    BOOLEAN,
    //libra  不能直接使用double
    DOUBLE_PRECISION;

    /**
     * 可以作为增量标识的字段类型:数值类型和时间类型
     */
    public static List<ColumnType> INCRE_TYPE = Arrays.asList(
            TINYINT, SMALLINT, INT, BIGINT, FLOAT, DOUBLE, INTEGER, NUMBER, DECIMAL, NUMERIC, MEDIUMINT, REAL, BINARY_DOUBLE, INT2,
            INT4, INT8, FLOAT2, FLOAT4, FLOAT8,UINT8, UINT16, UINT32, UINT64, INT8, INT16, INT32, INT64,
            DATETIME, TIMESTAMP, BIGSERIAL
    );

    /**
     * 判断是不是可以作为增量类型
     */
    public static boolean isIncreType(String type) {
        return INCRE_TYPE.contains(fromString(type));
    }

    public static ColumnType fromString(String type) {
        if (type == null) {
            throw new RdosDefineException("null ColumnType!");
        }

        String[] split = type.split(" ");
        if (split != null && split.length > 1) {
            //提取例如"INT UNSIGNED"情况下的字段类型
            type = split[0];
        }

        ColumnType columnType;
        try {
            if (type.matches("(?i)^number\\(\\d+,-*\\d+\\)")) {
                int p = Integer.parseInt(type.substring(type.indexOf(",") + 1, type.indexOf(")")));
                if (p == 0) {
                    columnType = BIGINT;
                } else {
                    columnType = DOUBLE;
                }
            } else if (type.toLowerCase().contains("timestamp")) {
                columnType = TIMESTAMP;
            } else {
                columnType = valueOf(type.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            columnType = ColumnType.STRING;
        }
        return columnType;
    }
}