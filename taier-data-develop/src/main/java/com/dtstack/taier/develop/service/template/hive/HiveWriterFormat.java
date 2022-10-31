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

package com.dtstack.taier.develop.service.template.hive;


import com.dtstack.taier.develop.utils.develop.sync.format.ColumnType;
import com.dtstack.taier.develop.utils.develop.sync.format.TypeFormat;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/4/26
 */
public class HiveWriterFormat implements TypeFormat {

    @Override
    public String formatToString(String str) {
        return format(str).name();
    }

    private ColumnType format(String str) {
        ColumnType originType = ColumnType.fromString(str);
        switch (originType) {
            case BIT:
            case TINYINT:
                return ColumnType.TINYINT;
            case SMALLINT:
            case SMALLSERIAL:
                return ColumnType.SMALLINT;
            case INT:
            case MEDIUMINT:
            case INTEGER:
            case YEAR:
            case INT2:
            case INT4:
            case INT8:
            case UINT8:
            case UINT16:
            case UINT32:
            case INT16:
            case INT32:
            case SERIAL:
                return ColumnType.INT;
            case BIGINT:
            case UINT64:
            case INT64:
            case BIGSERIAL:
                return ColumnType.BIGINT;
            case REAL:
            case FLOAT:
            case FLOAT2:
            case FLOAT4:
            case FLOAT8:
            case FLOAT32:
                return ColumnType.FLOAT;
            case DOUBLE:
            case BINARY_DOUBLE:
            case FLOAT64:
                return ColumnType.DOUBLE;
            case NUMERIC:
            case NUMBER:
            case DECIMAL:
                return ColumnType.DECIMAL;
            case STRING:
            case VARCHAR:
            case VARCHAR2:
            case CHAR:
            case CHARACTER:
            case NCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case LONGVARCHAR:
            case LONGNVARCHAR:
            case NVARCHAR:
            case NVARCHAR2:
            case CLOB:
            case BINARY:
                return ColumnType.STRING;
            case BOOLEAN:
                return ColumnType.BOOLEAN;
            case DATE:
                return ColumnType.DATE;
            case TIME:
            case DATETIME:
            case SMALLDATETIME:
                return ColumnType.STRING;
            case TIMESTAMP:
                return ColumnType.TIMESTAMP;
            default:
                throw new IllegalArgumentException(originType + " is an illegal column Type");
        }
    }
}
