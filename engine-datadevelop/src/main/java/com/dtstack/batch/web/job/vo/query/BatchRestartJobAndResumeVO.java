package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("重跑并恢复调度信息")
public class BatchRestartJobAndResumeVO extends DtInsightAuthParam {

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "任务实例Id", example = "123456", required = true)
    private Long jobId;

    @ApiModelProperty(value = "是否只跑子结点", example = "false", required = true)
    private Boolean justRunChild;

    @ApiModelProperty(value = "设置成功", example = "false", required = true)
    private Boolean setSuccess;

    @ApiModelProperty(value = "任务实例ID列表", required = true)
    private List<Long> subJobIds;
}
