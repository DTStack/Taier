package com.dtstack.taiga.common.lang.web;

import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.lang.i18n;
import com.dtstack.taiga.common.util.Strings;

public class R<T> {
    private static String DATA_VERSION;

    /**
     * 状态码
     */
    public int code;

    /**
     * 异常信息
     */
    public String message;

    /**
     * 返回值
     */
    private T data;

    /**
     * 请求时间
     */
    private long space;

    public final static <T> R<T> ok(T data) {
        return new R()
                .code(ErrorCode.SUCCESS.getCode())
                .data(data);
    }

    public final static R<Void> empty() {
        return new R()
                .code(ErrorCode.SUCCESS.getCode());
    }

    public final static <T> R<T> fail(int code, String message) {
        return new R()
                .code(code)
                .message(Strings.isNotBlank(message) ? message : i18n.error(code));
    }

    public final static <T> R<T> fail(ErrorCode errorCode) {
        return new R()
                .code(errorCode.getCode())
                .message(errorCode.getMsg());
    }

    public R<T> data(T data) {
        this.data = data;
        return this;
    }

    public R<T> code(int code) {
        this.code = code;
        return this;
    }

    public R<T> message(String message) {
        this.message = message;
        return this;
    }

    public R<T> space(long space) {
        this.space = space;
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    /**
     * 是否成功标志位,便于前端进行返回判断
     * @return
     */
    public boolean isSuccess() {
        return getCode() == ErrorCode.SUCCESS.getCode();
    }

    public String getVersion() {
        return R.DATA_VERSION;
    }



    /**
     * 设置接口版本
     * @param version
     */
    public static void setVersion(String version) {
        R.DATA_VERSION = version;
    }
}
