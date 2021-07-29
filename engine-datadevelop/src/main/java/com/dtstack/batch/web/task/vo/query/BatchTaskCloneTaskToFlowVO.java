package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("任务信息")
public class BatchTaskCloneTaskToFlowVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "任务 名称", example = "test", required = true)
    private String taskName;

    @ApiModelProperty(value = "任务 备注", example = "for test")
    private String taskDesc;

    @ApiModelProperty(value = "工作流 ID", example = "1")
    private Long flowId;

    @ApiModelProperty(value = "图形坐标信息")
    private Map<String, Object> coordsExtra;

}
