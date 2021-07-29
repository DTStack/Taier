package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("为工作流节点展开子节点信息")
public class BatchJobJobSpringWorkFlowVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "任务实例ID", example = "100", required = true)
    private Long jobId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;
}

