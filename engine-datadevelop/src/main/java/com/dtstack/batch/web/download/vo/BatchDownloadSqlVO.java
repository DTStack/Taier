package com.dtstack.batch.web.download.vo;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("下载sql解析信息")
public class BatchDownloadSqlVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "job id", example = "79275d9f", required = true)
    private String jobId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "是否超管用户", hidden = true)
    private Boolean isRoot;
}
