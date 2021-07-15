package com.dtstack.batch.web.apply.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("申请检查集群信息")
public class BatchApplyCheckClusterInfoVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "资源id", example = "1", required = true)
    private Long resourceId;

    @ApiModelProperty(value = "租户id", example = "1", required = true)
    private Long tenantId;
}
