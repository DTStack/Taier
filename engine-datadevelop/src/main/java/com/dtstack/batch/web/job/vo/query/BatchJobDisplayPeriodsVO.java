package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("显示周期信息")
public class BatchJobDisplayPeriodsVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "isAfter", example = "false")
    private Boolean isAfter = false;

    @ApiModelProperty(value = "任务实例Id", example = "abcd1234", required = true)
    private Long jobId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "限制", example = "1", required = true)
    private Integer limit;
}
