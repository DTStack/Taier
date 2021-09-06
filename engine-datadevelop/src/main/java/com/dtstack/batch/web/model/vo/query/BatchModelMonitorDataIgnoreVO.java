package com.dtstack.batch.web.model.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("忽略数据监视模型信息")
public class BatchModelMonitorDataIgnoreVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "类型", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "ID", example = "1", required = true)
    private Long id;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
