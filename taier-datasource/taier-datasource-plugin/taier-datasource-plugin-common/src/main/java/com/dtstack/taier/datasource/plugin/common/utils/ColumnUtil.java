package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * column 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午7:47 2022/3/3
 * company: www.dtstack.com
 */
public class ColumnUtil {

    /**
     * 将 column 转换成 id, name, age 这种形式的字符串, 如果 list 为空则返回 *
     *
     * @param list 字段集合
     * @return string column
     */
    public static String listToStr(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return "*";
        }

        StringBuilder column = new StringBuilder();
        list.stream()
                .filter(str -> !StringUtils.isBlank(str))
                .forEach(str -> column.append(str).append(","));

        if (column.length() > 0) {
            column.deleteCharAt(column.length() - 1);
        }
        return column.toString();
    }
}
