package com.dtstack.engine.common.exception;

/**
 * @Auther: dazhi
 * @Date: 2021/4/9 4:35 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TaskTaskRingException extends RdosDefineException {
    public TaskTaskRingException(Throwable cause) {
        super(cause);
    }

    public TaskTaskRingException(String errorMessage) {
        super(errorMessage);
    }

    public TaskTaskRingException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

    public TaskTaskRingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public TaskTaskRingException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public TaskTaskRingException(String message, ErrorCode errorCode, String url) {
        super(message, errorCode, url);
    }

    public TaskTaskRingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public TaskTaskRingException(String message, ErrorCode errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
