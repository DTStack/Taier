package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据实例Id获取任务信息")
public class BatchJobFindTaskRuleJobVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务实例Id", example = "1", required = true)
    private String jobId;

}
