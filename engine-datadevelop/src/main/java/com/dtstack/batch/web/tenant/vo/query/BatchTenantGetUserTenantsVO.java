package com.dtstack.batch.web.tenant.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据名称获取租户信息")
public class BatchTenantGetUserTenantsVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户名称", example = "DTStack租户", required = true)
    private String tenantName;

    @ApiModelProperty(hidden = true)
    private String dtToken;
}
