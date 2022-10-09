package com.dtstack.taier.datasource.api.exception;

/**
 * 自定义异常
 *
 * @author ：wangchuan
 * date：Created in 20:15 2022/9/23
 * company: www.dtstack.com
 */
public class CustomException extends RuntimeException {

    public CustomException(String message, Throwable e) {
        super(message, e);
    }
}
