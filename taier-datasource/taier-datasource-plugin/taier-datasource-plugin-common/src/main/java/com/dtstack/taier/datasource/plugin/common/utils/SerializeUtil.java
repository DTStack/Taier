package com.dtstack.taier.datasource.plugin.common.utils;

import com.alibaba.fastjson.JSON;
import com.dtstack.taier.datasource.api.exception.SourceException;

import java.util.Objects;

/**
 * 序列化工具
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/5/21
 * company: www.dtstack.com
 */
public class SerializeUtil {

    /**
     * 转换对象
     *
     * @param origin      原始对象
     * @param targetClass 目标对象 class 类型
     * @param <T>         目标对象类型
     * @return 转换后的对象
     */
    public static <T> T transBean(Object origin, Class<T> targetClass) {
        if (Objects.isNull(origin) || Objects.isNull(targetClass)) {
            throw new SourceException("origin object or target class can not be null...");
        }
        String objectJson = JSON.toJSONString(origin);
        return JSON.parseObject(objectJson, targetClass);
    }
}
