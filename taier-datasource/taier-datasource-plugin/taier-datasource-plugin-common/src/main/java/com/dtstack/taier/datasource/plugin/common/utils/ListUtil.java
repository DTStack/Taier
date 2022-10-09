package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * list 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午3:01 2022/4/8
 * company: www.dtstack.com
 */
public class ListUtil {

    /**
     * 判断字符串 List 集合中是否包含指定字符串
     *
     * @param targetList 目标集合
     * @param searchStr  搜索的字符串
     * @return 是否包含
     */
    public static boolean containsIgnoreCase(List<String> targetList, String searchStr) {
        String targetStr = targetList
                .stream()
                .filter(x -> StringUtils.equalsIgnoreCase(x, searchStr))
                .findAny()
                .orElse(null);
        return StringUtils.isNotBlank(targetStr);
    }
}