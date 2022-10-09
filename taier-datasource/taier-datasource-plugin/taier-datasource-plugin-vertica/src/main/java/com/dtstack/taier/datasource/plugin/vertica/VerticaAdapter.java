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