package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @company: www.dtstack.com
 * @Author ：zhichen
 * @Date ：Created in 10:50 2021/9/11
 */
public class TaskDirtyDataManageQueryVO extends DtInsightAuthParam {

    @NotNull(message = "sourceId not null")
    @ApiModelProperty(value = "taskId", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "项目Id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "脏数据表")
    private List<String> sourceTableList;

    @ApiModelProperty(value = "开始时间", example = "1231242")
    private Long startDate;

    @ApiModelProperty(value = "结束时间", example = "12314214")
    private Long endDate;

    @ApiModelProperty(value = "当前页", example = "1")
    private int currentPage = 1;

    @ApiModelProperty(value = "页面大小", example = "20")
    private int pageSize = 20;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<String> getSourceTableList() {
        return sourceTableList;
    }

    public void setSourceTableList(List<String> sourceTableList) {
        this.sourceTableList = sourceTableList;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
