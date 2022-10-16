package com.dtstack.taier.pluginapi.pojo;

import java.util.List;

public class ClusterResourceDescription {
    private final int totalNode;
    private final int totalMemory;
    private final int totalCores;
    private final List<QueueDescription> queueDescriptions;

    public ClusterResourceDescription(int totalNode, int totalMemory, int totalCores, List<QueueDescription> descriptions) {
        this.totalNode = totalNode;
        this.totalMemory = totalMemory;
        this.totalCores = totalCores;
        this.queueDescriptions = descriptions;
    }

    public int getTotalNode() {
        return totalNode;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public int getTotalCores() {
        return totalCores;
    }

    public List<QueueDescription> getQueueDescriptions() {
        return queueDescriptions;
    }

}

