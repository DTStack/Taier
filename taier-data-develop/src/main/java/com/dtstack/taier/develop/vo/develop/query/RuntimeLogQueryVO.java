package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

public class RuntimeLogQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务ID", example = "1")
    private Long taskId;

    @ApiModelProperty(value = "日志起始地址", example = "0")
    private Integer place;

    @ApiModelProperty(value = "任务管理节点ID", example = "1")
    private String taskManagerId;

    @ApiModelProperty(value = "当前页", example = "0")
    private Integer currentPage;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

    public String getTaskManagerId() {
        return taskManagerId;
    }

    public void setTaskManagerId(String taskManagerId) {
        this.taskManagerId = taskManagerId;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

}
