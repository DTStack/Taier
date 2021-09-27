package com.dtstack.batch.web.project.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("将项目设置为置顶")
public class BatchProjectSetStickyVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "操作项目的 ID", example = "1", required = true)
    private Long appointProjectId;

    @ApiModelProperty(value = "指定状态", example = "1")
    private Integer stickStatus;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;
}
