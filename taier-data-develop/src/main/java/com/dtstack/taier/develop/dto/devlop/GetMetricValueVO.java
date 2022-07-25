package com.dtstack.taier.develop.dto.devlop;

import io.swagger.annotations.ApiModelProperty;


/**
 * 获取指标详细信息
 *
 * @author ：wangchuan
 * date：Created in 上午11:05 2021/4/16
 * company: www.dtstack.com
 */
public class GetMetricValueVO {

    @ApiModelProperty(value = "UIC 租户 id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "结束时间", example = "1618976997027", required = true)
    private Long end;

    @ApiModelProperty(value = "时间跨度", example = "1m", required = true)
    private String timespan;

    @ApiModelProperty(value = "指标名称", example = "flink_taskmanager_job_task_operator_dtNumRecordsInRate", required = true)
    private String chartName;

    public Long getDtuicTenantId() {
        return dtuicTenantId;
    }

    public void setDtuicTenantId(Long dtuicTenantId) {
        this.dtuicTenantId = dtuicTenantId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public String getTimespan() {
        return timespan;
    }

    public void setTimespan(String timespan) {
        this.timespan = timespan;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }
}
