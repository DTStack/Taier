package com.dtstack.engine.common.akka.message;

import java.io.Serializable;

public class MessageGetEngineMessageByHttp implements Serializable {
    private static final long serialVersionUID = 1L;

    private String engineType;
    private String pluginInfo;
    private String path;

    public MessageGetEngineMessageByHttp(String engineType, String path, String pluginInfo){
        this.engineType = engineType;
        this.pluginInfo = pluginInfo;
        this.path = path;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
