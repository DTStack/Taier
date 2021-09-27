package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取任务状态信息")
public class BatchJobGetJobStatusVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务实例Id", example = "1", required = true)
    private String jobId;
}
