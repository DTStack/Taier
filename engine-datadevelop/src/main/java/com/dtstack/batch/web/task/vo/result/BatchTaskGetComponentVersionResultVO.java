package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("组件版本号")
public class BatchTaskGetComponentVersionResultVO {

    @ApiModelProperty(value = "版本号", example = "1.10")
    private String componentVersion;

    @ApiModelProperty(value = "是否默认版本", example = "true")
    private Boolean isDefault;
}
