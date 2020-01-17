package com.dtstack.task.server.vo;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/25
 */
public class BatchOperatorVO<T> {

    private int successNum;

    private int failNum;

    private T detail;

    public int getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(int successNum) {
        this.successNum = successNum;
    }

    public int getFailNum() {
        return failNum;
    }

    public void setFailNum(int failNum) {
        this.failNum = failNum;
    }

    public T getDetail() {
        return detail;
    }

    public void setDetail(T detail) {
        this.detail = detail;
    }
}
