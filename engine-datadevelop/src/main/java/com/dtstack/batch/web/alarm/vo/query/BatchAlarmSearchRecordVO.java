package com.dtstack.batch.web.alarm.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("告警记录查询信息")
public class BatchAlarmSearchRecordVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "任务名称", example = "taskName", required = true)
    private String taskName;

    @ApiModelProperty(value = "接收人id", example = "1", required = true)
    private Long receive;

    @ApiModelProperty(value = "告警id列表", required = true)
    private List<Long> alarmIds;

    @ApiModelProperty(value = "任务id列表", required = true)
    private List<Long> taskIds;

    @ApiModelProperty(value = "是否按时间降序排序", example = "true")
    private Boolean isTimeSortDesc = true;

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "页面大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "开始时间", example = "1609472875000", required = true)
    private Long startTime;

    @ApiModelProperty(value = "结束时间", example = "1609991275000", required = true)
    private Long endTime;
}
