package com.dtstack.batch.web.alarm.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警查询信息")
public class BatchAlarmSearchVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageIndex = 1;

    @ApiModelProperty(value = "页面大小", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "任务名称", example = "taskName", required = true)
    private String taskName;

    @ApiModelProperty(value = "所属用户id", example = "0", required = true)
    private Long ownerId = 0L;

    @ApiModelProperty(value = "告警状态", example = "1", required = true)
    private Integer alarmStatus = -1;

    @ApiModelProperty(value = "是否按时间降序排序", example = "true")
    private Boolean isTimeSortDesc = true;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;
}
