package com.dtstack.taier.datasource.plugin.impala;

import java.sql.Types;

/**
 * ref: https://documentation.sas.com/doc/en/vdmmlcdc/8.1/ds2pg/n1vzzs6f1zx6a6n1c10tihbwx6xu.htm
 * @author leon
 * @date 2022-07-20 11:06
 **/
public class ImpalaSqlAdapter {

    public static String mapColumnTypeJdbc2Java(final int columnType,int precision,int scale){
        switch (columnType){
            case Types.CHAR:
            case Types.VARCHAR:
                return JavaType.TYPE_VARCHAR.getFlinkSqlType();


            case Types.DATE:
            case Types.TIME:
                return JavaType.TYPE_DATE.getFlinkSqlType();
            case Types.TIMESTAMP:
                if (scale == 0){
                    return JavaType.TYPE_DATE.getFlinkSqlType();
                }
                return JavaType.TYPE_TIMESTAMP.getFlinkSqlType();

            case Types.BIGINT:
                return JavaType.TYPE_BIGINT.getFlinkSqlType();
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                return JavaType.TYPE_INT.getFlinkSqlType();

            case Types.DECIMAL:
                return JavaType.TYPE_DECIMAL.getFlinkSqlType();
            case Types.DOUBLE:
            case Types.FLOAT:
                return JavaType.TYPE_DOUBLE.getFlinkSqlType();
            case Types.REAL:
                return JavaType.TYPE_FLOAT.getFlinkSqlType();
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
        TYPE_TIMESTAMP("timestamp"),
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
