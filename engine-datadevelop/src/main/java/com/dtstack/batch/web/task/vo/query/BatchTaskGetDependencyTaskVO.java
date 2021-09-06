package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskGetDependencyTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId = 0L;

    @ApiModelProperty(value = "任务名称", example = "test")
    private String name;

    @ApiModelProperty(value = "查找项目 ID", example = "3", required = true)
    private Long searchProjectId;
}
