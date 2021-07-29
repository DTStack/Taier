package com.dtstack.engine.api.domain;

public class CronExceptionVO {
    /**
     * 校验失败
     */
    public static final int CHECK_EXCEPTION = 1;
    /**
     * 超过最小执行频率
     */
    public static final int PERIOD_EXCEPTION = 2;
    public CronExceptionVO(int errCode, String errMessage) {
        this.errCode = errCode;
        this.errMessage = errMessage;
    }

    public CronExceptionVO() {
    }

    private int errCode;
    private String errMessage;

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    @Override
    public String toString() {
        return "CronExceptionVO{" +
                "errCode=" + errCode +
                ", errMessage='" + errMessage + '\'' +
                '}';
    }
}
