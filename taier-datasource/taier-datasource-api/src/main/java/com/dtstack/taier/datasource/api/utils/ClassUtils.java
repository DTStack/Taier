package com.dtstack.taier.datasource.api.utils;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;

/**
 * class utils
 *
 * @author ：wangchuan
 * date：Created in 11:22 2022/8/25
 * company: www.dtstack.com
 */
public class ClassUtils {

    public static <T> T castOrThrow(Class<T> type, Object origin) {
        if (origin == null) {
            return null;
        }
        return TypeUtils.cast(origin, type, ParserConfig.global);
    }
}