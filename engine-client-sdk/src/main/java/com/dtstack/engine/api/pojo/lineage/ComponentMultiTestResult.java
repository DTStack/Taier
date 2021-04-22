package com.dtstack.engine.api.pojo.lineage;

import com.dtstack.engine.api.pojo.ComponentTestResult;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xinge
 */
public class ComponentMultiTestResult {

    public ComponentMultiTestResult(int componentTypeCode){
        this.componentTypeCode = componentTypeCode;
        this.multiVersion=new ArrayList<>(2);
        this.result = true;
    }

    private int componentTypeCode;

    private boolean result;

    private List<MultiErrorMsg> errorMsg;

    private List<ComponentTestResult> multiVersion;

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

    public List<MultiErrorMsg> getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(List<MultiErrorMsg> errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<ComponentTestResult> getMultiVersion() {
        return multiVersion;
    }

    public void setMultiVersion(List<ComponentTestResult> multiVersion) {
        this.multiVersion = multiVersion;
    }

    public static class MultiErrorMsg{
        private String componentVersion;
        private String errorMsg;


        public MultiErrorMsg(String componentVersion, String errorMsg) {
            if (StringUtils.isNotBlank(componentVersion)){
                this.componentVersion = componentVersion;
            }
            this.errorMsg = errorMsg;
        }

        public String getComponentVersion() {
            return componentVersion;
        }

        public void setComponentVersion(String componentVersion) {
            this.componentVersion = componentVersion;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return "ErrorMsg{" +
                    "componentVersion='" + componentVersion + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    '}';
        }
    }
}
