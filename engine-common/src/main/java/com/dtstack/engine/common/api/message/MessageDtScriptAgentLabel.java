package com.dtstack.engine.common.api.message;

import java.io.Serializable;

/**
 * @author xinge
 */
public class MessageDtScriptAgentLabel implements Serializable {

    public MessageDtScriptAgentLabel(String engineType, String agentAddress) {
        this.engineType = engineType;
        this.agentAddress = agentAddress;
    }

    private String agentAddress;

    private String engineType;

    public String getAgentAddress() {
        return agentAddress;
    }

    public void setAgentAddress(String agentAddress) {
        this.agentAddress = agentAddress;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }
}
