package com.dtstack.batch.web.job.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("批量处理返回")
public class BatchOperatorResultVO<T> {

    @ApiModelProperty(value = "成功数量", example = "0")
    private Integer successNum = 0;

    @ApiModelProperty(value = "失败数量", example = "0")
    private Integer failNum = 0;

    @ApiModelProperty(value = "细节")
    private T detail;
}
