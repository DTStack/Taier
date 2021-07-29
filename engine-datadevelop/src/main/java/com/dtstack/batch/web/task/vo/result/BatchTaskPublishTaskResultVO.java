package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发布信息信息")
public class BatchTaskPublishTaskResultVO {

    @ApiModelProperty(value = "标识  0 无错误 1 权限校验错误 2 语法校验错误", example = "0")
    private Integer errorSign;

    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

}
