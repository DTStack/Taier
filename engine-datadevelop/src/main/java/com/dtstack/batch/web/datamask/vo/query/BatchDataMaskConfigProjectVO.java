package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏获取项目信息")
public class BatchDataMaskConfigProjectVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "脱敏id", example = "1", required = true)
    private Long configId;
}
