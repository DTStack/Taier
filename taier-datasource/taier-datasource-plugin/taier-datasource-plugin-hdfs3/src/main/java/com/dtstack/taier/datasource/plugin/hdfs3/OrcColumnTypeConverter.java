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



package com.dtstack.taier.datasource.plugin.hdfs3;

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;

import java.util.Locale;

/**
 * Orc 字段类型转换器
 *
 * @author ：wangchuan
 * date：Created in 下午8:39 2021/11/22
 * company: www.dtstack.com
 */
public class OrcColumnTypeConverter {

    /**
     * {@link TypeInfoFactory}
     * @param type 原始数据类型
     * @return 转化后的数据类型
     */
    public static String apply(String type) {
        String upperCase = type.toUpperCase(Locale.ENGLISH);
        int endIndex = upperCase.contains("(") ? upperCase.indexOf("(") : upperCase.length();
        switch (upperCase.substring(0, endIndex).trim()) {
            case "INT":
            case "INTEGER":
                return "int";
            case "SMALLINT":
                return "smallint";
            case "TINYINT":
                return "tinyint";
            case "BINARY":
                return "binary";
            case "BIGINT":
                return "bigint";
            case "BOOLEAN":
                return "boolean";
            case "FLOAT":
                return "float";
            case "DOUBLE":
                return "double";
            case "DATE":
                return "date";
            case "TIMESTAMP":
                return "timestamp";
            default:
                if (type.contains("DECIMAL")) {
                    return type.toLowerCase();
                }
                return "string";
        }
    }
}
