package com.dtstack.taier.develop.dto.devlop;


import com.dtstack.taier.common.param.DtInsightAuthParam;

public class ServerLogsVO extends DtInsightAuthParam {

    private Long taskId;

    private String taskManagerId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskManagerId() {
        return taskManagerId;
    }

    public void setTaskManagerId(String taskManagerId) {
        this.taskManagerId = taskManagerId;
    }


}
