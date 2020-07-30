package com.dtstack.engine.common.pojo;

public class JudgeResult {

    private Boolean result;
    private String reason;

    public JudgeResult() {
    }

    public JudgeResult(Boolean result, String reason) {
        this.result = result;
        this.reason = reason;
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
