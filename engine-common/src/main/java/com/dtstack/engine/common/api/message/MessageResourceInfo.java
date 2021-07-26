package com.dtstack.engine.common.api.message;

import java.io.Serializable;

public class MessageResourceInfo implements Serializable {
    private static final long serialVersionUID = 8601041487209259707L;
    private String engineType;
    private String pluginInfo;

    public MessageResourceInfo(String engineType, String pluginInfo) {
        this.engineType = engineType;
        this.pluginInfo = pluginInfo;
    }
    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }
}
