package com.dtstack.batch.web.script.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("停止sql脚本信息")
public class BatchScriptStopSqlVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "脚本ID", example = "3", required = true)
    private Long scriptId;

    @ApiModelProperty(value = "job ID", example = "3", required = true)
    private String jobId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;
}
