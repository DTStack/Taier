package com.dtstack.engine.datasource.common.exception;

import com.dtstack.dtcenter.common.exception.ExceptionEnums;
import dt.insight.plat.lang.exception.biz.BizException;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
public class PubSvcDefineException extends BizException {

    public PubSvcDefineException(String message) {
        super(message);
    }

    public PubSvcDefineException(String message, Throwable cause) {
        super(message, cause);
    }

    public PubSvcDefineException(ExceptionEnums errorCode) {
        super(errorCode.getDescription());
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode) {
        super(message, errorCode);
    }

    public PubSvcDefineException(ExceptionEnums errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
