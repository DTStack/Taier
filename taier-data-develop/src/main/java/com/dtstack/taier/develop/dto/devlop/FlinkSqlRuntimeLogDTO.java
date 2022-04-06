package com.dtstack.taier.develop.dto.devlop;


import java.io.Serializable;
import java.util.List;
import java.util.Map;


public class FlinkSqlRuntimeLogDTO implements Serializable {

    /**
     * 日志类型:两种 taskmanager | jobmanager
     */
    private String typeName;

    /**
     * 日志滚动路径等信息
     */
    private List<Map<String, Object>> logs;

    /**
     * 日志类型如果是 taskmanager会有这个参数，用于获取taskmanager相关信息
     */
    private String otherInfo;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<Map<String, Object>> getLogs() {
        return logs;
    }

    public void setLogs(List<Map<String, Object>> logs) {
        this.logs = logs;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }


}