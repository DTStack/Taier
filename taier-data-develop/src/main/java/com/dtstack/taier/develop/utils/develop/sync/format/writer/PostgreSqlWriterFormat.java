package com.dtstack.taier.develop.utils.develop.sync.format.writer;

import com.dtstack.taier.develop.utils.develop.sync.format.ColumnType;
import com.dtstack.taier.develop.utils.develop.sync.format.TypeFormat;


public class PostgreSqlWriterFormat implements TypeFormat {

    @Override
    public String formatToString(String str) {
        return format(str).name();
    }

    private ColumnType format(String str) {
        ColumnType originType = ColumnType.fromString(str);
        switch (originType) {
            case BIT:
            case TINYINT:
            case SMALLINT:
                return ColumnType.SMALLINT;
            case INT:
            case MEDIUMINT:
            case INTEGER:
            case YEAR:
            case INT2:
            case INT4:
            case INT8:
                return ColumnType.INT;
            case BIGINT:
                return ColumnType.BIGINT;
            case REAL:
            case FLOAT:
            case FLOAT2:
            case FLOAT4:
            case FLOAT8:
                return ColumnType.FLOAT;
            case DOUBLE:
            case BINARY_DOUBLE:
                return ColumnType.DOUBLE_PRECISION;
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
                return ColumnType.VARCHAR;
            case BOOLEAN:
                return ColumnType.BOOLEAN;
            case DATE:
                return ColumnType.DATE;
            case TIME:
            case DATETIME:
            case SMALLDATETIME:
                return ColumnType.VARCHAR;
            case TIMESTAMP:
                return ColumnType.TIMESTAMP;
            default:
                throw new IllegalArgumentException();
        }
    }
}
