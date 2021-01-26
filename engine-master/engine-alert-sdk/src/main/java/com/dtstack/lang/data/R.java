package com.dtstack.lang.data;

import com.dtstack.lang.base.Strings;

/**
 * Date: 2020/8/6
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class R<T> {
    public static final String DEFAULT_MESSAGE_SCUESSS = "执行成功";
    public static final String DEFAULT_MESSAGE_FAIL = "执行失败";

    private boolean success;
    private String message;
    private T data;

    public R(){
        this.success = true;
    }
    private R(boolean success) {
        this.success = success;
    }

    public static <T> R<T> ok() {
        return new R(true).setMessage(DEFAULT_MESSAGE_SCUESSS);
    }

    public static <T> R<T> ok(T data) {
        return new R(true).setMessage(DEFAULT_MESSAGE_SCUESSS).setData(data);
    }

    public static <T> R<T> fail() {
        return new R(false).setMessage(DEFAULT_MESSAGE_FAIL);
    }

    public static <T> R<T> fail(String message) {
        return new R(false).setMessage(message);
    }


    public R<T> setData(T data) {
        this.data = data;
        return this;
    }

    public R<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean hasMessage() {
        return Strings.isNotBlank(message);
    }

    public String getMessage() {
        return message;
    }

    public boolean hasData() {
        return com.dtstack.lang.Langs.isNotNull(data);
    }

    public T getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

