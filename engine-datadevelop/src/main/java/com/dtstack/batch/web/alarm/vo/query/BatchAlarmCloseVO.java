package com.dtstack.batch.web.alarm.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警开启/关闭信息")
public class BatchAlarmCloseVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "告警id", example = "1", required = true)
    private Long alarmId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
