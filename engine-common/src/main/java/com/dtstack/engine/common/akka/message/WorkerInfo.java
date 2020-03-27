package com.dtstack.engine.common.akka.message;

import com.dtstack.engine.common.akka.config.AkkaConfig;

import java.io.Serializable;

public class WorkerInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String ip;
    private int port;
    private String path;
    private Long timestamp;
    private String nodeLabels = AkkaConfig.getNodeLabels();
    private String systemResource;

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

    public String getNodeLabels() {
        return nodeLabels;
    }

    public void setNodeLabels(String nodeLabels) {
        this.nodeLabels = nodeLabels;
    }

    public String getSystemResource() {
        return systemResource;
    }

    public void setSystemResource(String systemResource) {
        this.systemResource = systemResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkerInfo that = (WorkerInfo) o;

        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "WorkerInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", nodeLabels=" + nodeLabels +
                ", systemResource='" + systemResource + '\'' +
                '}';
    }
}

