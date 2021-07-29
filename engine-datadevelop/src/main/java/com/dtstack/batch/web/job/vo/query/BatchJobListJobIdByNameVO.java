package com.dtstack.batch.web.job.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("根据任务名称和状态列表得到实例Id信息")
public class BatchJobListJobIdByNameVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "任务名称", example = "我是名称", required = true)
    private String taskName;

    @ApiModelProperty(value = "状态列表", required = true)
    private List<Integer> statusList;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务实例Id列表", required = true)
    private List<String> jobIdList;
}
