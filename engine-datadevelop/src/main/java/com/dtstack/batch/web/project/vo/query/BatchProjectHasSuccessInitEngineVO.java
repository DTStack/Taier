package com.dtstack.batch.web.project.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("成功初始化引擎信息")
public class BatchProjectHasSuccessInitEngineVO {
    @ApiModelProperty(value = "引擎类型", example = "1", required = true)
    private Integer engineType;

    @ApiModelProperty(hidden = true)
    private Long projectId;
}
