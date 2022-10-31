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

package com.dtstack.taier.datasource.plugin.vertica;

import java.util.Locale;

/**
 * vertica client impl
 *
 * @author ：wangchuan
 * date：Created in 下午8:19 2020/12/8
 * company: www.dtstack.com
 */
public class VerticaAdapter {

    public static String mapColumnType2Flink(String columnType) {
        String columnTypeUpper = columnType.toUpperCase(Locale.ENGLISH);
        switch (columnTypeUpper) {
            case "BOOLEAN":
                return "BOOLEAN";
            case "INT":
            case "INTEGER":
            case "SMALLINT":
            case "TINYINT":
                return "INT";
            case "BIGINT":
                return "BIGINT";
            case "DECIMAL":
            case "NUMERIC":
            case "FLOAT":
            case "DOUBLE":
                return "DECIMAL";
            case "TIME":
                return "TIME";
            case "DATE":
                return "DATE";
            case "TIMESTAMP":
            case "DATETIME":
            case "TIMETZ":
            case "TIMESTAMPTZ":
                return "TIMESTAMP";
            case "VARCHAR":
            case "CHAR":
            case "LONG VARCHAR":
                return "STRING";
            case "BINARY":
            case "VARBINARY":
            case "LONG VARBINARY":
                return "BYTES";
            default:
                return columnType;
        }
    }
}