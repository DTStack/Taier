package com.dtstack.engine.dtscript.common;

/**
 * @Auther: jiangjunjie
 * @Date: 2019-05-13 11:20
 * @Description:
 */
public class ReturnValue {
    private int exitValue;
    private String errorLog;

    public ReturnValue(int exitValue, String errorLog){
        this.exitValue = exitValue;
        this.errorLog = errorLog;
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getErrorLog() {
        return errorLog;
    }

}
