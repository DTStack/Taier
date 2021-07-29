package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警记录数量结果信息")
public class BatchAlarmRecordCountResultVO {
    @ApiModelProperty(value = "今天告警数量", example = "10")
    private Integer today;

    @ApiModelProperty(value = "本周告警数量", example = "50")
    private Integer week;

    @ApiModelProperty(value = "本月告警数量", example = "100")
    private Integer month;
}
