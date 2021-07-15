package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据同步-增量标识信息")
public class BatchDataSourceIncreVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "任务id", example = "1", required = true)
    private Long id;
}
