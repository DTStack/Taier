package com.dtstack.engine.api.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ClusterResourceDescription;
import io.swagger.annotations.ApiModel;

import java.util.List;
@ApiModel
public class TestConnectionVO {

    private ClusterResourceDescription description;

    private List<ComponentTestResult> testResults;

    public ClusterResourceDescription getDescription() {
        return description;
    }

    public static TestConnectionVO EMPTY_RESULT = new TestConnectionVO();

    public void setDescription(ClusterResourceDescription description) {
        this.description = description;
    }

    public List<ComponentTestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<ComponentTestResult> testResults) {
        this.testResults = testResults;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public static class ComponentTestResult{

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
}

