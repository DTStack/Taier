package com.dtstack.engine.common.message;

import java.io.Serializable;

public class WorkerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ip;
    private int port;
    private String path;
    private Long timestamp;

    public WorkerInfo(String ip, int port, String path, Long timestamp) {
        this.ip = ip;
        this.port = port;
        this.path = path;
        this.timestamp = timestamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

