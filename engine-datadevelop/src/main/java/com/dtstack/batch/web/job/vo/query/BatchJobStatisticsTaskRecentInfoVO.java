package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("运行报告信息")
public class BatchJobStatisticsTaskRecentInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务Id", example = "1L", required = true)
    private Long taskId;

    @ApiModelProperty(value = "数量", example = "0", required = true)
    private Integer count;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
