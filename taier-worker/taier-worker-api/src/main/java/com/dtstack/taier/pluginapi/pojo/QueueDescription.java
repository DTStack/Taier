package com.dtstack.taier.pluginapi.pojo;

import java.util.List;

public class QueueDescription {
    private String queueName;
    private String queuePath;
    private String capacity;
    private String maximumCapacity;
    private String queueState;
    private List<QueueDescription> childQueues;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(String maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public String getQueueState() {
        return queueState;
    }

    public void setQueueState(String queueState) {
        this.queueState = queueState;
    }

    public List<QueueDescription> getChildQueues() {
        return childQueues;
    }

    public void setChildQueues(List<QueueDescription> childQueues) {
        this.childQueues = childQueues;
    }
}