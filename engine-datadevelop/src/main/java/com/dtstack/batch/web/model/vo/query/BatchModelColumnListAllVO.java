package com.dtstack.batch.web.model.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询全部信息")
public class BatchModelColumnListAllVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
