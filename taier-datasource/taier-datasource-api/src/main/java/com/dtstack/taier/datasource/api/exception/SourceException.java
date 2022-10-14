package com.dtstack.taier.datasource.api.exception;

/**
 * datasource 内部异常
 *
 * @author ：nanqi
 * date：Created in 20:15 2022/9/23
 * company: www.dtstack.com
 */
public class SourceException extends RuntimeException {

    public SourceException(String errorMessage) {
        super(errorMessage);
    }

    public SourceException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
