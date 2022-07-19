package com.dtstack.taier.develop.vo.develop.query;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhiChen
 * @date 2021/9/16 15:59
 */
public class TaskDirtyDataManageVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "任务id")
    private Long taskId;

    @ApiModelProperty(value = "输出类型1.log2.jdbc")
    private String outputType;

    @ApiModelProperty(value = "日志打印频率")
    private Integer logPrintInterval;

    @ApiModelProperty(value = "连接信息json")
    private JSONObject linkInfo;

    @ApiModelProperty(value = "脏数据最大值")
    private Integer maxRows;

    @ApiModelProperty(value = "失败条数")
    private Integer maxCollectFailedRows;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Integer getLogPrintInterval() {
        return logPrintInterval;
    }

    public void setLogPrintInterval(Integer logPrintInterval) {
        this.logPrintInterval = logPrintInterval;
    }

    public JSONObject getLinkInfo() {
        return linkInfo;
    }

    public void setLinkInfo(JSONObject linkInfo) {
        this.linkInfo = linkInfo;
    }

    public Integer getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    public Integer getMaxCollectFailedRows() {
        return maxCollectFailedRows;
    }

    public void setMaxCollectFailedRows(Integer maxCollectFailedRows) {
        this.maxCollectFailedRows = maxCollectFailedRows;
    }
}
