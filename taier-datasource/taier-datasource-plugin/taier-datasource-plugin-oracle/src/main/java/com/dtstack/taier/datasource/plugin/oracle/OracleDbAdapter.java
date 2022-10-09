package com.dtstack.taier.datasource.plugin.oracle;

import java.sql.Types;

/**
 * author: 尘二
 * date: 2019/4/3 17:03
 */
public class OracleDbAdapter {

    private static final String NOT_SUPPORT = "not support";

    public static String mapColumnTypeJdbc2Java(final int columnType, int precision, int scale) {
        switch (columnType) {
            case Types.CHAR:
                return JavaType.TYPE_CHAR.getFlinkSqlType();
            case Types.CLOB:
                return JavaType.TYPE_CLOB.getFlinkSqlType();
            case Types.NCLOB:
                return JavaType.TYPE_NCLOB.getFlinkSqlType();
            case Types.BLOB:
                return JavaType.TYPE_VARCHAR_4000.getFlinkSqlType();
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
        TYPE_BIGINT("bigint"),
        TYPE_VARCHAR("varchar"),
        TYPE_VARCHAR_4000("varchar(4000)"),
        TYPE_FLOAT("float"),
        TYPE_DOUBLE("double"),
        TYPE_TIMESTAMP("timestamp"),
        TYPE_DECIMAL("decimal"),
        TYPE_CLOB("bytes"),
        TYPE_NCLOB("nclob"),
        TYPE_RAW("raw");

        private final String flinkSqlType;

        JavaType(String flinkSqlType) {
            this.flinkSqlType = flinkSqlType;
        }

        public String getFlinkSqlType() {
            return flinkSqlType;
        }
    }
}