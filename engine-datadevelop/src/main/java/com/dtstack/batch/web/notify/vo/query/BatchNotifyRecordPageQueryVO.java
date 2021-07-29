package com.dtstack.batch.web.notify.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("通知记录分页信息")
public class BatchNotifyRecordPageQueryVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "总页数", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "1：普通查询，2：未读消息，3：已读消息", example = "1", required = true)
    private Integer mode;
}
