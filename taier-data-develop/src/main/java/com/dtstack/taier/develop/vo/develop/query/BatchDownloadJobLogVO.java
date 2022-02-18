package com.dtstack.taier.develop.vo.develop.query;

import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("下载job日志信息")
public class BatchDownloadJobLogVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "job id", example = "79275d9f", required = true)
    private String jobId;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private Integer taskType;

    @ApiModelProperty(value = "行数", example = "1000")
    private Integer byteNum;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getByteNum() {
        return byteNum;
    }

    public void setByteNum(Integer byteNum) {
        this.byteNum = byteNum;
    }
}
