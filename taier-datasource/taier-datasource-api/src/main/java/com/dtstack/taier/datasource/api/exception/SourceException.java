package com.dtstack.taier.datasource.api.exception;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:01 2020/1/3
 * @Description：定义 Loader 运行时异常
 */
public class SourceException extends RuntimeException {
    private String errorMessage;

    public SourceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public SourceException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
    }
}
