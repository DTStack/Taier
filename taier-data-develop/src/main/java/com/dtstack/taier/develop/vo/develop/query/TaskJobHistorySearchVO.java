package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

public class TaskJobHistorySearchVO extends DtInsightAuthParam {


    @ApiModelProperty(value = "任务ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer currentPage = 1;

    @ApiModelProperty(value = "页面数量", example = "20")
    private Integer pageSize = 20;

    @ApiModelProperty(value = "排序方式", example = "desc")
    private String sort = "desc";

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @ApiModelProperty(value = "根据什么字段排序", example = "applicationId")
    private String orderBy = "applicationId";

}
