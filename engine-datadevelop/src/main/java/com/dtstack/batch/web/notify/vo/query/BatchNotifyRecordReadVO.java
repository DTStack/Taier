package com.dtstack.batch.web.notify.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("消息记录读取信息")
public class BatchNotifyRecordReadVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "消息记录ID列表")
    private List<Long> notifyRecordIds;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
