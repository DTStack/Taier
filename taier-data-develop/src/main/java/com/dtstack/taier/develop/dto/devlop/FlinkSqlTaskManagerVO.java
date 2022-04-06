package com.dtstack.taier.develop.dto.devlop;


/**
 * @company: www.dtstack.com
 * @Author ：qianyi
 */
public class FlinkSqlTaskManagerVO {

    /**
     * taskManager ID
     */
    private String id;

    /**
     * taskManager路径
     */
    private String path;

    /**
     * taskManager端口号
     */
    private Integer dataPort;

    /**
     * 距离上次心跳的时间
     */
    private Long timeSinceLastHeartbeat = 0L;

    /**
     * taskManager总槽
     */
    private Integer slotsNumber;

    /**
     * taskManager闲置槽
     */
    private Integer freeSlots;

    /**
     * 日志地址
     */
    private String downLoadLog;

    /**
     * 日志总字节数
     */
    private Integer totalBytes = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getDataPort() {
        return dataPort;
    }

    public void setDataPort(Integer dataPort) {
        this.dataPort = dataPort;
    }

    public Long getTimeSinceLastHeartbeat() {
        return timeSinceLastHeartbeat;
    }

    public void setTimeSinceLastHeartbeat(Long timeSinceLastHeartbeat) {
        this.timeSinceLastHeartbeat = timeSinceLastHeartbeat;
    }

    public Integer getSlotsNumber() {
        return slotsNumber;
    }

    public void setSlotsNumber(Integer slotsNumber) {
        this.slotsNumber = slotsNumber;
    }

    public Integer getFreeSlots() {
        return freeSlots;
    }

    public void setFreeSlots(Integer freeSlots) {
        this.freeSlots = freeSlots;
    }

    public String getDownLoadLog() {
        return downLoadLog;
    }

    public void setDownLoadLog(String downLoadLog) {
        this.downLoadLog = downLoadLog;
    }

    public Integer getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(Integer totalBytes) {
        this.totalBytes = totalBytes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 日志名称 taskmanager.log | taskmanager.out | taskmanager.err
     */
    private String name;

}
