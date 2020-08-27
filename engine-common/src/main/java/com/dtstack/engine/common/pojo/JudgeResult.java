package com.dtstack.engine.common.pojo;

import java.io.Serializable;

public class JudgeResult implements Serializable {

    private Boolean result;
    private String reason;

    public static JudgeResult ok() {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(true);
        return judgeResult;
    }

    public static JudgeResult notOk(Boolean result, String reason) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(result);
        judgeResult.setReason(reason);
        return judgeResult;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
