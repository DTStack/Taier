package com.dtstack.batch.web.dirtydata.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脏数据原因分析信息")
public class BatchDirtyDataReasonVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "分区id", example = "1")
    private Long partId;

    @ApiModelProperty(value = "错误类型", example = "npe")
    private String errorType;

    @ApiModelProperty(value = "限制数量", example = "1")
    private Integer limit;

    @ApiModelProperty(value = "是否为root", example = "false")
    private Boolean isRoot;

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;
}
