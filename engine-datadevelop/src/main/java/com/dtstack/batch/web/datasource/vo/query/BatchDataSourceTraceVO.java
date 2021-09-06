package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据源追踪信息")
public class BatchDataSourceTraceVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long taskId;
}
