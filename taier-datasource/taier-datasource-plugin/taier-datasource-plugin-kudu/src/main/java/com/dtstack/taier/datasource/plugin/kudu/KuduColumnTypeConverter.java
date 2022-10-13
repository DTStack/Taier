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
