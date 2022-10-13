package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 2:13 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class QueryJobLogVO {

    /**
     * 任务实例ID
     */
    @ApiModelProperty(value = "任务实例ID", example = "1", required = true)
    @NotNull
    private String jobId;

    /**
     * 页数
     */
    @ApiModelProperty(value = "页数 默认 1", example = "1")
    private Integer pageInfo;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Integer pageInfo) {
        this.pageInfo = pageInfo;
    }
}
