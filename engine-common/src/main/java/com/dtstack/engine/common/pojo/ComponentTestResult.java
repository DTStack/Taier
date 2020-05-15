package com.dtstack.engine.common.pojo;

import com.alibaba.fastjson.JSONObject;

/**
 * @author yuebai
 * @date 2020-05-15
 */
public class ComponentTestResult {
    private int componentTypeCode;

    private boolean result;

    private String errorMsg;

    public int getComponentTypeCode() {
        return componentTypeCode;
    }

    public void setComponentTypeCode(int componentTypeCode) {
        this.componentTypeCode = componentTypeCode;
    }

    public boolean getResult() {
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

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
