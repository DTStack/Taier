package com.dtstack.engine.common.exception;

/**
 * @author toutian
 */
public class LimitResourceException extends RuntimeException {

    private static final String LIMIT_RESOURCE_ERROR = "LIMIT RESOURCE ERROR:";

    public LimitResourceException(String msg) {
        super(LIMIT_RESOURCE_ERROR + msg);
    }
}


