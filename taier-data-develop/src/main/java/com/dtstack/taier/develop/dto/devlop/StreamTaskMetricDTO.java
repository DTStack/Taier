package com.dtstack.taier.develop.dto.devlop;


import java.sql.Timestamp;
import java.util.List;

public class StreamTaskMetricDTO {

    private Long tenantId;

    private Long taskId;

    private Timestamp end;

    private String timespan;

    private List<String> chartNames;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }

    public List<String> getChartNames() {
        return chartNames;
    }

    public void setChartNames(List<String> chartNames) {
        this.chartNames = chartNames;
    }
}
