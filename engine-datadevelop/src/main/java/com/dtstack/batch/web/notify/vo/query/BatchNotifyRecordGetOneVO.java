package com.dtstack.batch.web.notify.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取通知记录信息")
public class BatchNotifyRecordGetOneVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "通知记录ID", example = "1", required = true)
    private Long notifyRecordId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
