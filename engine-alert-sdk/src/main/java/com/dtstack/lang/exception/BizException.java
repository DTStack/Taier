package com.dtstack.lang.exception;

/**
 * 业务异常
 * @Author 猫爸(maoba@dtstack.com)
 * @Date 2017-05-04 9:13 AM
 * @Motto 一生伏首拜阳明
 */
public class BizException extends RuntimeException {
    public BizException() {
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
