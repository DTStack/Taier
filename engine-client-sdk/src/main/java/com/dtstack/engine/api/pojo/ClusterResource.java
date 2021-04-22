package com.dtstack.engine.api.pojo;

import com.alibaba.fastjson.JSONObject;

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

    /**
     * 原始调度队列信息，通过rest api获取，带有调度类型信息。调度类型有fifoScheduler、capacityScheduler、fairScheduler
     */
    private JSONObject scheduleInfo;

    public JSONObject getScheduleInfo() {
        return scheduleInfo;
    }

    public void setScheduleInfo(JSONObject scheduleInfo) {
        this.scheduleInfo = scheduleInfo;
    }

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

    public static class ResourceMetrics {

        private Double totalMem;
        private Integer totalCores;
        private Double usedMem;
        private Integer usedCores;
        private Double memRate;
        private Double coresRate;

        public Double getTotalMem() {
            return totalMem;
        }

        public void setTotalMem(Double totalMem) {
            this.totalMem = totalMem;
        }

        public Integer getTotalCores() {
            return totalCores;
        }

        public void setTotalCores(Integer totalCores) {
            this.totalCores = totalCores;
        }

        public Double getUsedMem() {
            return usedMem;
        }

        public void setUsedMem(Double usedMem) {
            this.usedMem = usedMem;
        }

        public Integer getUsedCores() {
            return usedCores;
        }

        public void setUsedCores(Integer usedCores) {
            this.usedCores = usedCores;
        }

        public Double getMemRate() {
            return memRate;
        }

        public void setMemRate(Double memRate) {
            this.memRate = memRate;
        }

        public Double getCoresRate() {
            return coresRate;
        }

        public void setCoresRate(Double coresRate) {
            this.coresRate = coresRate;
        }
    }

}