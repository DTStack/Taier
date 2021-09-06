package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务详情详情")
public class BatchJobDetailVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务Id", example = "1", required = true)
    private Long taskId;
}
