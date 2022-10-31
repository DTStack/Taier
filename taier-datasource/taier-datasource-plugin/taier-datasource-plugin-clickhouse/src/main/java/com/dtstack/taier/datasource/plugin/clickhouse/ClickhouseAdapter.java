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

package com.dtstack.taier.datasource.plugin.clickhouse;

import java.sql.Types;

public class ClickhouseAdapter {
    public static String mapColumnTypeJdbc2Oracle(final int columnType,int precision,int scale){
        //TODO 转化成用户读(oracle显示)类型
        return null;
    }

    public static String mapColumnTypeJdbc2Java(final int columnType,int precision,int scale){
        switch (columnType){
            case Types.CHAR:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();
            case Types.CLOB:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();
            case Types.LONGVARCHAR:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();
            case Types.VARCHAR:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();
            case Types.NVARCHAR:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();
            case Types.NCLOB:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();


            case Types.DATE:
                return JavaType.TYPE_DATE.getFlinkSqlType();
            case Types.TIME:
                return JavaType.TYPE_DATE.getFlinkSqlType();
            case Types.TIMESTAMP:
                return JavaType.TYPE_TIMESTAMP.getFlinkSqlType();


            case Types.BIGINT:
                return JavaType.TYPE_BIGINT.getFlinkSqlType();
            case Types.INTEGER:
                return JavaType.TYPE_INT.getFlinkSqlType();
            case Types.SMALLINT:
                return JavaType.TYPE_INT.getFlinkSqlType();
            case Types.TINYINT:
                return JavaType.TYPE_INT.getFlinkSqlType();


            case Types.BIT:
                return JavaType.TYPE_BOOLEAN.getFlinkSqlType();

            case Types.DECIMAL:
                return JavaType.TYPE_DECIMAL.getFlinkSqlType();
            case Types.DOUBLE:
                return JavaType.TYPE_DOUBLE.getFlinkSqlType();
            case Types.FLOAT:
                return JavaType.TYPE_DOUBLE.getFlinkSqlType();
            case Types.REAL:
                return JavaType.TYPE_FLOAT.getFlinkSqlType();
            //FIXME 正常来说，需要精确映射成int/long/double等
            case Types.NUMERIC:
                return JavaType.TYPE_DECIMAL.getFlinkSqlType();

            default:
                return null;
        }
    }

    public enum JavaType{

        TYPE_BOOLEAN("boolean"),
        TYPE_INT("int"),
        TYPE_INTEGER("integer"),
        TYPE_BIGINT("bigint"),
        TYPE_TINYINT("tinyint"),
        TYPE_SMALLINT("smallint"),
        TYPE_VARCHAR("varchar"),
        TYPE_FLOAT("float"),
        TYPE_DOUBLE("double"),
        TYPE_DATE("date"),
        TYPE_TIMESTAMP("datetime"),
        TYPE_DECIMAL("decimal");

        private String flinkSqlType;

        JavaType(String flinkSqlType) {
            this.flinkSqlType = flinkSqlType;
        }

        public String getFlinkSqlType() {
            return flinkSqlType;
        }
    }
}