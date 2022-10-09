package com.dtstack.taier.datasource.api.exception;

/**
 * 环境初始化异常
 *
 * @author ：wangchuan
 * date：Created in 20:15 2022/9/23
 * company: www.dtstack.com
 */
public class InitializeException extends RuntimeException {

    public InitializeException(String message) {
        super(message);
    }
}
