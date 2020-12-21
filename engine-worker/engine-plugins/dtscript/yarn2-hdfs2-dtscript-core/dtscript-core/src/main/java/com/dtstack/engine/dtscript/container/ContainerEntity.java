package com.dtstack.engine.dtscript.container;


import com.dtstack.engine.dtscript.common.DtContainerStatus;

public class ContainerEntity {
    private DtContainerId containerId;
    private volatile DtContainerStatus dtContainerStatus;
    private int attempts;
    private String nodeHost;
    private int nodePort;
    private volatile Long lastBeatTime;


    public ContainerEntity(DtContainerId containerId, DtContainerStatus dtContainerStatus, String nodeHost, int nodePort) {
        this.containerId = containerId;
        this.dtContainerStatus = dtContainerStatus;
        this.nodeHost = nodeHost;
        this.nodePort = nodePort;
        this.lastBeatTime = System.currentTimeMillis();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ContainerEntity that = (ContainerEntity) o;
        return containerId != null ? containerId.equals(that.containerId) : that.containerId == null;
    }

    @Override
    public int hashCode() {
        return containerId.hashCode();
    }

    @Override
    public String toString() {
        return "ContainerEntity{" +
                ", containerId=" + containerId +
                ", dtContainerStatus=" + dtContainerStatus +
                ", nodeHost='" + nodeHost + '\'' +
                ", nodePort=" + nodePort +
                ", lastBeatTime=" + lastBeatTime +
                '}';
    }
}