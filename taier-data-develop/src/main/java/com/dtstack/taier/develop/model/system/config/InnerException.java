package com.dtstack.taier.develop.model.system.config;

class InnerException extends RuntimeException {
    public InnerException(String message) {
        super(message);
    }

    public InnerException(String message, Throwable cause) {
        super(message, cause);
    }
}
