package com.dtstack.taier.develop.vo.datasource;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

public class CheckPointListVO {


    @NotNull(message = "application can be null")

    @ApiModelProperty(value = "applicationId")
    private String applicationId;

    @NotNull(message = "jobId can be null")
    private String jobId;

    private String engineJobId;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getEngineJobId() {
        return engineJobId;
    }

    public void setEngineJobId(String engineJobId) {
        this.engineJobId = engineJobId;
    }
}
