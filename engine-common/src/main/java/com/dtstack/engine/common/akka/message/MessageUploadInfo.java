package com.dtstack.engine.common.akka.message;

import java.io.Serializable;

public class MessageUploadInfo implements Serializable {
    private String engineType;
    private String pluginInfo;
    private String bytes;
    private String hdfsPath;

    public MessageUploadInfo(String engineType, String pluginInfo, String bytes, String hdfsPath) {
        this.engineType = engineType;
        this.pluginInfo = pluginInfo;
        this.bytes = bytes;
        this.hdfsPath = hdfsPath;
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

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }
}
