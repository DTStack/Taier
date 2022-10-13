package com.dtstack.taier.develop.vo.schedule;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Auther: dazhi
 * @Date: 2021/12/29 5:27 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ReturnDisplayPeriodVO {

    /**
     * 实例id
     */
    @ApiModelProperty(value = "实例id", example = "123123")
    private String jobId;

    /**
     * 计划时间
     */
    @ApiModelProperty(value = "计划时间", example = "")
    private String cycTime;

    /**
     * 实例状态
     */
    @ApiModelProperty(value = "实例状态", example = "5")
    private Integer status;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getCycTime() {
        return cycTime;
    }

    public void setCycTime(String cycTime) {
        this.cycTime = cycTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
