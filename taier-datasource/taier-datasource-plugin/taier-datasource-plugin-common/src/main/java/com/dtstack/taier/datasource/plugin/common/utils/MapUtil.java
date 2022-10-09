package com.dtstack.taier.datasource.plugin.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Map 工具类
 *
 * @author ：wangchuan
 * date：Created in 下午9:37 2021/11/9
 * company: www.dtstack.com
 */
public class MapUtil {

    /**
     * 为map集合添加 key、value - 当 value 不为 null 时添加
     *
     * @param params 参数列表
     * @param key    key
     * @param value  value
     */
    public static void putIfValueNotBlank(Map<String, String> params, String key, String value) {
        if (Objects.nonNull(params) && StringUtils.isNotBlank(value)) {
            params.put(key, value);
        }
    }
}
