package com.dtstack.taiga.develop.model.system.config;

import com.alibaba.fastjson.JSONObject;

public class ComponentModelExtraParameters {

    private String versionName;

    private JSONObject parametersConfig;

    public ComponentModelExtraParameters(String versionName, String componentModelConfig) {
        this.versionName = versionName;
        this.parametersConfig = JSONObject.parseObject(componentModelConfig);
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public JSONObject getComponentModelConfig() {
        return parametersConfig;
    }

    public void setComponentModelConfig(JSONObject componentModelConfig) {
        this.parametersConfig = componentModelConfig;
    }
}

