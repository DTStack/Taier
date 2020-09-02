package com.dtstack.engine.common.pojo;

import java.io.Serializable;

public class JudgeResult implements Serializable {

    private JudgeType result;
    private String reason;

    public static JudgeResult ok() {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.OK);
        return judgeResult;
    }

    public static JudgeResult notOk(String reason) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.NOT_OK);
        judgeResult.setReason(reason);
        return judgeResult;
    }

    public static JudgeResult limitError(String reason) {
        JudgeResult judgeResult = new JudgeResult();
        judgeResult.setResult(JudgeType.LIMIT_ERROR);
        judgeResult.setReason(reason);
        return judgeResult;
    }

    public boolean available() {
        return result != null && result == JudgeType.OK;
    }

    public JudgeType getResult() {
        return result;
    }

    public void setResult(JudgeType result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }


    public enum JudgeType {
        OK,
        NOT_OK,
        LIMIT_ERROR
    }
}
