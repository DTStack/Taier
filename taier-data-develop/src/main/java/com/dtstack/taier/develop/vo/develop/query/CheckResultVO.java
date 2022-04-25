package com.dtstack.taier.develop.vo.develop.query;

/**
 * @author qianyi
 * @version 1.0
 * @date 2021/1/3 6:54 下午
 */
public class CheckResultVO {
    private Integer code;
    private Long space;
    private String errorMsg;
    private String data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getSpace() {
        return space;
    }

    public void setSpace(Long space) {
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

