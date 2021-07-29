package com.dtstack.batch.web.tenant.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("租户的出参信息")
public class TenantResultVO {

    @ApiModelProperty(value = "租户名称", example = "DTStack租户")
    private String tenantName;

    @ApiModelProperty(value = "租户 ID", example = "1")
    private Long tenantId;

    @ApiModelProperty(value = "是否是当前租户", example = "false")
    private Boolean current;

    @ApiModelProperty(value = "租户 UIC ID", example = "1")
    private Long uicTenantId;
}
