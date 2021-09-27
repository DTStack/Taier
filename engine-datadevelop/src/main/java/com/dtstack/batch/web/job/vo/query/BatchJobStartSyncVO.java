package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("运行同步任务")
public class BatchJobStartSyncVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务Id", example = "1L", required = true)
    private Long taskId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "任务参数", example = "我是参数", required = true)
    private String taskParams;
}
