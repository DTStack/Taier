package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModelProperty;


public class GetCheckPointTimeRangeResultVO {
    @ApiModelProperty(value = "开始时间", example = "1612340806000")
    private Long startTime;

    @ApiModelProperty(value = "结束时间", example = "1612340806000")
    private Long endTime;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
