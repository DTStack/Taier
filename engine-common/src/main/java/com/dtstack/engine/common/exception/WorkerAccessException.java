package com.dtstack.engine.common.exception;

/**
 * @author toutian
 */
public class WorkerAccessException extends RuntimeException {

    private static final String WORKER_ACCESS_EXCEPTION = "not find available worker, ";

    public WorkerAccessException(String msg) {
        super(WORKER_ACCESS_EXCEPTION + msg);
    }
}


