package com.dtstack.engine.common.security;

public class ExitException extends SecurityException {
    private static final long serialVersionUID = 1L;
    public final int status;

    public ExitException(int status) {
        super("忽略 Exit方法调用!");
        this.status = status;
    }
}