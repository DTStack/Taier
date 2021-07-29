package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("批量重新启动信息")
public class BatchJobBatchRestartJobAndResumeVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务实例ID列表", required = true)
    private List<Object> jobIdList;

    @ApiModelProperty(value = "是否只跑子结点", example = "false", required = true)
    private Boolean runCurrentJob;
}
