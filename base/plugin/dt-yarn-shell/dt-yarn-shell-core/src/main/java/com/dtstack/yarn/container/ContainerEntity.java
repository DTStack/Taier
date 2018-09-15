package com.dtstack.yarn.container;


import com.dtstack.yarn.common.DtContainerStatus;

public class ContainerEntity {
    private int lane;
    private DtContainerId containerId;
    private DtContainerStatus dtContainerStatus;
    private int attempts;

    public ContainerEntity(DtContainerId containerId, DtContainerStatus dtContainerStatus, int attempts) {
        this.containerId = containerId;
        this.dtContainerStatus = dtContainerStatus;
        this.attempts = attempts;
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
}
