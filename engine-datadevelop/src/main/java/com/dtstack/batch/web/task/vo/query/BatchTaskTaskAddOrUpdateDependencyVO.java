package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务依赖的任务信息")
public class BatchTaskTaskAddOrUpdateDependencyVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(value = "项目 ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "uic 租户 ID", example = "1")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "产品类型", example = "1")
    private Integer appType;

}
