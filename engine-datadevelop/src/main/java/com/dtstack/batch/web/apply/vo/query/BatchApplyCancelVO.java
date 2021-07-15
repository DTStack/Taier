package com.dtstack.batch.web.apply.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("申请撤销信息")
public class BatchApplyCancelVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "申请id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;
}
