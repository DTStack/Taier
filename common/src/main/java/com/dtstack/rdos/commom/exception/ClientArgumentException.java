package com.dtstack.rdos.commom.exception;

/**
 * @author toutian
 */
public class ClientArgumentException extends RuntimeException {

    private static final String CLIENT_ARGUMENT_EXCEPTION = "Client argument exception. ";

    public ClientArgumentException(Throwable cause) {
        super(CLIENT_ARGUMENT_EXCEPTION, cause);
    }
}


