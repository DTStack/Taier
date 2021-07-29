package com.dtstack.engine.datasource.common.utils;

import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import org.apache.commons.lang3.StringUtils;

public abstract class AssertUtils {
    public static void notNull(Object obj, ErrorCode errorCode) {
        if (obj == null) {
            throw new PubSvcDefineException(errorCode);
        }
    }

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new PubSvcDefineException(message);
        }
    }

    public static void notBlank(String obj, ErrorCode errorCode) {
        if (StringUtils.isBlank(obj)) {
            throw new PubSvcDefineException(errorCode);
        }
    }
}
