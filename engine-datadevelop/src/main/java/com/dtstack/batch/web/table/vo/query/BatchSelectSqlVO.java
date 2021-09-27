package com.dtstack.batch.web.table.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("执行选中的sql或者脚本")
public class BatchSelectSqlVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "dtToken", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "工作任务 ID", example = "3", required = true)
    private String jobId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "类别", example = "2", required = true)
    private Integer type;

    @ApiModelProperty(value = "SQL ID", example = "5", required = true)
    private String sqlId;

    @ApiModelProperty(value = "是否需要结果 默认是false", example = "false", required = true)
    private Boolean needResult = false;
}
