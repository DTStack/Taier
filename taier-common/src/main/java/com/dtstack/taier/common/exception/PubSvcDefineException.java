package com.dtstack.taier.common.exception;

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
