package com.dtstack.engine.api.pojo;

/**
 * @author haier
 * @Description 语法检测结果
 * @date 2021/3/9 11:33 上午
 */
public class CheckResult {
    private int code;
    private long space;
    private String errorMsg;
    private String data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
