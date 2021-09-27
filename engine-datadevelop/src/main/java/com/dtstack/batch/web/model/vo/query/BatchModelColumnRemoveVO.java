package com.dtstack.batch.web.model.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("移除字段模型信息")
public class BatchModelColumnRemoveVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "字段模型ID列表", required = true)
    private List<Long> ids;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long userId;
}
