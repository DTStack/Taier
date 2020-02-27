package com.dtstack.engine.domain;

public class Queue extends BaseEntity {

    private Long engineId;
    private String queueName;

    private String capacity;
    private String maxCapacity;

    private String queueState;

    private Long parentQueueId;
    private String queuePath;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getQueueState() {
        return queueState;
    }

    public void setQueueState(String queueState) {
        this.queueState = queueState;
    }

    public Long getParentQueueId() {
        return parentQueueId;
    }

    public void setParentQueueId(Long parentQueueId) {
        this.parentQueueId = parentQueueId;
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public boolean baseEquals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Queue queue = (Queue) o;

        if (queueName != null ? !queueName.equals(queue.queueName) : queue.queueName != null) return false;
        if (capacity != null ? !capacity.equals(queue.capacity) : queue.capacity != null) return false;
        if (maxCapacity != null ? !maxCapacity.equals(queue.maxCapacity) : queue.maxCapacity != null) return false;
        if (queueState != null ? !queueState.equals(queue.queueState) : queue.queueState != null) return false;
        return queuePath != null ? queuePath.equals(queue.queuePath) : queue.queuePath == null;
    }
}
