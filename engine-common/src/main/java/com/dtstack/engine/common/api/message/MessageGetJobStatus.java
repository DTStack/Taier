package com.dtstack.engine.common.api.message;

import com.dtstack.engine.common.JobIdentifier;

import java.io.Serializable;

public class MessageGetJobStatus implements Serializable {
    private static final long serialVersionUID = -3798326195056826959L;
    private String engineType;
    private String pluginInfo;
    private JobIdentifier jobIdentifier;

    public MessageGetJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier){
        this.engineType = engineType;
        this.pluginInfo = pluginInfo;
        this.jobIdentifier = jobIdentifier;
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

    public JobIdentifier getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(JobIdentifier jobIdentifier) {
        this.jobIdentifier = jobIdentifier;
    }
}
