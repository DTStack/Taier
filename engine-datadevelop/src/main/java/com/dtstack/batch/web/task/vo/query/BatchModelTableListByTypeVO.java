package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("表基本信息")
public class BatchModelTableListByTypeVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "类别", example = "1", required = true)
    private Integer type;

}
