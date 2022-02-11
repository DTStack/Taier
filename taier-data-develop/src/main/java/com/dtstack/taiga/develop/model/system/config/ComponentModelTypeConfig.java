package com.dtstack.taiga.develop.model.system.config;

import com.alibaba.fastjson.JSONObject;

public class ComponentModelTypeConfig {

    private String versionName;

    private JSONObject componentModelConfig;

    public ComponentModelTypeConfig(String versionName, String componentModelConfig) {
        this.versionName = versionName;
        this.componentModelConfig = JSONObject.parseObject(componentModelConfig);
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public JSONObject getComponentModelConfig() {
        return componentModelConfig;
    }

    public void setComponentModelConfig(JSONObject componentModelConfig) {
        this.componentModelConfig = componentModelConfig;
    }
}

