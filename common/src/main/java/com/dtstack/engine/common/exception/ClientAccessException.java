package com.dtstack.engine.common.exception;

/**
 * @author toutian
 */
public class ClientAccessException extends Exception {

    private static final String CLIENT_INIT_EXCEPTION = "Client access exception. ";

    public ClientAccessException(Throwable cause) {
        super(CLIENT_INIT_EXCEPTION, cause);
    }
}


