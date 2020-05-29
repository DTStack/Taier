package com.dtstack.engine.common.akka.message;

import java.io.Serializable;

/**
 * @author yuebai
 * @date 2020-05-14
 */
public class MessageTestConnectInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String engineType;
    private String pluginInfo;

    public MessageTestConnectInfo(String engineType, String pluginInfo) {
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

