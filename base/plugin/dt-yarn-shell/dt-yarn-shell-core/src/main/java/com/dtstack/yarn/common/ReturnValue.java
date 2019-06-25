package com.dtstack.yarn.common;

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