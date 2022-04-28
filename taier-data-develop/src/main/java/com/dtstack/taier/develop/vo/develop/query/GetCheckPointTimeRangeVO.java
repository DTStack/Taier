package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 15:12 2020/10/12
 * @Description：任务快照查询视图
 */
public class GetCheckPointTimeRangeVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true, value = "项目ID", required = true, example = "11")
    private Long projectId;

    @NotNull(message = "jobId not null")
    @ApiModelProperty(value = "任务 ID", required = true, example = "11")
    private String jobId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
