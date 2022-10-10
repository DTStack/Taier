package com.dtstack.taier.datasource.plugin.common.utils;

import java.util.Objects;
import java.util.Properties;

/**
 * properties 工具类
 *
 * @author ：wangchuan
 * date：Created in 上午10:57 2021/9/13
 * company: www.dtstack.com
 */
public class PropertyUtil {

    /**
     * key 和 value 不为空是插入
     *
     * @param properties 配置类
     * @param key        key
     * @param value      value
     */
    public static void putIfNotNull(Properties properties, Object key, Object value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            properties.put(key, value);
        }
    }
}
