package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("获取任务实例信息")
public class BatchJobGetJobByIdVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务实例Id", example = "1", required = true)
    private Long jobId;

}
