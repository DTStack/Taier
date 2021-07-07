package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskGetSupportJobTypesVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;
}
