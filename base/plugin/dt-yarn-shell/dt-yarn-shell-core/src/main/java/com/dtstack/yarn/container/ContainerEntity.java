package com.dtstack.yarn.container;


import com.dtstack.yarn.common.DtContainerStatus;

public class ContainerEntity {
    private int  lane;
    private DtContainerId containerId;
    private DtContainerStatus dtContainerStatus;
    private int attempts;
    private String nodeHost;
    private Long lastBeatTime;


    public ContainerEntity(int lane, DtContainerId containerId, DtContainerStatus dtContainerStatus, String nodeHost, int attempts) {
        this.lane = lane;
        this.containerId = containerId;
        this.dtContainerStatus = dtContainerStatus;
        this.nodeHost = nodeHost;
        this.attempts = attempts;
        this.lastBeatTime = System.currentTimeMillis();
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public DtContainerId getContainerId() {
        return containerId;
    }

    public void setContainerId(DtContainerId containerId) {
        this.containerId = containerId;
    }

    public DtContainerStatus getDtContainerStatus() {
        return dtContainerStatus;
    }

    public void setDtContainerStatus(DtContainerStatus dtContainerStatus) {
        this.dtContainerStatus = dtContainerStatus;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getNodeHost() {
        return nodeHost;
    }

    public void setNodeHost(String nodeHost) {
        this.nodeHost = nodeHost;
    }

    public Long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(Long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

}