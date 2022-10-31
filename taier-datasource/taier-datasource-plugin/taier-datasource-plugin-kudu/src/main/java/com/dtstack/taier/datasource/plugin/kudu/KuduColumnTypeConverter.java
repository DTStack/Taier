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



package com.dtstack.taier.datasource.plugin.kudu;


import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.Locale;

/**
 * @author tiezhu
 * @since 2021/6/19 星期六
 */
public class KuduColumnTypeConverter {

    /**
     * @param type kudu 原始数据类型
     * @return 转化后的数据类型
     */
    public static String apply(String type) {
        switch (type.toUpperCase(Locale.ENGLISH)) {
            case "INT8":
            case "TINYINT":
                return "TINYINT";
            case "BYTES":
            case "BINARY":
                return "BINARY";
            case "INT16":
            case "SMALLINT":
                return "SMALLINT";
            case "INT":
            case "INT32":
            case "INTEGER":
                return "INT";
            case "INT64":
            case "BIGINT":
            case "LONG":
            case "UNIXTIME_MICROS":
                return "BIGINT";
            case "BOOL":
            case "BOOLEAN":
                return "BOOLEAN";
            case "FLOAT":
                return "FLOAT";
            case "DOUBLE":
                return "DOUBLE";
            case "DECIMAL":
                return "DECIMAL(38, 18)";
            case "VARCHAR":
            case "STRING":
                return "STRING";
            case "DATE":
                return "DATE";
            case "TIMESTAMP":
                return "TIMESTAMP";
            default:
                throw new SourceException(String.format("type [%s] is not support", type));
        }
    }
}
