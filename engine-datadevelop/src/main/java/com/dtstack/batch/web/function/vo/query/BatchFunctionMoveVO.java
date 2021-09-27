package com.dtstack.batch.web.function.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("函数移动信息")
public class BatchFunctionMoveVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "函数id", example = "1", required = true)
    private Long functionId;

    @ApiModelProperty(value = "父文件夹id", example = "1", required = true)
    private Long nodePid;

    @ApiModelProperty(value = "dtuic租户id", hidden = true)
    private Long dtuicTenantId;
}
