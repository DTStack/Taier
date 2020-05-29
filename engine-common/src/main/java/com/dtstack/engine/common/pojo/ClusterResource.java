package com.dtstack.engine.common.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-05-19
 */
public class ClusterResource implements Serializable {

    private List<TaskManagerDescription> flink = new ArrayList<>();

    private List<NodeDescription> yarn = new ArrayList<>();

    public List<TaskManagerDescription> getFlink() {
        return flink;
    }

    public void setFlink(List<TaskManagerDescription> flink) {
        this.flink = flink;
    }

    public List<NodeDescription> getYarn() {
        return yarn;
    }

    public void setYarn(List<NodeDescription> yarn) {
        this.yarn = yarn;
    }

    public static class NodeDescription {
        private int memory;
        private int virtualCores;
        private int usedMemory;
        private int usedVirtualCores;

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


    public static class TaskManagerDescription {
        private String path;
        private int dataPort;
        private String id;
        private int freeSlots;
        private int cpuCores;
        private int slotsNumber;
        private long managedMemory;
        private long freeMemory;
        private long physicalMemory;

        public TaskManagerDescription() {
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getDataPort() {
            return dataPort;
        }

        public void setDataPort(int dataPort) {
            this.dataPort = dataPort;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getFreeSlots() {
            return freeSlots;
        }

        public void setFreeSlots(int freeSlots) {
            this.freeSlots = freeSlots;
        }

        public int getCpuCores() {
            return cpuCores;
        }

        public void setCpuCores(int cpuCores) {
            this.cpuCores = cpuCores;
        }

        public int getSlotsNumber() {
            return slotsNumber;
        }

        public void setSlotsNumber(int slotsNumber) {
            this.slotsNumber = slotsNumber;
        }

        public long getManagedMemory() {
            return managedMemory;
        }

        public void setManagedMemory(long managedMemory) {
            this.managedMemory = managedMemory;
        }

        public long getFreeMemory() {
            return freeMemory;
        }

        public void setFreeMemory(long freeMemory) {
            this.freeMemory = freeMemory;
        }

        public long getPhysicalMemory() {
            return physicalMemory;
        }

        public void setPhysicalMemory(long physicalMemory) {
            this.physicalMemory = physicalMemory;
        }
    }


}