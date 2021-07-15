package com.dtstack.batch.web.function.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("函数引擎标识信息")
public class BatchFunctionIdentityVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "engine类型", example = "1", required = true)
    private Integer engineType;
}