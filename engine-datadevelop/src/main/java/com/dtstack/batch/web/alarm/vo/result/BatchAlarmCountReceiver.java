package com.dtstack.batch.web.alarm.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警用户信息")
public class BatchAlarmCountReceiver {
    @ApiModelProperty(value = "用户id", example = "1")
    private Long userId;

    @ApiModelProperty(value = "用户名", example = "admin")
    private String userName;
}
