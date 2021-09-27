package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class BatchTaskGetByNameVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "任务名称", example = "dev", required = true)
    private String name;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;
    
}
