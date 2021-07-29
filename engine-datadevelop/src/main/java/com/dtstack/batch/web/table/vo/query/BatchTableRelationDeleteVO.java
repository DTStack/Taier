package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表关系信息")
public class BatchTableRelationDeleteVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "相关id", example = "1", required = true)
    private Long relationId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

}
