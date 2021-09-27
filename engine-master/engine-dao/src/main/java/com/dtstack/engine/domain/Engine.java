package com.dtstack.engine.domain;

import io.swagger.annotations.ApiModel;

@ApiModel
public class Engine extends BaseEntity{

    private Long clusterId;

    private String engineName;

    private int engineType;

    private int totalNode;

    private int totalMemory;

    private int totalCore;

    private Integer syncType;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public int getEngineType() {
        return engineType;
    }

    public void setEngineType(int engineType) {
        this.engineType = engineType;
    }

    public int getTotalNode() {
        return totalNode;
    }

    public void setTotalNode(int totalNode) {
        this.totalNode = totalNode;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getTotalCore() {
        return totalCore;
    }

    public void setTotalCore(int totalCore) {
        this.totalCore = totalCore;
    }

    public Integer getSyncType() {
        return syncType;
    }

    public void setSyncType(Integer syncType) {
        this.syncType = syncType;
    }
}
