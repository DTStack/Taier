package com.dtstack.engine.api.vo;


/**
 * 批量处理返回
 * Date: 2017/6/16
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public class OperatorVO<T> {

    private Integer successNum = 0;

    private Integer failNum = 0;

    private T detail;

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Integer getFailNum() {
        return failNum;
    }

    public void setFailNum(Integer failNum) {
        this.failNum = failNum;
    }

    public T getDetail() {
        return detail;
    }

    public void setDetail(T detail) {
        this.detail = detail;
    }
}
