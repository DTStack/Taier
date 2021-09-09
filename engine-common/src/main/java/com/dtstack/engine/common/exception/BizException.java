package com.dtstack.engine.common.exception;


import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.ExceptionEnums;

/**
 * 业务异常
 *
 * @author maoba@dtatck.inc
 */
public class BizException extends DtCenterDefException {
    public BizException() {
        super(ErrorCode.SYS_BUSINESS_EXCEPTION);
    }

    public BizException(ExceptionEnums exEnum) {
        super(exEnum);
    }

    public BizException(ExceptionEnums exEnum, String message) {
        super(exEnum, message);
    }

    public BizException(ExceptionEnums exEnum, String message, Object... args) {
        super(exEnum, message, args);
    }

    public BizException(String message, Object... args) {
        super(ErrorCode.SYS_BUSINESS_EXCEPTION, message, args);
    }

    public BizException(Throwable throwable, ExceptionEnums exEnum) {
        super(throwable, exEnum);
    }

    public BizException(Throwable throwable, ExceptionEnums exEnum, String message) {
        super(throwable, exEnum, message);
    }

    public BizException(Throwable throwable, ExceptionEnums exEnum, String message, Object... args) {
        super(throwable, exEnum, message, args);
    }

    public BizException(Throwable throwable, String message, Object... args) {
        super(throwable, ErrorCode.SYS_BUSINESS_EXCEPTION, message, args);
    }

}
