package com.dtstack.batch.web.datamask.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("脱敏规则基础信息")
public class BatchDataMaskRuleBaseVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "脱敏规则id", example = "0", required = true)
    private Long id;

    @ApiModelProperty(value = "用户id", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目id", hidden = true)
    private Long projectId;
}
