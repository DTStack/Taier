package com.dtstack.batch.web.job.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("实例运行时间排行榜信息")
public class BatchJobRunTimeTopVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "开始时间", example = "1609084800", required = true)
    private Long startTime;

    @ApiModelProperty(value = "结束时间", example = "1609084800", required = true)
    private Long endTime;
}
