package com.dtstack.batch.common.exception;

import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.exception.RdosDefineException;

public class PubSvcDefineException extends RdosDefineException {

    public PubSvcDefineException(String message) {
        super(message);
    }

    public PubSvcDefineException(String message, Throwable cause) {
        super(message,cause);
    }

    public PubSvcDefineException(ExceptionEnums errorCode) {
        super(errorCode.getDescription());
    }

    public PubSvcDefineException(String message, ExceptionEnums errorCode) {
        super(errorCode.getDescription());
    }

}
