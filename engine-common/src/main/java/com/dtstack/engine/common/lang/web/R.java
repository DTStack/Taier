package com.dtstack.engine.common.lang.web;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @program: DAGScheduleX
 * @description:
 * @create: 2021-12-16 16:52
 **/

import com.dtstack.engine.common.enums.ErrorEnum;
import com.dtstack.engine.common.lang.base.Strings;
import com.dtstack.engine.common.lang.i18n;

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
                .code(ErrorEnum.SUCCESS.code())
                .data(data);
    }

    public final static R<Void> empty() {
        return new R()
                .code(ErrorEnum.SUCCESS.code());
    }

    public final static <T> R<T> fail(int code, String message) {
        return new R()
                .code(code)
                .message(Strings.isNotBlank(message) ? message : i18n.error(code));
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
        return getCode() == ErrorEnum.SUCCESS.getCode();
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
