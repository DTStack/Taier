package com.dtstack.taier.pluginapi.leader;

public class LockTimeoutException extends RuntimeException {
    public LockTimeoutException() {
    }

    public LockTimeoutException(String message) {
        super(message);
    }

    public LockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockTimeoutException(Throwable cause) {
        super(cause);
    }

    public LockTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
