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

package com.dtstack.taier.datasource.plugin.oracle;

import java.sql.Types;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/4/3 17:03
 * @Description:
 */
public class OracleDbAdapter {

    private static final String NOT_SUPPORT = "not support";

    public static String mapColumnTypeJdbc2Oracle(final int columnType, int precision, int scale) {
        //TODO 转化成用户读(oracle显示)类型
        return null;
    }

    public static String mapColumnTypeJdbc2Java(final int columnType, int precision, int scale) {
        switch (columnType) {
            case Types.CHAR:
                return JavaType.TYPE_CHAR.getFlinkSqlType();
            case Types.CLOB:
                return JavaType.TYPE_CLOB.getFlinkSqlType();
            case Types.NCLOB:
                return JavaType.TYPE_NCLOB.getFlinkSqlType();
            case Types.BLOB:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();

            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return JavaType.TYPE_TIMESTAMP.getFlinkSqlType();


            case Types.BIGINT:
                return JavaType.TYPE_BIGINT.getFlinkSqlType();
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return JavaType.TYPE_INT.getFlinkSqlType();


            case Types.BIT:
                return JavaType.TYPE_BOOLEAN.getFlinkSqlType();

            case Types.DECIMAL:
            case Types.NUMERIC:
                return JavaType.TYPE_DECIMAL.getFlinkSqlType();
            case Types.DOUBLE:
            case Types.FLOAT:
                return JavaType.TYPE_DOUBLE.getFlinkSqlType();
            case Types.REAL:
                return JavaType.TYPE_FLOAT.getFlinkSqlType();
            case Types.VARBINARY:
                return JavaType.TYPE_RAW.getFlinkSqlType();
            default:
                return NOT_SUPPORT;
        }
    }

    public enum JavaType {
        TYPE_BOOLEAN("boolean"),
        TYPE_INT("int"),
        TYPE_CHAR("char"),
        TYPE_INTEGER("integer"),
        TYPE_BIGINT("bigint"),
        TYPE_TINYINT("tinyint"),
        TYPE_SMALLINT("smallint"),
        TYPE_VARCHAR("varchar"),
        TYPE_FLOAT("float"),
        TYPE_DOUBLE("double"),
        TYPE_DATE("date"),
        TYPE_TIMESTAMP("timestamp"),
        TYPE_DECIMAL("decimal"),
        TYPE_CLOB("clob"),
        TYPE_NCLOB("nclob"),
        TYPE_RAW("raw");

        private String flinkSqlType;

        JavaType(String flinkSqlType) {
            this.flinkSqlType = flinkSqlType;
        }

        public String getFlinkSqlType() {
            return flinkSqlType;
        }
    }
}