package com.dtstack.batch.web.task.vo.query;

import com.dtstack.batch.web.task.vo.query.BatchTaskTaskAddOrUpdateDependencyVO;
import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("任务血缘关系信息")
public class BatchTaskTaskAddOrUpdateVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "父任务列表", required = true)
    private List<BatchTaskTaskAddOrUpdateDependencyVO> dependencyVOS;
}
