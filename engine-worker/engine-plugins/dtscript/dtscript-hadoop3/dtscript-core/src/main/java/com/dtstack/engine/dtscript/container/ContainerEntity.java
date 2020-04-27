package com.dtstack.engine.dtscript.container;


import com.dtstack.engine.dtscript.common.DtContainerStatus;

public class ContainerEntity {
    private int  lane;
    private DtContainerId containerId;
    private DtContainerStatus dtContainerStatus;
    private int attempts;
    private String nodeHost;
    private int nodePort;
    private Long lastBeatTime;


    public ContainerEntity(int lane, DtContainerId containerId, DtContainerStatus dtContainerStatus, String nodeHost, int nodePort, int attempts) {
        this.lane = lane;
        this.containerId = containerId;
        this.dtContainerStatus = dtContainerStatus;
        this.nodeHost = nodeHost;
        this.nodePort = nodePort;
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

    public int getNodePort() {
        return nodePort;
    }

    public void setNodePort(int nodePort) {
        this.nodePort = nodePort;
    }

    public Long getLastBeatTime() {
        return lastBeatTime;
    }

    public void setLastBeatTime(Long lastBeatTime) {
        this.lastBeatTime = lastBeatTime;
    }

    @Override
    public String toString() {
        return "ContainerEntity{" +
                "lane=" + lane +
                ", containerId=" + containerId +
                ", dtContainerStatus=" + dtContainerStatus +
                ", attempts=" + attempts +
                ", nodeHost='" + nodeHost + '\'' +
                ", nodePort=" + nodePort +
                ", lastBeatTime=" + lastBeatTime +
                '}';
    }
}