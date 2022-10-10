package com.dtstack.taier.datasource.plugin.common.convert;

/**
 * 用于字段类型转换
 *
 * @author ：wangchuan
 * date：Created in 上午10:56 2021/10/11
 * company: www.dtstack.com
 */
@FunctionalInterface
public interface ColumnTypeConverter {

    /**
     * @param type 字段类型，如 "SHORT", "INT", "TIMESTAMP","INT8"
     * @return 转换后的字段类型 TINYINT
     */
    String convert(String type);
}
