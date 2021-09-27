package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务依赖管理信息")
public class BatchJobJobVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "任务实例ID", example = "100", required = true)
    private Long jobId;

    @ApiModelProperty(value = "等级", example = "1", required = true)
    private Integer level;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
