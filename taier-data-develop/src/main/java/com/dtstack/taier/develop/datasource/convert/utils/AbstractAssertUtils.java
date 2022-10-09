package com.dtstack.taier.develop.datasource.convert.utils;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ExceptionEnums;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 断言工具类
 *
 * @author ：wangchuan
 * date：Created in 下午2:16 2021/7/5
 * company: www.dtstack.com
 */
public abstract class AbstractAssertUtils {

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new DtCenterDefException(message);
        }
    }

    public static void isOverLength(String content, Integer limit, String message) {
        if (StringUtils.isNotBlank(content) && content.length() > limit) {
            throw new DtCenterDefException(message);
        }
    }

    public static void isTrue(boolean expression, ExceptionEnums exceptionEnums) {
        if (!expression) {
            throw new DtCenterDefException(exceptionEnums);
        }
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new DtCenterDefException(message);
        }
    }

    public static void isNull(Object obj, String message) {
        if (obj != null) {
            throw new DtCenterDefException(message);
        }
    }

    public static void isNull(Object obj, ExceptionEnums exceptionEnums) {
        if (obj != null) {
            throw new DtCenterDefException(exceptionEnums);
        }
    }

    public static void notBlank(String obj, ExceptionEnums exceptionEnums) {
        if (StringUtils.isBlank(obj)) {
            throw new DtCenterDefException(exceptionEnums);
        }
    }

    public static void notBlank(String obj, String message) {
        if (StringUtils.isBlank(obj)) {
            throw new DtCenterDefException(message);
        }
    }

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new DtCenterDefException(message);
        }
    }

    public static void isFalse(boolean expression, ExceptionEnums exceptionEnums) {
        if (expression) {
            throw new DtCenterDefException(exceptionEnums);
        }
    }

    public static void notNull(Object obj, ExceptionEnums exceptionEnums) {
        if (obj == null) {
            throw new DtCenterDefException(exceptionEnums);
        }
    }

    public static <T> void notNull(Collection<T> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new DtCenterDefException(message);
        }
    }

    public static <T> void notNull(Collection<T> collection, ExceptionEnums exceptionEnums) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new DtCenterDefException(exceptionEnums);
        }
    }

    public static <K, V> void notEmpty(Map<K, V> collection, String message) {
        if (MapUtils.isEmpty(collection)) {
            throw new DtCenterDefException(message);
        }
    }

}
