package com.dtstack.engine.api.pojo;

import java.io.Serializable;

/**
 * @author haier
 * @Description 语法检测结果
 * @date 2021/3/9 11:33 上午
 */
public class CheckResult implements Serializable {
    private boolean result;
    private String errorMsg;

    public static CheckResult success() {
        CheckResult checkResult = new CheckResult();
        checkResult.setResult(true);
        return checkResult;
    }

    public static CheckResult exception(String msg) {
        CheckResult checkResult = new CheckResult();
        checkResult.setResult(false);
        checkResult.setErrorMsg(msg);
        return checkResult;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
