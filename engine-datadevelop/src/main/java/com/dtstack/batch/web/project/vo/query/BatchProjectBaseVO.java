package com.dtstack.batch.web.project.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("项目基础信息")
public class BatchProjectBaseVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "查询的租户 ID", required = true)
    private Long searchTenantId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private String dtToken;

    @ApiModelProperty(hidden = true)
    private Boolean isAdmin;

    @ApiModelProperty(value = "产品类型")
    private Integer appType;

    @ApiModelProperty
    private Integer dataSourceType;
}
