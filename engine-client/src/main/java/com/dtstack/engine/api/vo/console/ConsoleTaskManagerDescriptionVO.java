package com.dtstack.engine.api.vo.console;

/**
 * @Auther: dazhi
 * @Date: 2020/7/30 2:44 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConsoleTaskManagerDescriptionVO {

    private String path;
    private int dataPort;
    private String id;
    private int freeSlots;
    private int cpuCores;
    private int slotsNumber;
    private long managedMemory;
    private long freeMemory;
    private long physicalMemory;

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
