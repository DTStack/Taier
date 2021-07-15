package com.dtstack.batch.web.alarm.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("告警信息-扩展")
public class BatchAlarmUpdateVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "发送类型 1邮件 2短信", required = true)
    private List<String> senderTypes;

    @ApiModelProperty(value = "接收方式", required = true)
    private List<Integer> receiveTypes;

    @ApiModelProperty(value = "接收人id列表", required = true)
    private List<Long> receivers;

    @ApiModelProperty(value = "发送时间", example = "5:00", required = true)
    private String sendTime;

    @ApiModelProperty(value = "钉钉webhook", example = "test", required = true)
    private String webhook;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
