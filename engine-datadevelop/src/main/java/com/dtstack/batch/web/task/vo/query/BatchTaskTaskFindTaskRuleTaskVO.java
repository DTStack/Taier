package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据任务Id获取任务信息")
public class BatchTaskTaskFindTaskRuleTaskVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务Id", example = "1", required = true)
    private Long taskId;

    @ApiModelProperty(value = "产品类型", example = "1", required = true)
    private Integer appType;
}
