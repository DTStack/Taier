package com.dtstack.taier.develop.vo.develop.query;

import io.swagger.annotations.ApiModelProperty;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author zhiChen
 * @date 2021/1/7 19:44
 * @see
 */
public class GetTaskMetricsVO {

    @ApiModelProperty(value = "任务ID", example = "111", required = true)
    private Long taskId;

    @ApiModelProperty(value = "结束时间", example = "2021-04-15 19:53:02", required = true)
    private Timestamp end;

    @ApiModelProperty(value = "时间跨度", example = "1m", required = true)
    private String timespan;

    @ApiModelProperty(value = "图表名称列表", required = true)
    private List<String> chartNames;

}
