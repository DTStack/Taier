package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警类型结果信息")
public class BatchAlarmTypeListResultVO {
    @ApiModelProperty(value = "告警通道标识", example = "test")
    private String alertGateSource;

    @ApiModelProperty(value = "告警通道类型", example = "1")
    private Integer alertGateType;

    @ApiModelProperty(value = "告警通道名称", example = "name")
    private String alertGateName;
}
