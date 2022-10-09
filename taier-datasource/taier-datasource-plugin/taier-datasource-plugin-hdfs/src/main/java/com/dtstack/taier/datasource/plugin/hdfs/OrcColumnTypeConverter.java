

package com.dtstack.taier.datasource.plugin.hdfs;

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
                if (upperCase.contains("DECIMAL")) {
                    return upperCase.toLowerCase();
                }
                return "string";
        }
    }
}
