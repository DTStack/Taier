package com.dtstack.engine.common.pojo;

import com.alibaba.fastjson.JSONObject;
import scala.Int;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-19
 */
public class ClusterResource implements Serializable {

    private ResourceMetrics resourceMetrics;

    private List<NodeDescription> nodes = new ArrayList<>();

    private List<JSONObject> queues;

    public ResourceMetrics getResourceMetrics() {
        return resourceMetrics;
    }

    public void setResourceMetrics(ResourceMetrics resourceMetrics) {
        this.resourceMetrics = resourceMetrics;
    }

    public List<JSONObject> getQueues() {
        return queues;
    }

    public void setQueues(List<JSONObject> queues) {
        this.queues = queues;
    }

    public List<NodeDescription> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeDescription> nodes) {
        this.nodes = nodes;
    }

    public static class NodeDescription {
        private String nodeName;
        private int memory;
        private int virtualCores;
        private int usedMemory;
        private int usedVirtualCores;

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public int getMemory() {
            return memory;
        }

        public void setMemory(int memory) {
            this.memory = memory;
        }

        public int getVirtualCores() {
            return virtualCores;
        }

        public void setVirtualCores(int virtualCores) {
            this.virtualCores = virtualCores;
        }

        public int getUsedMemory() {
            return usedMemory;
        }

        public void setUsedMemory(int usedMemory) {
            this.usedMemory = usedMemory;
        }

        public int getUsedVirtualCores() {
            return usedVirtualCores;
        }

        public void setUsedVirtualCores(int usedVirtualCores) {
            this.usedVirtualCores = usedVirtualCores;
        }
    }

    public static class QueueDescription {
        private String queueName;
        private Double usedCapacity;
        private Double configuredCapacity;
        private Double maxCapacity;
        private Resource usedResources;
        private Resource usedAMResource;
        private Resource amResourceLimit;


    }

    public static class ResourceMetrics {
        private Integer totalMem;
        private Integer totalCores;
        private Integer usedMem;
        private Integer usedCores;

        public Integer getTotalMem() {
            return totalMem;
        }

        public void setTotalMem(Integer totalMem) {
            this.totalMem = totalMem;
        }

        public Integer getTotalCores() {
            return totalCores;
        }

        public void setTotalCores(Integer totalCores) {
            this.totalCores = totalCores;
        }

        public Integer getUsedMem() {
            return usedMem;
        }

        public void setUsedMem(Integer usedMem) {
            this.usedMem = usedMem;
        }

        public Integer getUsedCores() {
            return usedCores;
        }

        public void setUsedCores(Integer usedCores) {
            this.usedCores = usedCores;
        }
    }

    public static class Resource {
        private Integer memory;
        private Integer core;

        public Integer getMemory() {
            return memory;
        }

        public void setMemory(Integer memory) {
            this.memory = memory;
        }

        public Integer getCore() {
            return core;
        }

        public void setCore(Integer core) {
            this.core = core;
        }
    }

}