package com.dtstack.engine.datadevelop.param;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("基础服务入参基类")
public class PubSvcBaseParam extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private String dtToken;

}
