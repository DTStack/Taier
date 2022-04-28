package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author zhiChen
 * @date 2021/1/11 20:48
 */
public class GetSavePointVO extends DtInsightAuthParam {

    @NotNull(message = "jobId not null")
    @ApiModelProperty(value = "任务 ID", required = true, example = "11")
    private String jobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
