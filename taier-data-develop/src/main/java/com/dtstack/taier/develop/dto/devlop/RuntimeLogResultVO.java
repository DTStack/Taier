package com.dtstack.taier.develop.dto.devlop;

import io.swagger.annotations.ApiModelProperty;

public class RuntimeLogResultVO {

    @ApiModelProperty(value = "提交日志")
    private String submitLog;

    @ApiModelProperty(value = "engine 日志")
    private String engineLog;

    @ApiModelProperty(value = "总大小")
    private Integer totalBytes;

    @ApiModelProperty(value = "总页码")
    private Integer totalPage;

    @ApiModelProperty(value = "place 位置")
    private Integer place;

    public String getSubmitLog() {
        return submitLog;
    }

    public void setSubmitLog(String submitLog) {
        this.submitLog = submitLog;
    }

    public String getEngineLog() {
        return engineLog;
    }

    public void setEngineLog(String engineLog) {
        this.engineLog = engineLog;
    }

    public Integer getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(Integer totalBytes) {
        this.totalBytes = totalBytes;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getPlace() {
        return place;
    }

    public void setPlace(Integer place) {
        this.place = place;
    }

}